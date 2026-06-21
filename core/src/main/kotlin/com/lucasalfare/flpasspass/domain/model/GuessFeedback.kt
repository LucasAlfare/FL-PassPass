package com.lucasalfare.flpasspass.domain.model

/**
 * Feedback returned after a code attempt.
 *
 * @property correctPositions The number of digits that are both correct and in the right position.
 * @property misplacedDigits The number of digits that exist in the secret code but are not in the right position.
 */
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
