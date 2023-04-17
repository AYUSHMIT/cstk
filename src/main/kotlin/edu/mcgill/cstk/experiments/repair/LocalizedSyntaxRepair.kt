package edu.mcgill.cstk.experiments.repair

import ai.hypergraph.kaliningraph.parsing.*
import edu.mcgill.cstk.disk.*
import edu.mcgill.cstk.utils.*
import org.apache.commons.lang3.StringUtils

/**
 * In this experiment, we sample nontrivial single-line statements with balanced
 * bracket from MiniGithub, delete a random bracket and feed the location to the
 * model, ask it to predict what the deleted token was.
 *
 * This will produce scores for each model, i.e., how many tokens it predicted
 * correctly out of the total number of samples tested:
 *
 *     Scores [model=(valid, total)]:
 *     microsoft/codebert-base-mlm=(1225, 3510)
 *     huggingface/CodeBERTa-small-v1=(827, 3510)
 *     microsoft/graphcodebert-base=(1021, 3510)
 *     dbernsohn/roberta-java=(715, 3510)
 *     tidyparse=(1113, 3510)
 */

/*
./gradlew localizedSyntaxRepair
 */

const val MSK = "___"

// Our model predicts all possible locations as well as the fix
// Fine tune model to predict whether it is missing parentheses
// or not (single-bit prediction for error location)
// If results are comparable (unmodified) in terms of # of fixes,
// binary classification task
// Easy setting: everyone knows the location
// Hard setting: location unknown (maybe BIFI/NLM get confused)

// TODO:
//     Design custom grammars to fix each (sub)category of errors
//     Goal is to minimize the grammar size (no need to be complicated)
//     Evaluate Tidyparse with grammar to compare with accuracy of BIFI

// Read paper carefully and try to understand why the categories
// do not match raw count, if cannot resolve why, write to authors

typealias Scores = Map<Model, Pair<Int, Int>>

fun main() {
  val tidyparse = Model("tidyparse")
  val models = MODELS + tidyparse
  val modelScores: Scores = models.associateWith { (0 to 0) }

  DATA_DIR.also { println("Evaluating syntax repair using $models on $it...") }
    .allFilesRecursively().allMethods()
    .map { it.first.lineSequence() }.flatten()
    .filter(Σᐩ::isANontrivialStatementWithBalancedBrackets)
    .map { it to it.constructPromptByMaskingRandomSyntax() }
    .runningFold(modelScores) { scores, (groundTruth, prompt) ->
      models.associateWith { model ->
        val repairs = prompt.dispatchTo(model, cfg)
        updateScore(scores, model) { groundTruth in repairs }
      }
    }
//    .filterIndexed { i, _ -> i % 10 == 0 }
    .forEach { println("\nScores [model=(valid, total)]:\n${it.entries.joinToString("\n")}") }
}

fun updateScore(scores: Scores, model: Model, groundTruth: () -> Boolean) =
  scores[model]!!.let { (n, d) -> // numerator / denominator
    if (groundTruth()) (n + 1) to (d + 1) else n to (d + 1)
  }

fun Σᐩ.coarsen(): Σᐩ =
  defaultTokenizer().joinToString(" ") {
    when {
      it.isBracket() -> it
      it == MSK -> "_"
      else -> "w"
    }
  }

fun Σᐩ.isWhitespace() = trim().isEmpty()

fun Σᐩ.uncoarsen(prompt: Σᐩ): Σᐩ {
  val words = prompt.defaultTokenizer().filter { it !in brackets }.toMutableList()
  return tokenizeByWhitespace().joinToString("") { token ->
    when {
      token.isBracket() -> token
      words.isEmpty() -> { //System.err.println("IDK what happened:\nSynthesized:  $this");
        "" }
      token == "w" -> words.removeAt(0)
      else -> throw Exception("Unknown token: $token")
    }
  } + words.joinToString("")
}

fun Σᐩ.isBracket() = length == 1 && this in brackets

fun Σᐩ.constructPromptByMaskingRandomSyntax(
  eligibleTokensToMask: Set<Σᐩ> = brackets,
  numTokensToMask: Int = 1,
  tokenize: Σᐩ.() -> List<Σᐩ> = Σᐩ::defaultTokenizer
): Σᐩ =
  tokenize().toMutableList().let { codeTokens ->
//    println("Code tokens: $codeTokens")
//    println("Eligible tokens to mask: $eligibleTokensToMask")
    codeTokens.indices.filter { codeTokens[it].trim() in eligibleTokensToMask }
//      .also { println("Indices of eligible tokens to mask: $it") }
      .shuffled().take(numTokensToMask)
      .forEach { codeTokens[it] = MSK }
    codeTokens
  }.joinToString(" ")

val brackets = "()[]{}".map { "$it" }.toSet()
fun Σᐩ.defaultTokenizer(): List<Σᐩ> =
  split(Regex("[\\(\\)\\[\\]{}]|___".let { "((?<=($it))|(?=($it)))" }))

fun Σᐩ.tokenizeGranular() =
  StringUtils.splitByCharacterTypeCamelCase(this).toList()
