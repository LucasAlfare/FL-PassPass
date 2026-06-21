package com.lucasalfare.flpasspass.domain.model.question

import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.PositionIndex

sealed interface InvestigationQuestion {
  data class DigitExists(val digit: Digit) : InvestigationQuestion

  data class DigitAtPosition(
    val position: PositionIndex,
    val digit: Digit,
  ) : InvestigationQuestion

  data class PositionCharacteristic(
    val position: PositionIndex,
    val characteristic: DigitCharacteristic,
  ) : InvestigationQuestion

  data class PositionComparison(
    val leftPosition: PositionIndex,
    val rightPosition: PositionIndex,
  ) : InvestigationQuestion
}

enum class DigitCharacteristic {
  EVEN,
  ODD,
  GREATER_THAN_FIVE,
  LESS_THAN_FIVE,
}
