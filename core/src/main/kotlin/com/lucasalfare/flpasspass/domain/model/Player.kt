package com.lucasalfare.flpasspass.domain.model

/**
 * Default resource bundle granted to a player at the start of a game.
 *
 * @property energy The starting energy budget used to pay action costs.
 * @property blockCharges The number of block actions available before depletion.
 */
data class PlayerResources(
  val energy: EnergyPoints = EnergyPoints(25),
  val blockCharges: BlockCharges = BlockCharges(2),
)

/**
 * Immutable domain representation of a player.
 *
 * The domain keeps the player's secret code attached to the player entity so
 * the engine can resolve feedback and investigation results without exposing
 * the code to front ends.
 *
 * @property id Stable player identity inside a session.
 * @property secretCode Hidden code that the opponent must discover.
 * @property resources Starting and current resource values used by the game rules.
 */
data class Player(
  val id: PlayerId,
  val secretCode: Code,
  val resources: PlayerResources = PlayerResources(),
)
