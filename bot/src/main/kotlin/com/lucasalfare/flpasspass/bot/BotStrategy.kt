package com.lucasalfare.flpasspass.bot

/**
 * Strategy contract for any bot implementation.
 *
 * Implementations can be simple heuristics, stateful deduction engines, or
 * more advanced search-based players, as long as they can produce a public
 * [BotDecision] from the supplied [BotContext].
 */
interface BotStrategy {
  /** Chooses the next action for the bot using the current public context. */
  fun decide(context: BotContext): BotDecision
}
