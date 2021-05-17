import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.JavaVersion.VERSION_11
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE

plugins {
  val kotlinVersion = "1.5.0"
  kotlin("jvm") version kotlinVersion
  id("com.github.ben-manes.versions") version "0.38.0"
//  kotlin("plugin.serialization") version kotlinVersion
  id("de.undercouch.download") version "4.1.1"
}

group = "com.github.breandan"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
  maven("https://jetbrains.bintray.com/lets-plot-maven")
}

dependencies {
  implementation(platform(kotlin("bom")))
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))

  // String index
  implementation("com.googlecode.concurrent-trees:concurrent-trees:2.6.1")

  implementation("org.slf4j:slf4j-simple:1.7.30")

//  implementation("ai.djl.tensorflow:tensorflow-engine:0.11.0")
//  implementation("ai.djl.tensorflow:tensorflow-native-cu101:2.3.1")
//  implementation("ai.djl:examples:0.6.0")

  implementation("ai.djl:api:0.11.0")
  implementation("ai.djl.mxnet:mxnet-engine:0.11.0")
  implementation("ai.djl.mxnet:mxnet-native-cu102mkl:1.7.0-backport")
  implementation("ai.djl.fasttext:fasttext-engine:0.11.0")
  implementation("ai.djl.sentencepiece:sentencepiece:0.11.0")
  implementation("ai.djl.mxnet:mxnet-model-zoo:0.11.0")
  implementation("ai.djl:model-zoo:0.11.0")

  // Vector embedding index
  val hnswlibVersion = "0.0.46"
  implementation("com.github.jelmerk:hnswlib-core:$hnswlibVersion")
  implementation("com.github.jelmerk:hnswlib-utils:$hnswlibVersion")

  val multikVersion = "0.0.1"
  implementation("org.jetbrains.kotlinx:multik-api:$multikVersion")
  implementation("org.jetbrains.kotlinx:multik-default:$multikVersion")

  // String comparison metrics
  implementation("info.debatty:java-string-similarity:2.0.0")

  // CLI parser
  implementation("com.github.ajalt.clikt:clikt:3.2.0")

  implementation("org.jetbrains.lets-plot-kotlin:lets-plot-kotlin-api:1.3.0")
  implementation("com.github.breandan.T-SNE-Java:tsne:master-SNAPSHOT")
  implementation("com.github.breandan:kaliningraph:0.1.6")

  // https://github.com/LearnLib/learnlib
  implementation("de.learnlib.distribution:learnlib-distribution:0.16.0")
  // https://github.com/LearnLib/automatalib
  implementation("net.automatalib.distribution:automata-distribution:0.10.0")

  // RegEx to DFA conversion
  // https://github.com/cs-au-dk/dk.brics.automaton
  implementation("dk.brics:automaton:1.12-1")

  // Querying and filtering data from GitHub
  implementation("org.kohsuke:github-api:1.128")

  // Read compressed repositories downloaded from GitHub
  implementation("org.apache.commons:commons-compress:1.20")
  implementation("org.apache.commons:commons-vfs2:2.8.0")

  // Constraint minimization for Kantorovich-Rubenstein distance
  val ortoolsVersion = "9.0.9048"
  implementation("com.google.ortools:ortools-java:$ortoolsVersion")
  implementation("com.google.ortools:ortools-linux-x86-64:$ortoolsVersion")

  // DFA to RegEx conversion
  // https://github.com/LearnLib/learnlib/issues/75
  // http://www.jflap.org/modules/ConvertedFiles/DFA%20to%20Regular%20Expression%20Conversion%20Module.pdf
  // https://github.com/LakshmiAntin/JFLAPEnhanced/blob/cbb1e6a52f44c826fcb082c85cba9e5f09dcdb33/gui/action/ArdenLemma.java
  // implementation("com.github.citiususc:jflap-lib:1.3")
}

tasks {
  register("getGrex", Download::class) {
    onlyIf { !File("grex").exists() }
    val name = "grex-v1.2.0-x86_64-unknown-linux-musl.tar.gz"
    src("https://github.com/pemistahl/grex/releases/download/v1.2.0/$name")
    dest(File(name))
    overwrite(false)

    doLast {
      copy {
        from(tarTree(resources.gzip(name)))
        into(projectDir)
      }
    }
  }

  mapOf(
    "trieSearch" to "edu.mcgill.gymfs.disk.KWSearchKt",
    "knnSearch" to "edu.mcgill.gymfs.disk.KNNSearchKt",
    "cloneRepos" to "edu.mcgill.gymfs.github.CloneReposKt",
    "filterRepos" to "edu.mcgill.gymfs.github.FilterReposKt",
    "trainBert" to "edu.mcgill.gymfs.agent.BertTrainerKt",
    "indexKW" to "edu.mcgill.gymfs.indices.KWIndexKt",
    "indexKNN" to "edu.mcgill.gymfs.indices.VecIndexKt",
    "querySynth" to "edu.mcgill.gymfs.experiments.DFASynthesizerKt",
    "compareMetrics" to "edu.mcgill.gymfs.experiments.CompareMetricsKt",
    "nearestNeighbors" to "edu.mcgill.gymfs.experiments.NearestNeighborsKt",
  ).forEach { (cmd, mainClass) ->
    register(cmd, JavaExec::class) {
      main = mainClass
      classpath = sourceSets["main"].runtimeClasspath
    }
  }

  compileKotlin {
    kotlinOptions.jvmTarget = VERSION_11.toString()
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
  }

  jar {
    manifest.attributes["Main-Class"] = "edu.mcgill.gymfs.agent.BertTrainerKt"

    from(configurations.compileClasspath.get().files
      .filter { it.extension != "pom" }
      .map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = EXCLUDE
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/*.SF")
    archiveBaseName.set("${project.name}-fat")
  }
}