package com.lucasalfare.flpasspass.domain.model

import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget
import com.lucasalfare.flpasspass.domain.model.question.DigitCharacteristic
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion

fun Code.matches(question: InvestigationQuestion): Boolean {
  return when (question) {
    is InvestigationQuestion.DigitExists -> question.digit in digits
    is InvestigationQuestion.DigitAtPosition -> digits[question.position.value - 1] == question.digit
    is InvestigationQuestion.PositionCharacteristic -> digits[question.position.value - 1].matches(question.characteristic)
    is InvestigationQuestion.PositionComparison -> {
      val left = digits[question.leftPosition.value - 1]
      val right = digits[question.rightPosition.value - 1]
      left.value > right.value
    }
  }
}

fun InvestigationQuestion.blockTarget(): InvestigationBlockTarget {
  return when (this) {
    is InvestigationQuestion.DigitExists -> InvestigationBlockTarget.DIGIT_EXISTS
    is InvestigationQuestion.DigitAtPosition -> InvestigationBlockTarget.DIGIT_AT_POSITION
    is InvestigationQuestion.PositionCharacteristic -> InvestigationBlockTarget.POSITION_CHARACTERISTIC
    is InvestigationQuestion.PositionComparison -> InvestigationBlockTarget.POSITION_COMPARISON
  }
}

private fun Digit.matches(characteristic: DigitCharacteristic): Boolean {
  return when (characteristic) {
    DigitCharacteristic.EVEN -> value % 2 == 0
    DigitCharacteristic.ODD -> value % 2 != 0
    DigitCharacteristic.GREATER_THAN_FIVE -> value > 5
    DigitCharacteristic.LESS_THAN_FIVE -> value < 5
  }
}
