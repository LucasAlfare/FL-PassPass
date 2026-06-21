package com.lucasalfare.flpasspass.application.model

import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.PlayerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Documents the invariants enforced by the internal application session model.
 */
class ApplicationModelTest {

  /** Ensures a valid session can be created with two distinct players and consistent references. */
  @Test
  fun `game session accepts two unique players and valid references`() {
    val session = GameSession(
      players = listOf(
        PlayerSnapshot(PlayerId(1), Code.of(7, 2, 9, 4), EnergyPoints(25), BlockCharges(2)),
        PlayerSnapshot(PlayerId(2), Code.of(1, 3, 5, 8), EnergyPoints(24), BlockCharges(1)),
      ),
      activePlayerId = PlayerId(1),
      winnerId = PlayerId(2),
    )

    assertEquals(2, session.players.size)
    assertEquals(PlayerId(1), session.activePlayerId)
    assertEquals(PlayerId(2), session.winnerId)
  }

  /** Ensures duplicated player identifiers are rejected early. */
  @Test
  fun `game session rejects duplicated players`() {
    assertFailsWith<IllegalArgumentException> {
      GameSession(
        players = listOf(
          PlayerSnapshot(PlayerId(1), Code.of(7, 2, 9, 4), EnergyPoints(25), BlockCharges(2)),
          PlayerSnapshot(PlayerId(1), Code.of(1, 3, 5, 8), EnergyPoints(24), BlockCharges(1)),
        ),
        activePlayerId = PlayerId(1),
      )
    }
  }

  /** Ensures the active player always belongs to the session roster. */
  @Test
  fun `game session rejects active player not in session`() {
    assertFailsWith<IllegalArgumentException> {
      GameSession(
        players = listOf(
          PlayerSnapshot(PlayerId(1), Code.of(7, 2, 9, 4), EnergyPoints(25), BlockCharges(2)),
          PlayerSnapshot(PlayerId(2), Code.of(1, 3, 5, 8), EnergyPoints(24), BlockCharges(1)),
        ),
        activePlayerId = PlayerId(3),
      )
    }
  }

  /** Ensures the winner, when present, also belongs to the session roster. */
  @Test
  fun `game session rejects winner not in session`() {
    assertFailsWith<IllegalArgumentException> {
      GameSession(
        players = listOf(
          PlayerSnapshot(PlayerId(1), Code.of(7, 2, 9, 4), EnergyPoints(25), BlockCharges(2)),
          PlayerSnapshot(PlayerId(2), Code.of(1, 3, 5, 8), EnergyPoints(24), BlockCharges(1)),
        ),
        activePlayerId = PlayerId(1),
        winnerId = PlayerId(3),
      )
    }
  }
}
