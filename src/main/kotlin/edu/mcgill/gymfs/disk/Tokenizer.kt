package edu.mcgill.gymfs.disk

import ai.djl.modality.nlp.SimpleVocabulary
import com.robrua.nlp.bert.FullTokenizer
import java.io.File
import java.net.URL

// TODO pack the URL query with 512 tokens
fun main() {
  val vocab = SimpleVocabulary.builder()
    .optMinFrequency(1)
    .add(VOCAB.readText().lines())
    .optUnknownToken("[UNK]")
    .build()

  File("vocab.txt").let {
    if (!it.exists())
      it.writeText(
        URL("https://huggingface.co/microsoft/codebert-base/resolve/main/vocab.json")
          .readText()
          .removePrefix("{\"")
          .substringBeforeLast("\"")
//        .replace("Ġ", " ")
          .split(Regex("\": [0-9]*, \""))
          .joinToString("\n")
      )
  }

  val tok = FullTokenizer(File("vocab.txt"))
  val tokens = tok.tokenize("for i in t doSomething")
  println(tokens.joinToString(", "))
//  println(tok.convert(tokens))
}