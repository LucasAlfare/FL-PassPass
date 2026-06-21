package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PlayerSnapshot

internal class StartGameInteractor {
  fun execute(command: StartGameCommand): GameSession {
    require(command.firstPlayer.id != command.secondPlayer.id) {
      "Players in a game must have different ids."
    }

    return GameSession(
      players = listOf(
        command.firstPlayer.toSnapshot(),
        command.secondPlayer.toSnapshot(),
      ),
      activePlayerId = command.firstPlayer.id,
    )
  }
}

private fun PlayerSetup.toSnapshot(): PlayerSnapshot {
  return PlayerSnapshot(
    id = id,
    secretCode = secretCode,
  )
}
