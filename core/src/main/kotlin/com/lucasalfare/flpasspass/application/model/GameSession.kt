package com.lucasalfare.flpasspass.application.model

import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.PlayerResources
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget

/**
 * Internal snapshot of a player used by the application layer.
 *
 * It mirrors the domain player but stores mutable session resources directly so
 * the engine can evolve state without exposing implementation details.
 *
 * @property id Stable identifier for the player inside the session.
 * @property secretCode Hidden code owned by this player.
 * @property energy Remaining energy available for turn actions.
 * @property blockCharges Remaining number of blocks that can still be spent.
 */
internal data class PlayerSnapshot(
  val id: PlayerId,
  val secretCode: Code,
  val energy: EnergyPoints = PlayerResources().energy,
  val blockCharges: BlockCharges = PlayerResources().blockCharges,
)

/**
 * Complete mutable game session used by the application interactor layer.
 *
 * The session stores only the information needed to resolve turns and derive
 * the public engine state. It intentionally remains internal so front ends can
 * only access the safer public projections exposed by the engine.
 *
 * @property players The two participating players.
 * @property activePlayerId The player currently allowed to submit a turn.
 * @property winnerId The winner if the game has already ended.
 * @property pendingBlock The active block effect that applies to the next opponent turn.
 */
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

/**
 * Internal representation of the block effect waiting to be consumed.
 *
 * The application layer uses this to enforce the "next turn only" semantics of
 * blocking without leaking implementation details to the public engine API.
 */
internal sealed interface PendingBlockEffect {
  /** Blocks one investigation category on the next opponent turn. */
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : PendingBlockEffect

  /** Reduces the amount of information returned by the next code attempt. */
  data object AttemptClue : PendingBlockEffect

  /** Adds a flat surcharge to the next action performed by the opponent. */
  data object EnergySurcharge : PendingBlockEffect
}
