package com.lucasalfare.flpasspass.domain.model.block

enum class InvestigationBlockTarget {
  DIGIT_EXISTS,
  DIGIT_AT_POSITION,
  POSITION_CHARACTERISTIC,
  POSITION_COMPARISON,
}

sealed interface BlockAction {
  data class Investigation(
    val target: InvestigationBlockTarget,
  ) : BlockAction

  data object AttemptClue : BlockAction

  data object EnergySurcharge : BlockAction
}
