package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.engine.GameState

/**
 * Lightweight runtime that owns a bot strategy and its evolving memory.
 *
 * The runtime acts as the integration surface that UIs can use without caring
 * about the specific strategy implementation or memory layout.
 */
class BotRuntime(
  private val strategy: BotStrategy = SimpleHeuristicBotStrategy(),
  private var memory: BotMemory = BotMemory(),
) {
  /** Records an observation so future decisions can take prior turns into account. */
  fun observe(observation: BotObservation) {
    memory = memory.record(observation)
  }

  /** Asks the configured strategy to decide using the supplied full context. */
  fun decide(context: BotContext): BotDecision {
    return strategy.decide(context.copy(memory = memory))
  }

  /** Convenience overload that builds the context from the current memory and the public game state. */
  fun decide(gameState: GameState, selfPlayerId: PlayerId): BotDecision {
    return decide(BotContext(selfPlayerId = selfPlayerId, gameState = gameState, memory = memory))
  }

  /** Exposes the current memory for inspection or persistence. */
  fun memory(): BotMemory = memory

  /** Clears all accumulated observations and returns the runtime to its initial state. */
  fun reset() {
    memory = BotMemory()
  }
}
