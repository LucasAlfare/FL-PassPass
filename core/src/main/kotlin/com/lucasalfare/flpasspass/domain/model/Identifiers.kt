package com.lucasalfare.flpasspass.domain.model

/**
 * Strongly typed identifiers and bounded values used by the domain model.
 *
 * Using inline value classes keeps the code expressive while still protecting
 * the rules of the game from invalid raw integers.
 */

/** Identifies a player within a session. */
@JvmInline
value class PlayerId(val value: Int) {
  init {
    require(value > 0) { "PlayerId must be positive." }
  }
}

/** Represents a single numeric digit allowed in a code. */
@JvmInline
value class Digit(val value: Int) {
  init {
    require(value in 0..9) { "Digit must be between 0 and 9." }
  }
}

/** Represents a 1-based position inside a four-digit code. */
@JvmInline
value class PositionIndex(val value: Int) {
  init {
    require(value in 1..4) { "PositionIndex must be between 1 and 4." }
  }
}

/** Encapsulates the energy resource consumed by actions. */
@JvmInline
value class EnergyPoints(val value: Int) {
  init {
    require(value >= 0) { "EnergyPoints cannot be negative." }
  }
}

/** Encapsulates how many block actions a player can still spend. */
@JvmInline
value class BlockCharges(val value: Int) {
  init {
    require(value in 0..2) { "BlockCharges must be between 0 and 2." }
  }
}
