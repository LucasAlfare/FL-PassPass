package com.lucasalfare.flpasspass.engine

import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.GuessFeedback
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget

/**
 * Public bootstrap data for a player when starting a game through the engine.
 *
 * UIs only need to provide the player identifier and the hidden code; the rest
 * of the session state is derived by the application layer.
 */
data class PlayerSetupInput(
  val id: PlayerId,
  val secretCode: Code,
)

/**
 * Top-level command accepted by the engine.
 *
 * The engine only exposes commands that make sense to a front end: start a new
 * game or submit a turn for the currently active player.
 */
sealed interface GameCommand {
  /** Starts a new session with two prepared players. */
  data class StartGame(
    val firstPlayer: PlayerSetupInput,
    val secondPlayer: PlayerSetupInput,
  ) : GameCommand

  /** Submits a turn on behalf of one player. */
  data class SubmitTurn(
    val playerId: PlayerId,
    val action: TurnAction,
  ) : GameCommand
}

/**
 * Unified response returned by the engine after processing a command.
 *
 * @property state The latest public game state after command resolution.
 * @property feedback Feedback returned when the action was a code attempt.
 * @property investigationAnswer Resolution of an investigation question, when applicable.
 */
data class GameResponse(
  val state: GameState,
  val feedback: GuessFeedback? = null,
  val investigationAnswer: Boolean? = null,
)

/**
 * Public snapshot of the match state suitable for UI rendering.
 *
 * It intentionally hides the secret codes and other internal session details
 * while still giving front ends enough information to display progress.
 *
 * @property players Public player resource summaries.
 * @property activePlayerId The player currently allowed to act.
 * @property winnerId The winner if the game has ended.
 * @property pendingBlock The currently active block effect, if any.
 */
data class GameState(
  val players: List<PlayerState>,
  val activePlayerId: PlayerId,
  val winnerId: PlayerId? = null,
  val pendingBlock: PendingBlockState? = null,
)

/**
 * Publicly visible player state exposed by the engine.
 *
 * @property id Stable player identifier.
 * @property energy Remaining energy points.
 * @property blockCharges Remaining block charges.
 */
data class PlayerState(
  val id: PlayerId,
  val energy: EnergyPoints,
  val blockCharges: BlockCharges,
)

/**
 * Public description of a pending block effect.
 *
 * The engine surfaces these values so a UI can explain why an action is
 * restricted or why an upcoming turn will behave differently.
 */
sealed interface PendingBlockState {
  /** Blocks one category of investigation questions. */
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : PendingBlockState

  /** Removes misplaced digits from the next attempt feedback. */
  data object AttemptClue : PendingBlockState

  /** Adds an energy surcharge to the next action. */
  data object EnergySurcharge : PendingBlockState
}
