package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.engine.GameState
import com.lucasalfare.flpasspass.engine.PlayerState

/**
 * Snapshot of everything a bot is allowed to know at decision time.
 *
 * The context keeps bot logic decoupled from the UI by exposing only the
 * public game state plus the bot's own accumulated memory.
 *
 * @property selfPlayerId The bot-controlled player.
 * @property gameState The current public match state.
 * @property memory The bot's accumulated observations and reasoning aids.
 */
data class BotContext(
  val selfPlayerId: PlayerId,
  val gameState: GameState,
  val memory: BotMemory = BotMemory(),
) {
  /** Returns the public state of the bot-controlled player. */
  val selfState: PlayerState
    get() = gameState.players.first { it.id == selfPlayerId }

  /** Returns the public state of the opponent. */
  val opponentState: PlayerState
    get() = gameState.players.first { it.id != selfPlayerId }
}
