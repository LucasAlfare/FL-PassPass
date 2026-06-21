package com.lucasalfare.flpasspass.domain.model

/**
 * Scores a guessed [Code] against the secret code.
 *
 * The result separates exact matches from digits that exist in the secret but
 * are placed in the wrong position. That split is the foundation of the
 * feedback loop used by the deduction gameplay and by the bot implementations.
 */
fun Code.scoreAttempt(guess: Code): GuessFeedback {
  val correctPositions = digits.zip(guess.digits).count { (secretDigit, guessedDigit) ->
    secretDigit == guessedDigit
  }

  val misplacedDigits = guess.digits.count { it in digits } - correctPositions

  return GuessFeedback(
    correctPositions = correctPositions,
    misplacedDigits = misplacedDigits,
  )
}
