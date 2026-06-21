package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.action.TurnAction

internal data class PlayerSetup(
  val id: PlayerId,
  val secretCode: Code,
)

internal data class StartGameCommand(
  val firstPlayer: PlayerSetup,
  val secondPlayer: PlayerSetup,
)

internal data class SubmitTurnCommand(
  val playerId: PlayerId,
  val action: TurnAction,
)
