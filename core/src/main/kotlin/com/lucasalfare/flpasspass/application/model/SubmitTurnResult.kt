package com.lucasalfare.flpasspass.application.model

import com.lucasalfare.flpasspass.domain.model.GuessFeedback

internal data class SubmitTurnResult(
  val session: GameSession,
  val feedback: GuessFeedback? = null,
  val investigationAnswer: Boolean? = null,
)
