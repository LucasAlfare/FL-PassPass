package com.lucasalfare.flpasspass.domain.model

@JvmInline
value class PlayerId(val value: Int) {
  init {
    require(value > 0) { "PlayerId must be positive." }
  }
}

@JvmInline
value class Digit(val value: Int) {
  init {
    require(value in 0..9) { "Digit must be between 0 and 9." }
  }
}

@JvmInline
value class PositionIndex(val value: Int) {
  init {
    require(value in 1..4) { "PositionIndex must be between 1 and 4." }
  }
}

@JvmInline
value class EnergyPoints(val value: Int) {
  init {
    require(value >= 0) { "EnergyPoints cannot be negative." }
  }
}

@JvmInline
value class BlockCharges(val value: Int) {
  init {
    require(value in 0..2) { "BlockCharges must be between 0 and 2." }
  }
}
