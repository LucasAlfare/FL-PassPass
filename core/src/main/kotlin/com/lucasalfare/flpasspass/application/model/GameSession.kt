package com.lucasalfare.flpasspass.application.model

import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.PlayerResources
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget

internal data class PlayerSnapshot(
  val id: PlayerId,
  val secretCode: Code,
  val energy: EnergyPoints = PlayerResources().energy,
  val blockCharges: BlockCharges = PlayerResources().blockCharges,
)

internal data class GameSession(
  val players: List<PlayerSnapshot>,
  val activePlayerId: PlayerId,
  val winnerId: PlayerId? = null,
  val pendingBlock: PendingBlockEffect? = null,
) {
  init {
    require(players.size == 2) { "A game session must have exactly two players." }
    require(players.distinctBy { it.id }.size == players.size) { "Players in a session must be unique." }
    require(players.any { it.id == activePlayerId }) { "Active player must belong to the session." }
    require(winnerId == null || players.any { it.id == winnerId }) { "Winner must belong to the session." }
  }
}

internal sealed interface PendingBlockEffect {
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : PendingBlockEffect

  data object AttemptClue : PendingBlockEffect

  data object EnergySurcharge : PendingBlockEffect
}
