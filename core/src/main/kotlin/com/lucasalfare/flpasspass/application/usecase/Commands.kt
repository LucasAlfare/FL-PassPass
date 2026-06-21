package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.action.TurnAction

/**
 * Input used to bootstrap a player into a new game session.
 *
 * This command object keeps the start-game use case explicit and easy to extend
 * without forcing the engine to depend on a more complex initialization model.
 *
 * @property id Identifier assigned to the player.
 * @property secretCode Hidden code the player will defend during the match.
 */
internal data class PlayerSetup(
  val id: PlayerId,
  val secretCode: Code,
)

/**
 * Command object that starts a new game session.
 *
 * The two-player constraint is enforced by the interactor and by the session
 * model, which keeps the game flow simple and deterministic.
 */
internal data class StartGameCommand(
  val firstPlayer: PlayerSetup,
  val secondPlayer: PlayerSetup,
)

/**
 * Command object representing a submitted turn.
 *
 * The UI and bot layers both translate player input into this command before it
 * reaches the application logic.
 *
 * @property playerId The player attempting to play the turn.
 * @property action The action chosen for this turn.
 */
internal data class SubmitTurnCommand(
  val playerId: PlayerId,
  val action: TurnAction,
)
