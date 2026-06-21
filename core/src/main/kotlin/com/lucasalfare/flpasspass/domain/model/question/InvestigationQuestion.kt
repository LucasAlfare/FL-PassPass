package com.lucasalfare.flpasspass.domain.model.question

import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.PositionIndex

/**
 * Closed set of investigation questions allowed by the game rules.
 *
 * Each subtype corresponds to one of the query forms described in the README
 * and can be resolved or blocked independently by the engine.
 */
sealed interface InvestigationQuestion {
  /** Asks whether a given digit appears anywhere in the secret code. */
  data class DigitExists(val digit: Digit) : InvestigationQuestion

  /** Asks whether a given position contains a specific digit. */
  data class DigitAtPosition(
    val position: PositionIndex,
    val digit: Digit,
  ) : InvestigationQuestion

  /** Asks whether a digit at a specific position has the requested property. */
  data class PositionCharacteristic(
    val position: PositionIndex,
    val characteristic: DigitCharacteristic,
  ) : InvestigationQuestion

  /** Asks whether the digit on the left is greater than the digit on the right. */
  data class PositionComparison(
    val leftPosition: PositionIndex,
    val rightPosition: PositionIndex,
  ) : InvestigationQuestion
}

/**
 * High-level characteristics that can be queried about a digit.
 */
enum class DigitCharacteristic {
  EVEN,
  ODD,
  GREATER_THAN_FIVE,
  LESS_THAN_FIVE,
}
