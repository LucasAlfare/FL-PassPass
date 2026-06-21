package com.lucasalfare.flpasspass.domain.model.block

/**
 * Categories of investigation that can be temporarily blocked.
 *
 * The block targets mirror the question families in
 * [com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion].
 */
enum class InvestigationBlockTarget {
  DIGIT_EXISTS,
  DIGIT_AT_POSITION,
  POSITION_CHARACTERISTIC,
  POSITION_COMPARISON,
}

/**
 * All block actions available in the game.
 *
 * The game intentionally keeps block actions limited and explicit so the
 * resulting state remains easy to explain in the UI and easy for bots to model.
 */
sealed interface BlockAction {
  /** Blocks one category of investigation questions on the opponent's next turn. */
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : BlockAction

  /** Reduces the clue quality of the opponent's next code attempt. */
  data object AttemptClue : BlockAction

  /** Increases the energy cost of the opponent's next action. */
  data object EnergySurcharge : BlockAction
}
