package com.lucasalfare.flpasspass.engine

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PendingBlockEffect
import com.lucasalfare.flpasspass.application.usecase.PlayerSetup
import com.lucasalfare.flpasspass.application.usecase.StartGameCommand
import com.lucasalfare.flpasspass.application.usecase.StartGameInteractor
import com.lucasalfare.flpasspass.application.usecase.SubmitTurnCommand
import com.lucasalfare.flpasspass.application.usecase.SubmitTurnInteractor

/**
 * Facade that exposes the game logic to UIs and other external consumers.
 *
 * The engine keeps the current session in memory and translates between the
 * internal application model and the public model used by front ends.
 */
class GameEngine {
  private val startGameInteractor = StartGameInteractor()
  private val submitTurnInteractor = SubmitTurnInteractor()
  private var session: GameSession? = null

  /**
   * Returns the current public state, or null before a game has been started.
   */
  fun state(): GameState? = session?.toPublicState()

  /**
   * Routes a public command to the correct application use case and returns the normalized response.
   */
  fun handle(command: GameCommand): GameResponse {
    return when (command) {
      is GameCommand.StartGame -> startGame(command)
      is GameCommand.SubmitTurn -> submitTurn(command)
    }
  }

  /**
   * Starts a new game session after verifying that no unfinished game is already running.
   */
  private fun startGame(command: GameCommand.StartGame): GameResponse {
    val currentSession = session
    require(currentSession == null || currentSession.winnerId != null) { "A game is already in progress." }

    val internalSession = startGameInteractor.execute(
      StartGameCommand(
        firstPlayer = command.firstPlayer.toInternal(),
        secondPlayer = command.secondPlayer.toInternal(),
      ),
    )

    session = internalSession

    return GameResponse(state = internalSession.toPublicState())
  }

  /**
   * Applies a submitted turn to the active session and stores the resulting state.
   */
  private fun submitTurn(command: GameCommand.SubmitTurn): GameResponse {
    val currentSession = requireNotNull(session) { "The game has not started yet." }
    val result = submitTurnInteractor.execute(
      SubmitTurnCommand(
        playerId = command.playerId,
        action = command.action,
      ),
      currentSession,
    )

    session = result.session

    return GameResponse(
      state = result.session.toPublicState(),
      feedback = result.feedback,
      investigationAnswer = result.investigationAnswer,
    )
  }
}

/**
 * Converts a public player setup input into the internal setup command.
 */
private fun PlayerSetupInput.toInternal(): PlayerSetup {
  return PlayerSetup(
    id = id,
    secretCode = secretCode,
  )
}

/**
 * Projects the internal session model into the public engine state.
 */
private fun GameSession.toPublicState(): GameState {
  return GameState(
    players = players.map {
      PlayerState(
        id = it.id,
        energy = it.energy,
        blockCharges = it.blockCharges,
      )
    },
    activePlayerId = activePlayerId,
    winnerId = winnerId,
    pendingBlock = pendingBlock?.toPublicState(),
  )
}

/**
 * Converts an internal pending block effect into the public representation used by UIs.
 */
private fun PendingBlockEffect.toPublicState(): PendingBlockState {
  return when (this) {
    is PendingBlockEffect.Investigation -> PendingBlockState.Investigation(target)
    PendingBlockEffect.AttemptClue -> PendingBlockState.AttemptClue
    PendingBlockEffect.EnergySurcharge -> PendingBlockState.EnergySurcharge
  }
}
