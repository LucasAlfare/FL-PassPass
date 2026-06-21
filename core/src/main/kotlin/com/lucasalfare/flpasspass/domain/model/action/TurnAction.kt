package com.lucasalfare.flpasspass.domain.model.action

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion

sealed interface TurnAction {
  data class Investigate(val question: InvestigationQuestion) : TurnAction

  data class AttemptCode(val code: Code) : TurnAction

  data class Block(val block: BlockAction) : TurnAction
}
