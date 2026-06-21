package com.lucasalfare.flpasspass.domain.model

data class PlayerResources(
  val energy: EnergyPoints = EnergyPoints(25),
  val blockCharges: BlockCharges = BlockCharges(2),
)

data class Player(
  val id: PlayerId,
  val secretCode: Code,
  val resources: PlayerResources = PlayerResources(),
)
