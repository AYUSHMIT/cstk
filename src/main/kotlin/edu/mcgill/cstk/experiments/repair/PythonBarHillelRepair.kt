package edu.mcgill.cstk.experiments.repair

import NUM_CORES
import ai.hypergraph.kaliningraph.parsing.*
import ai.hypergraph.kaliningraph.tokenizeByWhitespace
import ai.hypergraph.kaliningraph.types.Π2A
import ai.hypergraph.kaliningraph.types.*
import edu.mcgill.cstk.utils.*
import java.io.File
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/*
./gradlew pythonBarHillelRepair
 */
fun main() {
  // Perfect recall on first 20 repairs takes ~7 minutes on a 2019 MacBook Pro
  val allRate = LBHMetrics()
  val levRates = mutableMapOf<Int, LBHMetrics>()
  val sampleTimeByLevDist = mutableMapOf(1 to 0.0, 2 to 0.0, 3 to 0.0)
  val allTimeByLevDist = mutableMapOf(1 to 0.0, 2 to 0.0, 3 to 0.0)
  val samplesBeforeMatchByLevDist = mutableMapOf(1 to 0.0, 2 to 0.0, 3 to 0.0)
//   val s2pg = vanillaS2PCFG // Original grammar, including all productions
  val s2pg = vanillaS2PCFGMinimized // Minimized grammar, with rare productions removed
//  assert(validLexedPythonStatements.lines().all { it in s2pg.language })
  val latestCommitMessage = lastGitMessage().replace(" ", "_")
  val positiveHeader = "length, lev_dist, sample_ms, total_ms, " +
    "total_samples, lev_ball_arcs, productions, rank, edit1, edit2, edit3\n"
  val negativeHeader = "length, lev_dist, samples, productions, edit1, edit2, edit3\n"
  val positive = try { File("bar_hillel_results_positive_$latestCommitMessage.csv") }
  catch (e: Exception) { File("/scratch/b/bengioy/breandan/bar_hillel_results_positive_$latestCommitMessage.csv") }
    .also { it.delete(); it.appendText(positiveHeader) }
  val negative = try { File("bar_hillel_results_negative_$latestCommitMessage.csv") }
  catch (e: Exception) { File("/scratch/b/bengioy/breandan/bar_hillel_results_negative_$latestCommitMessage.csv") }
    .also { it.delete(); it.appendText(negativeHeader) }

  val dataset = naturallySmallRepairs //pairwiseUniformAll
  println("Running Bar-Hillel repair on Python snippets with $NUM_CORES cores")
  dataset.first().second.let { P_BIFI.score("BOS NEWLINE $it EOS".tokenizeByWhitespace()) }
  println()

  dataset.shuffled(Random(1)).forEach { (invalid, valid) ->
    val allTime = TimeSource.Monotonic.markNow()
    val toRepair = "$invalid NEWLINE".tokenizeByWhitespace()
    val humanRepair = "$valid NEWLINE".tokenizeByWhitespace()
    val target = humanRepair.joinToString(" ")
    val source = toRepair.joinToString(" ")
    val levAlign = levenshteinAlign(toRepair, humanRepair)
    val levDist = levAlign.patchSize()

    var levBallSize = 1
    val humanRepairANSI = levenshteinAlign(toRepair, humanRepair).paintANSIColors()
    val intGram = try {
      s2pg.jvmIntersectLevFSA(
        makeLevFSA(toRepair, levDist).also { levBallSize = it.Q.size }
      ).also { intGram -> intGram.ifEmpty { null } }
    } catch (e: Exception) { null }

    println("Constructed LEV($levDist, ${toRepair.size}, $levBallSize) " +
      "∩ CFG grammar with ${intGram?.size ?: 0} productions in ${allTime.elapsedNow()}")

    try {
      if (intGram == null || humanRepair !in intGram.language)
          throw Exception("Human repair is unrecognizable!")
      else println("Human repair is recognized by LEV ∩ CFG grammar")
    } catch (e: Exception) {
      println("Encountered error (${e.message}): $humanRepairANSI")
      allRate.error++; levRates.getOrPut(levDist) { LBHMetrics() }.error++
      println(allRate.toString())
      negative.appendText("${toRepair.size}, $levDist, 0, " +
        "${levBallSize}, ${intGram?.size ?: 0}, ${levAlign.summarize()}\n")
      return@forEach
    }

    allRate.total++; levRates.getOrPut(levDist) { LBHMetrics() }.total++
    println("Ground truth repair: $humanRepairANSI")
    val clock = TimeSource.Monotonic.markNow()
    var samplesBeforeMatch = 0
    var matchFound = false
    val timeout = 60.seconds
    val results = mutableListOf<Σᐩ>()
    var elapsed = clock.elapsedNow().inWholeMilliseconds
    intGram.sampleDirectlyWR(stoppingCriterion = { clock.elapsedNow() < timeout })
      .distinct().forEach {
        results.add(it)
        samplesBeforeMatch++
        if (it == target) { matchFound = true; elapsed = clock.elapsedNow().inWholeMilliseconds }
      }

    if (!matchFound) {
      println("Drew $samplesBeforeMatch samples in $timeout," +
        " ${intGram.size} prods, length-$levDist human repair not found")
      negative.appendText("${toRepair.size}, $levDist, $samplesBeforeMatch, " +
        "${levBallSize}, ${intGram.size}, ${levAlign.summarize()}\n")
    } else {
      val allElapsed = allTime.elapsedNow().inWholeMilliseconds
      val rankedResults = results
        // Sort by Markov chain perplexity
        .map { it to P_BIFI.score(it.mapToBIFIFmt()) }
        .sortedBy { it.second }.map { it.first }
      // First sort by levenshtein distance, then by perplexity
//          .map { it to levenshtein(source, it) to P_BIFI.score(it.mapToBIFIFmt()) }
//          .sortedWith(compareBy({ it.second }, { it.third })).map { it.first }

      allRate.recall++; levRates.getOrPut(levDist) { LBHMetrics() }.recall++
      val indexOfTarget = rankedResults.indexOf(target)
        .also { if (it == 0) { allRate.top1++; levRates.getOrPut(levDist) { LBHMetrics() }.top1++ } }
      println("Found human repair (${clock.elapsedNow()}): $humanRepairANSI")
      println("Found length-$levDist repair in $elapsed ms, $allElapsed ms," +
        " $samplesBeforeMatch samples, ${intGram.size} prods, $indexOfTarget rank")//, rank: ${rankedResults.indexOf(target) + 1} / ${rankedResults.size}")
      allRate.run { println("Lev(*): $allRate") }; println(levRates.summarize())
      sampleTimeByLevDist[levDist] = sampleTimeByLevDist[levDist]!! + elapsed
      println("Draw timings (ms): ${sampleTimeByLevDist.mapValues { it.value / allRate.recall }}")
      allTimeByLevDist[levDist] = allTimeByLevDist[levDist]!! + allElapsed
      println("Full timings (ms): ${allTimeByLevDist.mapValues { it.value / allRate.recall }}")
      samplesBeforeMatchByLevDist[levDist] = samplesBeforeMatchByLevDist[levDist]!! + samplesBeforeMatch
      println("Avg samples drawn: ${samplesBeforeMatchByLevDist.mapValues { it.value / allRate.recall }}")
      positive.appendText("${toRepair.size}, $levDist, $elapsed, $allElapsed, " +
        "$samplesBeforeMatch, ${levBallSize}, ${intGram.size}, $indexOfTarget, ${levAlign.summarize()}\n")
    }

    println()
  }
}

fun Map<Int, LBHMetrics>.summarize() =
  entries.sortedBy { it.key }.joinToString("\n") { (k, v) -> "Lev($k): $v" }

data class LBHMetrics(var top1: Int = 0, var recall: Int = 0, var total: Int = 0, var error: Int = 0) {
  override fun toString() =
    "Top-1/rec/pos/total: $top1 / $recall / $total / ${total + error}, " +
      "errors: $error, P@1: ${top1.toDouble() / (total + error)}"
}

val naturallySmallRepairs: Sequence<Π2A<Σᐩ>> by lazy {
  val path = "/src/main/resources/datasets/python/stack_overflow/naturally_small_repairs.txt"
  val file = File(File("").absolutePath + path).readText()
  file.lines().asSequence().windowed(2, 2).map { it[0] to it[1] }
    .filter { (a, b) ->
      val broke = a.tokenizeByWhitespace()
      a.length < 60 && levenshtein(broke, b.tokenizeByWhitespace()) < 4
    }
}

fun Σᐩ.mapToBIFIFmt() =
  "BOS NEWLINE $this EOS".tokenizeByWhitespace()