package com.lucasalfare.flpasspass.domain.model

import com.lucasalfare.flpasspass.domain.model.question.DigitCharacteristic
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DomainModelTest {

  @Test
  fun `code factory creates a valid 4 digit unique code`() {
    val code = Code.of(7, 2, 9, 4)

    assertEquals(listOf(Digit(7), Digit(2), Digit(9), Digit(4)), code.digits)
  }

  @Test
  fun `code rejects repeated digits`() {
    assertFailsWith<IllegalArgumentException> {
      Code.of(7, 7, 2, 4)
    }
  }

  @Test
  fun `code rejects wrong length`() {
    assertFailsWith<IllegalArgumentException> {
      Code.of(1, 2, 3)
    }
  }

  @Test
  fun `digit accepts values between 0 and 9`() {
    assertEquals(0, Digit(0).value)
    assertEquals(9, Digit(9).value)
  }

  @Test
  fun `digit rejects values outside the valid range`() {
    assertFailsWith<IllegalArgumentException> {
      Digit(-1)
    }

    assertFailsWith<IllegalArgumentException> {
      Digit(10)
    }
  }

  @Test
  fun `position index accepts values from 1 to 4`() {
    assertEquals(1, PositionIndex(1).value)
    assertEquals(4, PositionIndex(4).value)
  }

  @Test
  fun `position index rejects values outside the valid range`() {
    assertFailsWith<IllegalArgumentException> {
      PositionIndex(0)
    }

    assertFailsWith<IllegalArgumentException> {
      PositionIndex(5)
    }
  }

  @Test
  fun `guess feedback accepts consistent values`() {
    val feedback = GuessFeedback(correctPositions = 2, misplacedDigits = 1)

    assertEquals(2, feedback.correctPositions)
    assertEquals(1, feedback.misplacedDigits)
  }

  @Test
  fun `guess feedback rejects invalid totals`() {
    assertFailsWith<IllegalArgumentException> {
      GuessFeedback(correctPositions = 3, misplacedDigits = 2)
    }
  }

  @Test
  fun `code scoring counts correct and misplaced digits`() {
    val secret = Code.of(7, 2, 9, 4)
    val guess = Code.of(7, 9, 4, 1)

    val feedback = secret.scoreAttempt(guess)

    assertEquals(1, feedback.correctPositions)
    assertEquals(2, feedback.misplacedDigits)
  }

  @Test
  fun `code matches investigation questions`() {
    val code = Code.of(7, 2, 9, 4)

    assertEquals(false, code.matches(InvestigationQuestion.DigitExists(Digit(8))))
    assertEquals(true, code.matches(InvestigationQuestion.DigitAtPosition(PositionIndex(1), Digit(7))))
    assertEquals(true, code.matches(InvestigationQuestion.PositionCharacteristic(PositionIndex(3), DigitCharacteristic.ODD)))
    assertEquals(true, code.matches(InvestigationQuestion.PositionComparison(PositionIndex(1), PositionIndex(2))))
  }

  @Test
  fun `player resources start with default values`() {
    val resources = PlayerResources()

    assertEquals(25, resources.energy.value)
    assertEquals(2, resources.blockCharges.value)
  }
}
