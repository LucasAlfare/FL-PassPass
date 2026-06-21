package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PlayerSnapshot

/**
 * Builds a fresh internal game session from the two player setups.
 *
 * The interactor exists so session creation rules stay isolated from the
 * engine facade and from any future UI-specific orchestration.
 */
internal class StartGameInteractor {
  /**
   * Validates the input and creates the initial session state.
   *
   * The first player always starts the game, which keeps the startup rules
   * deterministic and easy to explain in a terminal UI.
   */
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

/**
 * Converts a start-game player setup into the internal session snapshot.
 */
private fun PlayerSetup.toSnapshot(): PlayerSnapshot {
  return PlayerSnapshot(
    id = id,
    secretCode = secretCode,
  )
}
