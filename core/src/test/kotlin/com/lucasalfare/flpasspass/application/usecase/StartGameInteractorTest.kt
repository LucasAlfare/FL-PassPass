package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.PlayerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Spec-style tests for session bootstrap behavior.
 */
class StartGameInteractorTest {

  private val interactor = StartGameInteractor()

  /** Ensures the first player becomes the active player in a new game. */
  @Test
  fun `start game creates a session with the first player active`() {
    val session = interactor.execute(
      StartGameCommand(
        firstPlayer = PlayerSetup(PlayerId(1), Code.of(7, 2, 9, 4)),
        secondPlayer = PlayerSetup(PlayerId(2), Code.of(1, 3, 5, 8)),
      ),
    )

    assertEquals(2, session.players.size)
    assertEquals(PlayerId(1), session.activePlayerId)
    assertEquals(null, session.winnerId)
    assertEquals(Code.of(7, 2, 9, 4), session.players[0].secretCode)
    assertEquals(25, session.players[0].energy.value)
    assertEquals(2, session.players[0].blockCharges.value)
  }

  /** Ensures the bootstrap command rejects duplicated player identifiers. */
  @Test
  fun `start game rejects duplicated player ids`() {
    assertFailsWith<IllegalArgumentException> {
      interactor.execute(
        StartGameCommand(
          firstPlayer = PlayerSetup(PlayerId(1), Code.of(7, 2, 9, 4)),
          secondPlayer = PlayerSetup(PlayerId(1), Code.of(1, 3, 5, 8)),
        ),
      )
    }
  }
}
