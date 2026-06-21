package com.lucasalfare.flpasspass.application.model

import com.lucasalfare.flpasspass.domain.model.GuessFeedback

/**
 * Result produced after applying one turn to an internal session.
 *
 * The result carries the updated session plus optional action-specific output
 * so the engine can expose a compact, front-end-friendly response object.
 *
 * @property session The updated internal session after the turn resolves.
 * @property feedback Feedback for code attempts, when the action was a guess.
 * @property investigationAnswer Boolean answer for investigation questions, when applicable.
 */
internal data class SubmitTurnResult(
  val session: GameSession,
  val feedback: GuessFeedback? = null,
  val investigationAnswer: Boolean? = null,
)
