package com.lucasalfare.flpasspass.bot

/**
 * Mutable reasoning memory carried across bot turns.
 *
 * The structure is intentionally tiny for now, but it provides a stable place
 * to accumulate observations, deductions, probabilities, or any future
 * heuristics without changing the public bot API.
 *
 * @property observations The chronological log of observations the bot has received.
 */
data class BotMemory(
  val observations: List<BotObservation> = emptyList(),
) {

  /** Returns a new memory instance with the supplied observation appended. */
  fun record(observation: BotObservation): BotMemory {
    return copy(observations = observations + observation)
  }
}
