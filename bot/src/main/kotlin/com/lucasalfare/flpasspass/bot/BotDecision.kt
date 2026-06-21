package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.domain.model.action.TurnAction

/**
 * Final action selected by a bot strategy.
 *
 * @property action The turn action to submit back to the engine.
 * @property confidence Human-readable confidence estimate for UIs and diagnostics.
 * @property reason Optional narrative explanation for why the action was chosen.
 * @property trace Optional debugging trace that can help explain the bot's reasoning.
 */
data class BotDecision(
  val action: TurnAction,
  val confidence: Double = 0.5,
  val reason: String? = null,
  val trace: List<String> = emptyList(),
) {
  init {
    require(confidence in 0.0..1.0) { "Confidence must be between 0.0 and 1.0." }
  }
}
