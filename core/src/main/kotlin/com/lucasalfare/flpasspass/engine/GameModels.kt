package com.lucasalfare.flpasspass.engine

import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.GuessFeedback
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget

data class PlayerSetupInput(
  val id: PlayerId,
  val secretCode: Code,
)

sealed interface GameCommand {
  data class StartGame(
    val firstPlayer: PlayerSetupInput,
    val secondPlayer: PlayerSetupInput,
  ) : GameCommand

  data class SubmitTurn(
    val playerId: PlayerId,
    val action: TurnAction,
  ) : GameCommand
}

data class GameResponse(
  val state: GameState,
  val feedback: GuessFeedback? = null,
  val investigationAnswer: Boolean? = null,
)

data class GameState(
  val players: List<PlayerState>,
  val activePlayerId: PlayerId,
  val winnerId: PlayerId? = null,
  val pendingBlock: PendingBlockState? = null,
)

data class PlayerState(
  val id: PlayerId,
  val energy: EnergyPoints,
  val blockCharges: BlockCharges,
)

sealed interface PendingBlockState {
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : PendingBlockState

  data object AttemptClue : PendingBlockState

  data object EnergySurcharge : PendingBlockState
}
