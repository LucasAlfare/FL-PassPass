package com.lucasalfare.flpasspass.domain.model.action

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion

/**
 * The complete set of actions a player can perform on a turn.
 *
 * This is the central command type shared by the engine, the UI and the bot
 * module, which makes it the main integration point for future front ends.
 */
sealed interface TurnAction {
  /** Performs one allowed investigation question. */
  data class Investigate(val question: InvestigationQuestion) : TurnAction

  /** Attempts to guess the opponent's code directly. */
  data class AttemptCode(val code: Code) : TurnAction

  /** Spends a block charge to apply a disruptive effect to the opponent. */
  data class Block(val block: BlockAction) : TurnAction
}
