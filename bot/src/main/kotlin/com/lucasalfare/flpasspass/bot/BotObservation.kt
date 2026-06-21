package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.engine.GameCommand
import com.lucasalfare.flpasspass.engine.GameResponse
import com.lucasalfare.flpasspass.engine.GameState

/**
 * Event feed that lets a bot learn from the match over time.
 *
 * UIs or orchestration layers can forward observations after setup and after
 * each resolved turn so richer strategies can build internal state.
 */
sealed interface BotObservation {
  /** Signals that a new match has started and exposes the initial public state. */
  data class GameStarted(
    val initialState: GameState,
  ) : BotObservation

  /** Captures a resolved turn so the bot can learn from the engine response. */
  data class TurnResolved(
    val command: GameCommand.SubmitTurn,
    val response: GameResponse,
  ) : BotObservation
}
