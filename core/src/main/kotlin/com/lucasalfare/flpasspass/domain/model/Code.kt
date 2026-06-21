package com.lucasalfare.flpasspass.domain.model

/**
 * Immutable representation of a secret code used by the game.
 *
 * The code is intentionally tiny and strict: it always contains exactly four
 * unique digits, which makes it suitable for deduction-based gameplay and for
 * deterministic scoring in the core engine.
 *
 * @property digits The ordered digits that compose the code.
 */
data class Code(val digits: List<Digit>) {
  init {
    require(digits.size == LENGTH) { "Code must have exactly $LENGTH digits." }
    require(digits.distinct().size == digits.size) { "Code digits must be unique." }
  }

  companion object {
    /** The number of digits every valid code must contain. */
    const val LENGTH = 4

    /**
     * Creates a validated code from raw integer values.
     *
     * The helper keeps the rest of the project concise by letting callers build
     * codes without manually instantiating each [Digit].
     */
    fun of(vararg digits: Int): Code {
      return Code(digits.map(::Digit))
    }
  }
}
