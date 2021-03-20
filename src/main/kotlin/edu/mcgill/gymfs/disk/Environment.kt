package edu.mcgill.gymfs.disk

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree
import java.io.*
import java.net.URI
import java.nio.file.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.zip.*
import kotlin.io.path.*
import kotlin.time.*

@OptIn(ExperimentalTime::class)
class Grepper: CliktCommand() {
  val path by option("--path", help = "Root directory")
    .default(Paths.get("").toAbsolutePath().toString())

  val query by option("--query", help = "Query to find").default("match")

  val index by option("--index", help = "Prebuilt index file").default("")

  // Suffix trie multimap for (file, offset) pairs of matching prefixes
  val trie: ConcurrentSuffixTree<Queue<Location>>
    by lazy { buildOrLoadIndex(File(index), Path.of(path)) }

  fun search(query: String): List<Location> =
    trie.getValuesForKeysContaining(query).flatten()

  override fun run() {
    println("\nSearching index of size ${trie.size()} for [?]=[$query]…\n")

    measureTimedValue { search(query) }.let { (res, time) ->
      res.take(10).forEachIndexed { i, it ->
        println("$i.) ${previewResult(query, it)}")
        val nextLocations = it.expand(this@Grepper)
        println("Next locations:")
        nextLocations.forEachIndexed { index, (query, loc) ->
          println("\t$index.) ${previewResult(query, loc)}")
        }
        println()
      }
      println("\nFound ${res.size} results in $time")
    }
  }

  private fun previewResult(query: String, loc: Location) =
    "[?=$query] ${loc.getContext(0).preview(query)}\t($loc)"
}

@OptIn(ExperimentalPathApi::class)
data class Location(val file: URI, val line: Int): Serializable {
  fun getContext(surroundingLines: Int) =
    Files.newBufferedReader(file.toPath()).use {
      it.lineSequence()
        .drop((line - surroundingLines).coerceAtLeast(0))
        .take(surroundingLines + 1).joinToString("\n")
    }

  private fun topKeywordsFromContext(
    mostKeywordsToTake: Int = 5,
    score: (String) -> Double
  ): List<Pair<String, Double>> =
    getContext(3).split(Regex("[^\\w']+"))
    .asSequence()
    .filter(String::isNotEmpty)
    .distinct()
    .map { it to score(it) }
    .filter { (_, score) -> 1.0 != score } // Score of 1.0 is the current loc
    .sortedBy { it.second }
    .take(mostKeywordsToTake)
    .toList()

  /*
   * Expand keywords by least common first. Common keywords are
   * unlikely to contain any useful information.
   *
   * TODO: Reweight score by other metrics?
   */

  fun expand(grepper: Grepper): List<Pair<String, Location>> =
    topKeywordsFromContext { grepper.search(it).size.toDouble() }
      .also { println("Keyword scores: $it") }
      .map { (kw, _) ->
        grepper.search(kw)
          .filter { it != this }
          .take(5).map { kw to it }
      }.flatten()

  override fun toString() = "…${file.path.substringAfterLast('/')}:L${line + 1}"
}

@OptIn(ExperimentalTime::class)
fun buildOrLoadIndex(
  index: File,
  rootDir: Path
): ConcurrentSuffixTree<Queue<Location>> =
  if (!index.exists())
    measureTimedValue { indexPath(rootDir.also { println("Indexing $it") }) }
      .let { (trie, time) ->
        val file = if (index.name.isEmpty()) File("gymfs.idx") else index
        trie.also { it.serialize(file); println("Indexed in $time to: $file") }
      }
  else {
    println("Loading index from ${index.absolutePath}")
    deserialize(index) as ConcurrentSuffixTree<Queue<Location>>
  }

// Indexes all lines in all files in the path
fun indexPath(rootDir: Path): ConcurrentSuffixTree<Queue<Location>> =
  ConcurrentSuffixTree<Queue<Location>>(DefaultCharArrayNodeFactory())
    .also { trie ->
      rootDir.allFilesRecursively().parallelStream().forEach { src ->
        try {
          Files.readAllLines(src).forEachIndexed { lineIndex, line ->
            if (line.length < 500)
              ConcurrentLinkedQueue(listOf(Location(src.toUri(), lineIndex)))
                .let { trie.putIfAbsent(line, it)?.offer(it.first()) }
          }
        } catch (e: Exception) {
//        System.err.println("Unreadable …${src.fileName} due to ${e.message}")
        }
      }
    }

fun Any?.serialize(path: File) =
  ObjectOutputStream(GZIPOutputStream(FileOutputStream(path))).use { it.writeObject(this) }

fun deserialize(file: File): Any =
  ObjectInputStream(GZIPInputStream(FileInputStream(file))).use { it.readObject() }

fun main(args: Array<String>) = Grepper().main(args)