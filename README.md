# gym-fs

A fast, RL environment for the filesystem.

Stores BPE-compressed files in memory.

Gives an agent the ability to selectively read files.

Interface:

* `Path.read(start, end)` - Returns file chunk at offset.
* `Path.grep(query)` - Returns offsets matching query.

# Usage

Keyword search:

`./gradlew -q trieSearch --args='--query=<QUERY> [--path=<PATH_TO_INDEX>] [--index=<INDEX_FILE>]'`

For example:

```
$ ./gradlew -q trieSearch
Indexing /home/breandan/IdeaProjects/gym-fs
Indexed in 524ms to: gymfs.idx

Searching index of size 1227 for [?]=[match]…

0.) [?=match] ….default("[?]")… (…Environment.kt:L21)
Keyword scores: [(toAbsolutePath, 2.0), (Query, 2.0), (find, 2.0)]
Next locations:
        0.) [?=toAbsolutePath] …ath = src.[?]().toStrin…        (…DiskUtils.kt:L21)
        1.) [?=toAbsolutePath] …s.get("").[?]().toStrin…        (…Environment.kt:L19)
        2.) [?=Query] …// [?] in contex…        (…StringUtils.kt:L7)
        3.) [?=find] …ex(query).[?]All(this).…  (…StringUtils.kt:L36)

1.) [?=match] …val ([?]Start, mat…tchStart, [?]End) =…  (…StringUtils.kt:L38)
Keyword scores: [(Regex, 2.0), (matchStart, 2.0), (matchEnd, 2.0)]
Next locations:
        0.) [?=Regex] …(3).split([?]("[^\\w']+… (…Environment.kt:L66)
        1.) [?=Regex] …[?](query).fi…   (…StringUtils.kt:L36)
        2.) [?=matchStart] …substring([?], matchEnd…chEnd) to [?]…      (…StringUtils.kt:L40)
        3.) [?=matchEnd] …tchStart, [?]) to match…      (…StringUtils.kt:L40)

2.) [?=match] …substring([?]Start, mat…tchStart, [?]End) to ma…chEnd) to [?]Start…      (…StringUtils.kt:L40)
Keyword scores: [(matchStart, 2.0), (matchEnd, 2.0), (first, 3.0)]
Next locations:
        0.) [?=matchStart] …val ([?], matchEnd… (…StringUtils.kt:L38)
        1.) [?=matchEnd] …tchStart, [?]) =…     (…StringUtils.kt:L38)
        2.) [?=first] ….offer(it.[?]()) }…      (…Environment.kt:L120)
        3.) [?=first] …st common [?]. Common k… (…Environment.kt:L77)
        4.) [?=first] …it.range.[?].coerceIn(…  (…StringUtils.kt:L39)

3.) [?=match] …pairs of [?]ing prefix…  (…Environment.kt:L25)
Keyword scores: [(offset, 2.0), (pairs, 2.0), (help, 3.0)]
Next locations:
        0.) [?=offset] …val [?]: Int…   (…StringUtils.kt:L12)
        1.) [?=pairs] …sentence [?] containin…  (…BertTrainer.kt:L112)
        2.) [?=help] …--index", [?] = "Prebui…  (…Environment.kt:L23)
        3.) [?=help] …--query", [?] = "Query…   (…Environment.kt:L21)
        4.) [?=help] …"--path", [?] = "Root d…  (…Environment.kt:L18)


Found 4 results in 2.82ms
```

Nearest neighbor search:

`./gradlew -q knnSearch --args='--query=<QUERY> [--path=<PATH_TO_INDEX>] [--index=<INDEX_FILE>] [--graphs=10]'`

For example:

```
./gradlew -q knnSearch --args='--query="const val MAX_GPUS = 1"'

Searching KNN index of size 981 for [?]=[const val MAX_GPUS = 1]…

0.) const val MAX_GPUS = 1
1.) const val MAX_BATCH = 50
2.) const val MAX_VOCAB = 35000
3.) const val EPOCHS = 100000
4.) const val BATCH_SIZE = 24
5.) const val MAX_SEQUENCE_LENGTH = 128
6.) const val CLS = "<cls>"
7.) dataSize.toLong()
8.) const val BERT_EMBEDDING_SIZE = 768
9.) const val UNK = "<unk>"

Fetched nearest neighbors in 1.48674ms

|-----> Original index before reranking by MetricLCS
|    |-----> Current index after reranking by MetricLCS
|    |
  0->0.) const val MAX_GPUS = 1
  1->1.) const val MAX_BATCH = 50
 14->2.) const val MSK = "<msk>"
  3->3.) const val EPOCHS = 100000
  4->4.) const val BATCH_SIZE = 24
  2->5.) const val MAX_VOCAB = 35000
363->6.) ).default("const val MAX_GPUS = 1")
  6->7.) const val CLS = "<cls>"
  9->8.) const val UNK = "<unk>"
 16->9.) const val SEP = "<sep>"

Reranked nearest neighbors in 1.412775ms
```

# Semantic Similarity

What do nearest neighbors share in common?

<details>

```
Nearest nearest neighbors by cumulative similarity
Angle brackets enclose longest common substring up to current result

0.] dataSize.toLong()
	0.0] executorService.shutdownNow()
	0.1] PolynomialDecayTracker.builder《()》
	0.2] .toLabeledGraph《()》
	0.3] WarmUpTracker.builder《()》
	0.4] .allCodeFragments《()》
	0.5] .toTypedArray《()》
	0.6] batchData: TrainingListener.BatchData
	0.7] .asSequence()
	0.8] .shuffled()
	0.9] .readText().lines()
	0.10] vocabSize
	0.11] .toList()
	0.12] .distinct()
	0.13] PaddingStackBatchifier.builder()
	0.14] return trainer.trainingResult
	0.15] Adam.builder()
	0.16] return jfsRoot
	0.17] createOrLoadModel()
	0.18] sentenceA = otherA
	0.19] const val MAX_GPUS = 1


1.] .toLabeledGraph()
	1.0] .toTypedArray()
	1.1] 《.to》List()
	1.2] .asSequence《()》
	1.3] .allCodeFragments《()》
	1.4] .renderVKG《()》
	1.5] .shuffled《()》
	1.6] .distinct《()》
	1.7] dataSize.toLong《()》
	1.8] .readText《()》.lines《()》
	1.9] PolynomialDecayTracker.builder《()》
	1.10] WarmUpTracker.builder《()》
	1.11] .show《()》
	1.12] .readText《()》
	1.13] Adam.builder《()》
	1.14] .allFilesRecursively《()》
	1.15] executorService.shutdownNow《()》
	1.16] .build《()》
	1.17] .first《()》.toDoubleArray《()》
	1.18] PaddingStackBatchifier.builder《()》
	1.19] .optLimit(100)


2.] .shuffled()
	2.0] .distinct()
	2.1] .renderVKG《()》
	2.2] .toLabeledGraph《()》
	2.3] .show《()》
	2.4] .toTypedArray《()》
	2.5] .toList《()》
	2.6] .asSequence《()》
	2.7] .allCodeFragments《()》
	2.8] .build《()》
	2.9] dataSize.toLong《()》
	2.10] .readText《()》.lines《()》
	2.11] PolynomialDecayTracker.builder《()》
	2.12] WarmUpTracker.builder《()》
	2.13] .allFilesRecursively《()》
	2.14] .first《()》.toDoubleArray《()》
	2.15] executorService.shutdownNow《()》
	2.16] .readText《()》
	2.17] PaddingStackBatchifier.builder《()》
	2.18] trainer.metrics = Metrics《()》
	2.19] Adam.builder《()》


3.] .toList()
	3.0] .toTypedArray()
	3.1] 《.to》LabeledGraph()
	3.2] .distinct《()》
	3.3] .asSequence《()》
	3.4] .shuffled《()》
	3.5] .readText《()》.lines《()》
	3.6] .allCodeFragments《()》
	3.7] .show《()》
	3.8] .allFilesRecursively《()》
	3.9] dataSize.toLong《()》
	3.10] .renderVKG《()》
	3.11] .readText《()》
	3.12] .build《()》
	3.13] WarmUpTracker.builder《()》
	3.14] .first《()》.toDoubleArray《()》
	3.15] PolynomialDecayTracker.builder《()》
	3.16] executorService.shutdownNow《()》
	3.17] trainer.metrics = Metrics《()》
	3.18] Adam.builder《()》
	3.19] .optLimit(100)


4.] PolynomialDecayTracker.builder()
	4.0] WarmUpTracker.builder()
	4.1] PaddingStackBatchifi《er.builder()》
	4.2] dataSize.toLong《()》
	4.3] TrainBertOnCode.runExample《()》
	4.4] executorService.shutdownNow《()》
	4.5] trainer.metrics = Metrics《()》
	4.6] .shuffled《()》
	4.7] .toLabeledGraph《()》
	4.8] .toTypedArray《()》
	4.9] .distinct《()》
	4.10] createOrLoadModel《()》
	4.11] Activation.relu(it)
	4.12] .renderVKG()
	4.13] batchData: TrainingListener.BatchData
	4.14] else rebuildIndex()
	4.15] .allCodeFragments()
	4.16] return jfsRoot
	4.17] .asSequence()
	4.18] .toList()
	4.19] vocabSize


5.] .distinct()
	5.0] .shuffled()
	5.1] 《.sh》ow()
	5.2] .toList《()》
	5.3] .toLabeledGraph《()》
	5.4] .renderVKG《()》
	5.5] .build《()》
	5.6] .asSequence《()》
	5.7] .toTypedArray《()》
	5.8] dataSize.toLong《()》
	5.9] .readText《()》.lines《()》
	5.10] .allCodeFragments《()》
	5.11] PolynomialDecayTracker.builder《()》
	5.12] WarmUpTracker.builder《()》
	5.13] Adam.builder《()》
	5.14] .allFilesRecursively《()》
	5.15] .readText《()》
	5.16] executorService.shutdownNow《()》
	5.17] trainer.metrics = Metrics《()》
	5.18] createOrLoadModel《()》
	5.19] printQuery《()》


6.] WarmUpTracker.builder()
	6.0] PolynomialDecayTracker.builder()
	6.1] PaddingStackBatchifi《er.builder()》
	6.2] TrainBertOnCode.runExample《()》
	6.3] dataSize.toLong《()》
	6.4] trainer.metrics = Metrics《()》
	6.5] executorService.shutdownNow《()》
	6.6] .shuffled《()》
	6.7] .toTypedArray《()》
	6.8] .distinct《()》
	6.9] .toLabeledGraph《()》
	6.10] Activation.relu(it)
	6.11] .toList()
	6.12] .renderVKG()
	6.13] else rebuildIndex()
	6.14] .asSequence()
	6.15] createOrLoadModel()
	6.16] batchData: TrainingListener.BatchData
	6.17] .allCodeFragments()
	6.18] .readText().lines()
	6.19] TextTerminator()


7.] .toTypedArray()
	7.0] .toLabeledGraph()
	7.1] 《.toL》ist()
	7.2] .asSequence《()》
	7.3] .shuffled《()》
	7.4] .allCodeFragments《()》
	7.5] dataSize.toLong《()》
	7.6] .distinct《()》
	7.7] .renderVKG《()》
	7.8] WarmUpTracker.builder《()》
	7.9] PolynomialDecayTracker.builder《()》
	7.10] .readText《()》.lines《()》
	7.11] .allFilesRecursively《()》
	7.12] .first《()》.toDoubleArray《()》
	7.13] .readText《()》
	7.14] executorService.shutdownNow《()》
	7.15] .show《()》
	7.16] PaddingStackBatchifier.builder《()》
	7.17] trainer.metrics = Metrics《()》
	7.18] .build《()》
	7.19] TrainBertOnCode.runExample《()》


8.] const val MAX_BATCH = 50
	8.0] const val MAX_VOCAB = 35000
	8.1] 《const val MAX_》GPUS = 1
	8.2] 《const val 》EPOCHS = 100000
	8.3] 《const val 》MAX_SEQUENCE_LENGTH = 128
	8.4] 《const val 》BATCH_SIZE = 24
	8.5] 《const val 》CLS = "<cls>"
	8.6] 《const val 》UNK = "<unk>"
	8.7] 《const val 》BERT_EMBEDDING_SIZE = 768
	8.8] dataSize.toL《on》g()
	8.9] val targetEmbedding =
	8.10] const val MSK = "<msk>"
	8.11] val use = UniversalSentenceEncoder
	8.12] sentenceA = otherA
	8.13] const val CODEBERT_CLS_TOKEN = "<s>"
	8.14] const val SEP = "<sep>"
	8.15] val d2vecs = vectors.reduceDim()
	8.16] return jfsRoot
	8.17] val range = 0..length
	8.18] val (matchStart, matchEnd) =
	8.19] PolynomialDecayTracker.builder()


9.] .renderVKG()
	9.0] .toLabeledGraph()
	9.1] .shuff《led》()
	9.2] .allCodeFragments《()》
	9.3] .distinct《()》
	9.4] .show《()》
	9.5] .toTypedArray《()》
	9.6] .toList《()》
	9.7] .readText《()》.lines《()》
	9.8] .build《()》
	9.9] dataSize.toLong《()》
	9.10] PolynomialDecayTracker.builder《()》
	9.11] WarmUpTracker.builder《()》
	9.12] .readText《()》
	9.13] .asSequence《()》
	9.14] .allFilesRecursively《()》
	9.15] Adam.builder《()》
	9.16] printQuery《()》
	9.17] createOrLoadModel《()》
	9.18] TrainBertOnCode.runExample《()》
	9.19] PaddingStackBatchifier.builder《()》


10.] .readText().lines()
	10.0] .readText()
	10.1] .toLabeledGraph《()》
	10.2] .toList《()》
	10.3] dataSize.toLong《()》
	10.4] .shuffled《()》
	10.5] .allCodeFragments《()》
	10.6] path.readText《()》.lines《()》
	10.7] .distinct《()》
	10.8] .renderVKG《()》
	10.9] .toTypedArray《()》
	10.10] .allFilesRecursively《()》
	10.11] .asSequence《()》
	10.12] .show《()》
	10.13] executorService.shutdownNow《()》
	10.14] WarmUpTracker.builder《()》
	10.15] Adam.builder《()》
	10.16] .build《()》
	10.17] PolynomialDecayTracker.builder《()》
	10.18] .first《()》.toDoubleArray《()》
	10.19] trainer.metrics = Metrics《()》


11.] .show()
	11.0] .build()
	11.1] .distinct《()》
	11.2] .shuffled《()》
	11.3] .toList《()》
	11.4] Adam.builder《()》
	11.5] .renderVKG《()》
	11.6] printQuery《()》
	11.7] .toLabeledGraph《()》
	11.8] TextTerminator《()》
	11.9] .readText《()》
	11.10] println《()》
	11.11] createOrLoadModel《()》
	11.12] .readText《()》.lines《()》
	11.13] else rebuildIndex《()》
	11.14] .toTypedArray《()》
	11.15] WarmUpTracker.builder《()》
	11.16] dataSize.toLong《()》
	11.17] .allCodeFragments《()》
	11.18] PolynomialDecayTracker.builder《()》
	11.19] }.toList《()》


12.] const val MAX_VOCAB = 35000
	12.0] const val MAX_BATCH = 50
	12.1] 《const val 》EPOCHS = 100000
	12.2] 《const val 》MAX_GPUS = 1
	12.3] 《const val 》MAX_SEQUENCE_LENGTH = 128
	12.4] 《const val 》CLS = "<cls>"
	12.5] 《const val 》BATCH_SIZE = 24
	12.6] 《const val 》UNK = "<unk>"
	12.7] 《const val 》MSK = "<msk>"
	12.8] 《const val 》BERT_EMBEDDING_SIZE = 768
	12.9] dataSize.toL《on》g()
	12.10] val d2vecs = vectors.reduceDim()
	12.11] const val SEP = "<sep>"
	12.12] val vocab = SimpleVocabulary.builder()
	12.13] val use = UniversalSentenceEncoder
	12.14] const val CODEBERT_CLS_TOKEN = "<s>"
	12.15] val targetEmbedding =
	12.16] sentenceA = otherA
	12.17] return jfsRoot
	12.18] val r = rand.nextFloat()
	12.19] PolynomialDecayTracker.builder()


13.] .allCodeFragments()
	13.0] .toLabeledGraph()
	13.1] .renderVKG《()》
	13.2] .toTypedArray《()》
	13.3] .allFilesRecursively《()》
	13.4] .toList《()》
	13.5] .shuffled《()》
	13.6] dataSize.toLong《()》
	13.7] .readText《()》.lines《()》
	13.8] .asSequence《()》
	13.9] .distinct《()》
	13.10] PolynomialDecayTracker.builder《()》
	13.11] .readText《()》
	13.12] WarmUpTracker.builder《()》
	13.13] executorService.shutdownNow《()》
	13.14] Adam.builder《()》
	13.15] .show《()》
	13.16] .build《()》
	13.17] .optLimit(100)
	13.18] .optBatchFirst(true)
	13.19] PaddingStackBatchifier.builder()


14.] const val MAX_GPUS = 1
	14.0] const val MAX_BATCH = 50
	14.1] 《const val MAX_》VOCAB = 35000
	14.2] 《const val 》EPOCHS = 100000
	14.3] 《const val 》BATCH_SIZE = 24
	14.4] 《const val 》MAX_SEQUENCE_LENGTH = 128
	14.5] 《const val 》CLS = "<cls>"
	14.6] dataSize.toL《on》g()
	14.7] c《on》st val BERT_EMBEDDING_SIZE = 768
	14.8] c《on》st val UNK = "<unk>"
	14.9] c《on》st val CODEBERT_CLS_TOKEN = "<s>"
	14.10] val targetEmbedding =
	14.11] val use = UniversalSentenceEncoder
	14.12] sentenceA = otherA
	14.13] const val MSK = "<msk>"
	14.14] val (matchStart, matchEnd) =
	14.15] const val SEP = "<sep>"
	14.16] return jfsRoot
	14.17] return trainer.trainingResult
	14.18] var numEpochs = 0
	14.19] PolynomialDecayTracker.builder()


15.] createOrLoadModel()
	15.0] printQuery()
	15.1] TextTerminator《()》
	15.2] else rebuildIndex《()》
	15.3] println《()》
	15.4] TrainBertOnCode.runExample《()》
	15.5] dataSize.toLong《()》
	15.6] PolynomialDecayTracker.builder《()》
	15.7] executorService.shutdownNow《()》
	15.8] return trainer.trainingResult
	15.9] WarmUpTracker.builder()
	15.10] }.toList()
	15.11] .show()
	15.12] PaddingStackBatchifier.builder()
	15.13] add(CLS)
	15.14] .build()
	15.15] Adam.builder()
	15.16] vocabSize
	15.17] .distinct()
	15.18] sentenceA = otherA
	15.19] .shuffled()


16.] vocabSize
	16.0] return trainer.trainingResult
	16.1] 《return 》jfsRoot
	16.2] 《return 》dataset
	16.3] Adam.builder()
	16.4] dataSize.toLong()
	16.5] rootDir: Path
	16.6] sentenceA = otherA
	16.7] val offset: Int
	16.8] list: NDList
	16.9] batchData: TrainingListener.BatchData
	16.10] TextTerminator()
	16.11] executorService.shutdownNow()
	16.12] PolynomialDecayTracker.builder()
	16.13] vocabSize: Long
	16.14] createOrLoadModel()
	16.15] PunctuationSeparator(),
	16.16] TextTruncator(10)
	16.17] Batchifier.STACK,
	16.18] add(CLS)
	16.19] PaddingStackBatchifier.builder()


17.] const val EPOCHS = 100000
	17.0] const val MAX_VOCAB = 35000
	17.1] 《const val MAX_》BATCH = 50
	17.2] 《const val MAX_》GPUS = 1
	17.3] 《const val MAX_》SEQUENCE_LENGTH = 128
	17.4] 《const val 》CLS = "<cls>"
	17.5] 《const val 》BATCH_SIZE = 24
	17.6] 《const val 》UNK = "<unk>"
	17.7] 《const val 》MSK = "<msk>"
	17.8] 《const val 》SEP = "<sep>"
	17.9] 《const val 》BERT_EMBEDDING_SIZE = 768
	17.10] 《val 》targetEmbedding =
	17.11] dataSize.toLong()
	17.12] val use = UniversalSentenceEncoder
	17.13] const val CODEBERT_CLS_TOKEN = "<s>"
	17.14] val d2vecs = vectors.reduceDim()
	17.15] val vocab = SimpleVocabulary.builder()
	17.16] var consecutive = true
	17.17] val knn = knnIndex.findNearest(v, topK)
	17.18] sentenceA = otherA
	17.19] val r = rand.nextFloat()


18.] Adam.builder()
	18.0] return dataset
	18.1] .show()
	18.2] vocabSize
	18.3] TextTerminator()
	18.4] dataSize.toLong()
	18.5] return trainer.trainingResult
	18.6] .build()
	18.7] .distinct()
	18.8] .toLabeledGraph()
	18.9] add(SEP)
	18.10] createOrLoadModel()
	18.11] PolynomialDecayTracker.builder()
	18.12] consecutive = false
	18.13] executorService.shutdownNow()
	18.14] val offset: Int
	18.15] .shuffled()
	18.16] .readText().lines()
	18.17] WarmUpTracker.builder()
	18.18] } else {
	18.19] add(CLS)


19.] package edu.mcgill.gymfs.agent
	19.0] package edu.mcgill.gymfs.experiments
	19.1] 《package edu.mcgill.gymfs.》inference
	19.2] 《package edu.mcgill.gymfs.》disk
	19.3] 《package edu.mcgill.gymfs》
	19.4] import jetbrains.letsPlot.labe《l.g》gtitle
	19.5] import edu.mcgil《l.g》ymfs.disk.*
	19.6] import com《.g》ithub.jelmerk.knn.SearchResult
	19.7] import jetbrains.datalore.plot.*
	19.8] import edu.mcgill.kaliningraph.*
	19.9] import jetbrains.letsPlot.intern.*
	19.10] import com.jujutsu.tsne.TSne
	19.11] import com.jujutsu.utils.TSneUtils
	19.12] import org.nield.kotlinstatistics.variance
	19.13] import com.github.jelmerk.knn.*
	19.14] import jetbrains.letsPlot.*
	19.15] import edu.mcgill.kaliningraph.show
	19.16] import guru.nidi.graphviz.*
	19.17] import kotlin.math.pow
	19.18] import kotlin.system.measureTimeMillis
	19.19] import org.slf4j.LoggerFactorNearest nearest neighbors by cumulative similarity
Angle brackets enclose longest common substring up to current result

0.] dataSize.toLong()
	0.0] executorService.shutdownNow()
	0.1] PolynomialDecayTracker.builder《()》
	0.2] .toLabeledGraph《()》
	0.3] WarmUpTracker.builder《()》
	0.4] .allCodeFragments《()》
	0.5] .toTypedArray《()》
	0.6] batchData: TrainingListener.BatchData
	0.7] .asSequence()
	0.8] .shuffled()
	0.9] .readText().lines()
	0.10] vocabSize
	0.11] .toList()
	0.12] .distinct()
	0.13] PaddingStackBatchifier.builder()
	0.14] return trainer.trainingResult
	0.15] Adam.builder()
	0.16] return jfsRoot
	0.17] createOrLoadModel()
	0.18] sentenceA = otherA
	0.19] const val MAX_GPUS = 1


1.] .toLabeledGraph()
	1.0] .toTypedArray()
	1.1] 《.to》List()
	1.2] .asSequence《()》
	1.3] .allCodeFragments《()》
	1.4] .renderVKG《()》
	1.5] .shuffled《()》
	1.6] .distinct《()》
	1.7] dataSize.toLong《()》
	1.8] .readText《()》.lines《()》
	1.9] PolynomialDecayTracker.builder《()》
	1.10] WarmUpTracker.builder《()》
	1.11] .show《()》
	1.12] .readText《()》
	1.13] Adam.builder《()》
	1.14] .allFilesRecursively《()》
	1.15] executorService.shutdownNow《()》
	1.16] .build《()》
	1.17] .first《()》.toDoubleArray《()》
	1.18] PaddingStackBatchifier.builder《()》
	1.19] .optLimit(100)


2.] .shuffled()
	2.0] .distinct()
	2.1] .renderVKG《()》
	2.2] .toLabeledGraph《()》
	2.3] .show《()》
	2.4] .toTypedArray《()》
	2.5] .toList《()》
	2.6] .asSequence《()》
	2.7] .allCodeFragments《()》
	2.8] .build《()》
	2.9] dataSize.toLong《()》
	2.10] .readText《()》.lines《()》
	2.11] PolynomialDecayTracker.builder《()》
	2.12] WarmUpTracker.builder《()》
	2.13] .allFilesRecursively《()》
	2.14] .first《()》.toDoubleArray《()》
	2.15] executorService.shutdownNow《()》
	2.16] .readText《()》
	2.17] PaddingStackBatchifier.builder《()》
	2.18] trainer.metrics = Metrics《()》
	2.19] Adam.builder《()》


3.] .toList()
	3.0] .toTypedArray()
	3.1] 《.to》LabeledGraph()
	3.2] .distinct《()》
	3.3] .asSequence《()》
	3.4] .shuffled《()》
	3.5] .readText《()》.lines《()》
	3.6] .allCodeFragments《()》
	3.7] .show《()》
	3.8] .allFilesRecursively《()》
	3.9] dataSize.toLong《()》
	3.10] .renderVKG《()》
	3.11] .readText《()》
	3.12] .build《()》
	3.13] WarmUpTracker.builder《()》
	3.14] .first《()》.toDoubleArray《()》
	3.15] PolynomialDecayTracker.builder《()》
	3.16] executorService.shutdownNow《()》
	3.17] trainer.metrics = Metrics《()》
	3.18] Adam.builder《()》
	3.19] .optLimit(100)


4.] PolynomialDecayTracker.builder()
	4.0] WarmUpTracker.builder()
	4.1] PaddingStackBatchifi《er.builder()》
	4.2] dataSize.toLong《()》
	4.3] TrainBertOnCode.runExample《()》
	4.4] executorService.shutdownNow《()》
	4.5] trainer.metrics = Metrics《()》
	4.6] .shuffled《()》
	4.7] .toLabeledGraph《()》
	4.8] .toTypedArray《()》
	4.9] .distinct《()》
	4.10] createOrLoadModel《()》
	4.11] Activation.relu(it)
	4.12] .renderVKG()
	4.13] batchData: TrainingListener.BatchData
	4.14] else rebuildIndex()
	4.15] .allCodeFragments()
	4.16] return jfsRoot
	4.17] .asSequence()
	4.18] .toList()
	4.19] vocabSize


5.] .distinct()
	5.0] .shuffled()
	5.1] 《.sh》ow()
	5.2] .toList《()》
	5.3] .toLabeledGraph《()》
	5.4] .renderVKG《()》
	5.5] .build《()》
	5.6] .asSequence《()》
	5.7] .toTypedArray《()》
	5.8] dataSize.toLong《()》
	5.9] .readText《()》.lines《()》
	5.10] .allCodeFragments《()》
	5.11] PolynomialDecayTracker.builder《()》
	5.12] WarmUpTracker.builder《()》
	5.13] Adam.builder《()》
	5.14] .allFilesRecursively《()》
	5.15] .readText《()》
	5.16] executorService.shutdownNow《()》
	5.17] trainer.metrics = Metrics《()》
	5.18] createOrLoadModel《()》
	5.19] printQuery《()》


6.] WarmUpTracker.builder()
	6.0] PolynomialDecayTracker.builder()
	6.1] PaddingStackBatchifi《er.builder()》
	6.2] TrainBertOnCode.runExample《()》
	6.3] dataSize.toLong《()》
	6.4] trainer.metrics = Metrics《()》
	6.5] executorService.shutdownNow《()》
	6.6] .shuffled《()》
	6.7] .toTypedArray《()》
	6.8] .distinct《()》
	6.9] .toLabeledGraph《()》
	6.10] Activation.relu(it)
	6.11] .toList()
	6.12] .renderVKG()
	6.13] else rebuildIndex()
	6.14] .asSequence()
	6.15] createOrLoadModel()
	6.16] batchData: TrainingListener.BatchData
	6.17] .allCodeFragments()
	6.18] .readText().lines()
	6.19] TextTerminator()


7.] .toTypedArray()
	7.0] .toLabeledGraph()
	7.1] 《.toL》ist()
	7.2] .asSequence《()》
	7.3] .shuffled《()》
	7.4] .allCodeFragments《()》
	7.5] dataSize.toLong《()》
	7.6] .distinct《()》
	7.7] .renderVKG《()》
	7.8] WarmUpTracker.builder《()》
	7.9] PolynomialDecayTracker.builder《()》
	7.10] .readText《()》.lines《()》
	7.11] .allFilesRecursively《()》
	7.12] .first《()》.toDoubleArray《()》
	7.13] .readText《()》
	7.14] executorService.shutdownNow《()》
	7.15] .show《()》
	7.16] PaddingStackBatchifier.builder《()》
	7.17] trainer.metrics = Metrics《()》
	7.18] .build《()》
	7.19] TrainBertOnCode.runExample《()》


8.] const val MAX_BATCH = 50
	8.0] const val MAX_VOCAB = 35000
	8.1] 《const val MAX_》GPUS = 1
	8.2] 《const val 》EPOCHS = 100000
	8.3] 《const val 》MAX_SEQUENCE_LENGTH = 128
	8.4] 《const val 》BATCH_SIZE = 24
	8.5] 《const val 》CLS = "<cls>"
	8.6] 《const val 》UNK = "<unk>"
	8.7] 《const val 》BERT_EMBEDDING_SIZE = 768
	8.8] dataSize.toL《on》g()
	8.9] val targetEmbedding =
	8.10] const val MSK = "<msk>"
	8.11] val use = UniversalSentenceEncoder
	8.12] sentenceA = otherA
	8.13] const val CODEBERT_CLS_TOKEN = "<s>"
	8.14] const val SEP = "<sep>"
	8.15] val d2vecs = vectors.reduceDim()
	8.16] return jfsRoot
	8.17] val range = 0..length
	8.18] val (matchStart, matchEnd) =
	8.19] PolynomialDecayTracker.builder()


9.] .renderVKG()
	9.0] .toLabeledGraph()
	9.1] .shuff《led》()
	9.2] .allCodeFragments《()》
	9.3] .distinct《()》
	9.4] .show《()》
	9.5] .toTypedArray《()》
	9.6] .toList《()》
	9.7] .readText《()》.lines《()》
	9.8] .build《()》
	9.9] dataSize.toLong《()》
	9.10] PolynomialDecayTracker.builder《()》
	9.11] WarmUpTracker.builder《()》
	9.12] .readText《()》
	9.13] .asSequence《()》
	9.14] .allFilesRecursively《()》
	9.15] Adam.builder《()》
	9.16] printQuery《()》
	9.17] createOrLoadModel《()》
	9.18] TrainBertOnCode.runExample《()》
	9.19] PaddingStackBatchifier.builder《()》


10.] .readText().lines()
	10.0] .readText()
	10.1] .toLabeledGraph《()》
	10.2] .toList《()》
	10.3] dataSize.toLong《()》
	10.4] .shuffled《()》
	10.5] .allCodeFragments《()》
	10.6] path.readText《()》.lines《()》
	10.7] .distinct《()》
	10.8] .renderVKG《()》
	10.9] .toTypedArray《()》
	10.10] .allFilesRecursively《()》
	10.11] .asSequence《()》
	10.12] .show《()》
	10.13] executorService.shutdownNow《()》
	10.14] WarmUpTracker.builder《()》
	10.15] Adam.builder《()》
	10.16] .build《()》
	10.17] PolynomialDecayTracker.builder《()》
	10.18] .first《()》.toDoubleArray《()》
	10.19] trainer.metrics = Metrics《()》


11.] .show()
	11.0] .build()
	11.1] .distinct《()》
	11.2] .shuffled《()》
	11.3] .toList《()》
	11.4] Adam.builder《()》
	11.5] .renderVKG《()》
	11.6] printQuery《()》
	11.7] .toLabeledGraph《()》
	11.8] TextTerminator《()》
	11.9] .readText《()》
	11.10] println《()》
	11.11] createOrLoadModel《()》
	11.12] .readText《()》.lines《()》
	11.13] else rebuildIndex《()》
	11.14] .toTypedArray《()》
	11.15] WarmUpTracker.builder《()》
	11.16] dataSize.toLong《()》
	11.17] .allCodeFragments《()》
	11.18] PolynomialDecayTracker.builder《()》
	11.19] }.toList《()》


12.] const val MAX_VOCAB = 35000
	12.0] const val MAX_BATCH = 50
	12.1] 《const val 》EPOCHS = 100000
	12.2] 《const val 》MAX_GPUS = 1
	12.3] 《const val 》MAX_SEQUENCE_LENGTH = 128
	12.4] 《const val 》CLS = "<cls>"
	12.5] 《const val 》BATCH_SIZE = 24
	12.6] 《const val 》UNK = "<unk>"
	12.7] 《const val 》MSK = "<msk>"
	12.8] 《const val 》BERT_EMBEDDING_SIZE = 768
	12.9] dataSize.toL《on》g()
	12.10] val d2vecs = vectors.reduceDim()
	12.11] const val SEP = "<sep>"
	12.12] val vocab = SimpleVocabulary.builder()
	12.13] val use = UniversalSentenceEncoder
	12.14] const val CODEBERT_CLS_TOKEN = "<s>"
	12.15] val targetEmbedding =
	12.16] sentenceA = otherA
	12.17] return jfsRoot
	12.18] val r = rand.nextFloat()
	12.19] PolynomialDecayTracker.builder()


13.] .allCodeFragments()
	13.0] .toLabeledGraph()
	13.1] .renderVKG《()》
	13.2] .toTypedArray《()》
	13.3] .allFilesRecursively《()》
	13.4] .toList《()》
	13.5] .shuffled《()》
	13.6] dataSize.toLong《()》
	13.7] .readText《()》.lines《()》
	13.8] .asSequence《()》
	13.9] .distinct《()》
	13.10] PolynomialDecayTracker.builder《()》
	13.11] .readText《()》
	13.12] WarmUpTracker.builder《()》
	13.13] executorService.shutdownNow《()》
	13.14] Adam.builder《()》
	13.15] .show《()》
	13.16] .build《()》
	13.17] .optLimit(100)
	13.18] .optBatchFirst(true)
	13.19] PaddingStackBatchifier.builder()


14.] const val MAX_GPUS = 1
	14.0] const val MAX_BATCH = 50
	14.1] 《const val MAX_》VOCAB = 35000
	14.2] 《const val 》EPOCHS = 100000
	14.3] 《const val 》BATCH_SIZE = 24
	14.4] 《const val 》MAX_SEQUENCE_LENGTH = 128
	14.5] 《const val 》CLS = "<cls>"
	14.6] dataSize.toL《on》g()
	14.7] c《on》st val BERT_EMBEDDING_SIZE = 768
	14.8] c《on》st val UNK = "<unk>"
	14.9] c《on》st val CODEBERT_CLS_TOKEN = "<s>"
	14.10] val targetEmbedding =
	14.11] val use = UniversalSentenceEncoder
	14.12] sentenceA = otherA
	14.13] const val MSK = "<msk>"
	14.14] val (matchStart, matchEnd) =
	14.15] const val SEP = "<sep>"
	14.16] return jfsRoot
	14.17] return trainer.trainingResult
	14.18] var numEpochs = 0
	14.19] PolynomialDecayTracker.builder()


15.] createOrLoadModel()
	15.0] printQuery()
	15.1] TextTerminator《()》
	15.2] else rebuildIndex《()》
	15.3] println《()》
	15.4] TrainBertOnCode.runExample《()》
	15.5] dataSize.toLong《()》
	15.6] PolynomialDecayTracker.builder《()》
	15.7] executorService.shutdownNow《()》
	15.8] return trainer.trainingResult
	15.9] WarmUpTracker.builder()
	15.10] }.toList()
	15.11] .show()
	15.12] PaddingStackBatchifier.builder()
	15.13] add(CLS)
	15.14] .build()
	15.15] Adam.builder()
	15.16] vocabSize
	15.17] .distinct()
	15.18] sentenceA = otherA
	15.19] .shuffled()


16.] vocabSize
	16.0] return trainer.trainingResult
	16.1] 《return 》jfsRoot
	16.2] 《return 》dataset
	16.3] Adam.builder()
	16.4] dataSize.toLong()
	16.5] rootDir: Path
	16.6] sentenceA = otherA
	16.7] val offset: Int
	16.8] list: NDList
	16.9] batchData: TrainingListener.BatchData
	16.10] TextTerminator()
	16.11] executorService.shutdownNow()
	16.12] PolynomialDecayTracker.builder()
	16.13] vocabSize: Long
	16.14] createOrLoadModel()
	16.15] PunctuationSeparator(),
	16.16] TextTruncator(10)
	16.17] Batchifier.STACK,
	16.18] add(CLS)
	16.19] PaddingStackBatchifier.builder()


17.] const val EPOCHS = 100000
	17.0] const val MAX_VOCAB = 35000
	17.1] 《const val MAX_》BATCH = 50
	17.2] 《const val MAX_》GPUS = 1
	17.3] 《const val MAX_》SEQUENCE_LENGTH = 128
	17.4] 《const val 》CLS = "<cls>"
	17.5] 《const val 》BATCH_SIZE = 24
	17.6] 《const val 》UNK = "<unk>"
	17.7] 《const val 》MSK = "<msk>"
	17.8] 《const val 》SEP = "<sep>"
	17.9] 《const val 》BERT_EMBEDDING_SIZE = 768
	17.10] 《val 》targetEmbedding =
	17.11] dataSize.toLong()
	17.12] val use = UniversalSentenceEncoder
	17.13] const val CODEBERT_CLS_TOKEN = "<s>"
	17.14] val d2vecs = vectors.reduceDim()
	17.15] val vocab = SimpleVocabulary.builder()
	17.16] var consecutive = true
	17.17] val knn = knnIndex.findNearest(v, topK)
	17.18] sentenceA = otherA
	17.19] val r = rand.nextFloat()


18.] Adam.builder()
	18.0] return dataset
	18.1] .show()
	18.2] vocabSize
	18.3] TextTerminator()
	18.4] dataSize.toLong()
	18.5] return trainer.trainingResult
	18.6] .build()
	18.7] .distinct()
	18.8] .toLabeledGraph()
	18.9] add(SEP)
	18.10] createOrLoadModel()
	18.11] PolynomialDecayTracker.builder()
	18.12] consecutive = false
	18.13] executorService.shutdownNow()
	18.14] val offset: Int
	18.15] .shuffled()
	18.16] .readText().lines()
	18.17] WarmUpTracker.builder()
	18.18] } else {
	18.19] add(CLS)


19.] package edu.mcgill.gymfs.agent
	19.0] package edu.mcgill.gymfs.experiments
	19.1] 《package edu.mcgill.gymfs.》inference
	19.2] 《package edu.mcgill.gymfs.》disk
	19.3] 《package edu.mcgill.gymfs》
	19.4] import jetbrains.letsPlot.labe《l.g》gtitle
	19.5] import edu.mcgil《l.g》ymfs.disk.*
	19.6] import com《.g》ithub.jelmerk.knn.SearchResult
	19.7] import jetbrains.datalore.plot.*
	19.8] import edu.mcgill.kaliningraph.*
	19.9] import jetbrains.letsPlot.intern.*
	19.10] import com.jujutsu.tsne.TSne
	19.11] import com.jujutsu.utils.TSneUtils
	19.12] import org.nield.kotlinstatistics.variance
	19.13] import com.github.jelmerk.knn.*
	19.14] import jetbrains.letsPlot.*
	19.15] import edu.mcgill.kaliningraph.show
	19.16] import guru.nidi.graphviz.*
	19.17] import kotlin.math.pow
	19.18] import kotlin.system.measureTimeMillis
	19.19] import org.slf4j.LoggerFactory
```
</details>

# Deployment

Need to build fat JAR locally then deploy, CC doesn't like Gradle for some reason.

```
./gradlew jar && scp build/libs/gym-fs-fat-1.0-SNAPSHOT.jar breandan@beluga.calculquebec.ca:/home/breandan/projects/def-jinguo/breandan/gym-fs
```

To reindex, first start CodeBERT server, to vectorize the code fragments:

```bash
# Serves vectorized code fragments at http://localhost:8000/?<QUERY>
python codebert_server.py
```

# Libraries

* [Concurrent Trees](https://github.com/npgall/concurrent-trees) - For fast indexing and retrieval.
* [Jimfs](https://github.com/google/jimfs) - An in-memory file system for dynamic document parsing.
* [HNSW](https://github.com/jelmerk/hnswlib) - Java library for approximate nearest neighbors search using Hierarchical Navigable Small World graphs
* [java-string-similarity](https://github.com/tdebatty/java-string-similarity) - Implementation of various string similarity and distance algorithms

# Papers

* [Efficient and robust approximate nearest neighbor search using Hierarchical Navigable Small World graphs](https://arxiv.org/pdf/1603.09320.pdf), Malkov & Yashunin (2015
* [BERT-kNN: Adding a kNN Search Component to Pretrained Language Models for Better QA](https://arxiv.org/pdf/2005.00766.pdf), Kassner & Schutze (2020)
* [AutoKG: Constructing Virtual Knowledge Graphs from Unstructured Documents for Question Answering](https://arxiv.org/pdf/2008.08995.pdf), Yu et al. (2021)
* [Graph Optimal Transport for Cross-Domain Alignment](http://proceedings.mlr.press/v119/chen20e/chen20e.pdf), Chen et al. (2021)
* [TextRank: Bringing Order into Texts](https://www.aclweb.org/anthology/W04-3252.pdf), Mihalcea and Tarau (2004)

# Resources

* [Query Refinement / Relevance models](https://chauff.github.io/documents/ir2017/Query-Refinement-Lecture.pdf#page=24)

# Benchmarking

* [CodeSearchNet](https://github.com/github/CodeSearchNet)
* [OpenMatch](https://github.com/thunlp/OpenMatch)
* [Steps for Evaluating Search Algorithms](https://shopify.engineering/evaluating-search-algorithms) (e.g. MAP, DCG)