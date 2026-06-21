package com.lucasalfare.flpasspass.engine

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PendingBlockEffect
import com.lucasalfare.flpasspass.application.usecase.PlayerSetup
import com.lucasalfare.flpasspass.application.usecase.StartGameCommand
import com.lucasalfare.flpasspass.application.usecase.StartGameInteractor
import com.lucasalfare.flpasspass.application.usecase.SubmitTurnCommand
import com.lucasalfare.flpasspass.application.usecase.SubmitTurnInteractor

class GameEngine {
  private val startGameInteractor = StartGameInteractor()
  private val submitTurnInteractor = SubmitTurnInteractor()
  private var session: GameSession? = null

  fun state(): GameState? = session?.toPublicState()

  fun handle(command: GameCommand): GameResponse {
    return when (command) {
      is GameCommand.StartGame -> startGame(command)
      is GameCommand.SubmitTurn -> submitTurn(command)
    }
  }

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

private fun PlayerSetupInput.toInternal(): PlayerSetup {
  return PlayerSetup(
    id = id,
    secretCode = secretCode,
  )
}

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

private fun PendingBlockEffect.toPublicState(): PendingBlockState {
  return when (this) {
    is PendingBlockEffect.Investigation -> PendingBlockState.Investigation(target)
    PendingBlockEffect.AttemptClue -> PendingBlockState.AttemptClue
    PendingBlockEffect.EnergySurcharge -> PendingBlockState.EnergySurcharge
  }
}
