package com.lucasalfare.flpasspass.domain.model

data class GuessFeedback(
  val correctPositions: Int,
  val misplacedDigits: Int,
) {
  init {
    require(correctPositions in 0..Code.LENGTH) { "Correct positions must be between 0 and ${Code.LENGTH}." }
    require(misplacedDigits in 0..Code.LENGTH) { "Misplaced digits must be between 0 and ${Code.LENGTH}." }
    require(correctPositions + misplacedDigits <= Code.LENGTH) {
      "Total feedback cannot exceed ${Code.LENGTH}."
    }
  }
}
