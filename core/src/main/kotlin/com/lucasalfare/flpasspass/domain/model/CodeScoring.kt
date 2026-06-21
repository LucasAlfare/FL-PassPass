package com.lucasalfare.flpasspass.domain.model

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
