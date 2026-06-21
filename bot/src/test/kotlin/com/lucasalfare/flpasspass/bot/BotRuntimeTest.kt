package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.engine.GameCommand
import com.lucasalfare.flpasspass.engine.GameResponse
import com.lucasalfare.flpasspass.engine.GameState
import com.lucasalfare.flpasspass.engine.PendingBlockState
import com.lucasalfare.flpasspass.engine.PlayerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies that the bot runtime preserves memory and delegates decision-making safely.
 */
class BotRuntimeTest {
  private val gameState = GameState(
    players = listOf(
      PlayerState(PlayerId(1), com.lucasalfare.flpasspass.domain.model.EnergyPoints(25), com.lucasalfare.flpasspass.domain.model.BlockCharges(2)),
      PlayerState(PlayerId(2), com.lucasalfare.flpasspass.domain.model.EnergyPoints(25), com.lucasalfare.flpasspass.domain.model.BlockCharges(2)),
    ),
    activePlayerId = PlayerId(1),
    winnerId = null,
    pendingBlock = PendingBlockState.AttemptClue,
  )

  /** Ensures observations are appended to the runtime memory. */
  @Test
  fun `runtime stores observations`() {
    val runtime = BotRuntime()

    runtime.observe(
      BotObservation.GameStarted(gameState),
    )

    assertEquals(1, runtime.memory().observations.size)
  }

  /** Ensures the strategy can produce a valid public action from a live game state. */
  @Test
  fun `strategy produces a valid decision`() {
    val runtime = BotRuntime()

    val decision = runtime.decide(gameState, PlayerId(1))

    assertTrue(decision.confidence in 0.0..1.0)
    assertEquals(PlayerId(1), gameState.activePlayerId)
    assertTrue(
      decision.action is com.lucasalfare.flpasspass.domain.model.action.TurnAction.Investigate ||
        decision.action is com.lucasalfare.flpasspass.domain.model.action.TurnAction.AttemptCode ||
        decision.action is com.lucasalfare.flpasspass.domain.model.action.TurnAction.Block,
    )
  }

  /** Ensures turn observations can be recorded without leaking implementation details. */
  @Test
  fun `turn resolution observation can be recorded`() {
    val runtime = BotRuntime()
    val command = GameCommand.SubmitTurn(
      playerId = PlayerId(1),
      action = com.lucasalfare.flpasspass.domain.model.action.TurnAction.AttemptCode(Code.of(1, 2, 3, 4)),
    )
    val response = GameResponse(state = gameState)

    runtime.observe(BotObservation.TurnResolved(command, response))

    assertEquals(1, runtime.memory().observations.size)
  }
}
