package com.lucasalfare.flpasspass.engine

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Verifies the engine facade exposes only public state and command responses.
 */
class GameEngineTest {

  private val engine = GameEngine()

  /** Ensures a new session exposes only public state, not internal details. */
  @Test
  fun `engine starts a game and exposes public state only`() {
    val response = engine.handle(
      GameCommand.StartGame(
        firstPlayer = PlayerSetupInput(PlayerId(1), Code.of(7, 2, 9, 4)),
        secondPlayer = PlayerSetupInput(PlayerId(2), Code.of(1, 3, 5, 8)),
      ),
    )

    assertEquals(PlayerId(1), response.state.activePlayerId)
    assertEquals(2, response.state.players.size)
    assertNull(response.state.winnerId)
    assertNull(response.state.pendingBlock)
    assertEquals(response.state, engine.state())
  }

  /** Ensures turn submission updates state and returns action-specific output. */
  @Test
  fun `engine submits a turn and returns response data`() {
    engine.handle(
      GameCommand.StartGame(
        firstPlayer = PlayerSetupInput(PlayerId(1), Code.of(7, 2, 9, 4)),
        secondPlayer = PlayerSetupInput(PlayerId(2), Code.of(1, 3, 5, 8)),
      ),
    )

    val response = engine.handle(
      GameCommand.SubmitTurn(
        playerId = PlayerId(1),
        action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
      ),
    )

    assertEquals(true, response.investigationAnswer)
    assertNull(response.feedback)
    assertEquals(PlayerId(2), response.state.activePlayerId)
    assertEquals(24, response.state.players.first { it.id == PlayerId(1) }.energy.value)
  }
}
