package edu.mcgill.cstk.experiments.probing

import ai.hypergraph.kaliningraph.parsing.dyckCheck
import edu.mcgill.cstk.disk.*
import edu.mcgill.cstk.nlp.*

/**
 * In this experiment, we sample nontrivial single-line statements with balanced
 * parentheses from MiniGithub, truncate after the last open parenthesis, sample
 * autoregressively from the model under test until an end of statement token is
 * emitted, then measure how many samples are syntactically well-formed.
 *
 * This will produce scores for each model, i.e., how many samples are
 * syntactically well-formed out of the total number of samples tested:
 *
 *      Scores [model=(valid, total)]:
 *      microsoft/codebert-base-mlm=(85, 236)
 *      huggingface/CodeBERTa-small-v1=(61, 299)
 *      microsoft/graphcodebert-base=(45, 262)
 *      dbernsohn/roberta-java=(28, 201)
 *      ...
*/

/**
./gradlew completeSyntax
 */

fun main() {
  DATA_DIR
    .also { println("Evaluating syntax completion using $MODELS on $it...") }
    .allFilesRecursively().allMethods().map { it.first.lineSequence() }.flatten()
    .filter(String::isANontrivialStatementWithBalancedParentheses)
    .map { it to it.constructPrompt() }
    .runningFold(MODELS.associateWith { (0 to 0) }) { scores, (groundTruth, prompt) ->
      MODELS.associateWith { model ->
        val completion = model.complete(prompt + model.mask, maxTokens = 50)
        scores[model]!!.let { (n, d) ->
          if (!completion.endsWith(";")) n to d
          else (n + if (completion.dyckCheck()) 1 else 0) to (d + 1)
        }
      }
    }
    .filterIndexed { i, _ -> i % 10 == 0 }
    .forEach { println("\nScores [model=(valid, total)]:\n${it.entries.joinToString("\n")}") }
}

// Can model reconstruct a syntactically valid snippet from its truncated form?
private fun String.constructPrompt() =
  substringBeforeLast('(').trim() + '('

private fun String.isANontrivialStatementWithBalancedParentheses(
  parensAndDepth: Pair<Int, Int> = countBracketsAndMaxDepth(),
) =
  trim().endsWith(';')
    && parensAndDepth.let { (p, d) -> p == 0 && 2 < d }
    && dyckCheck()