package com.lucasalfare.flpasspass.domain.model

data class Code(val digits: List<Digit>) {
  init {
    require(digits.size == LENGTH) { "Code must have exactly $LENGTH digits." }
    require(digits.distinct().size == digits.size) { "Code digits must be unique." }
  }

  companion object {
    const val LENGTH = 4

    fun of(vararg digits: Int): Code {
      return Code(digits.map(::Digit))
    }
  }
}
