package com.lucasalfare.flpasspass.bot

import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.PositionIndex
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget
import com.lucasalfare.flpasspass.domain.model.question.DigitCharacteristic
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import kotlin.random.Random

/**
 * Starter bot strategy with intentionally simple heuristics.
 *
 * The implementation is designed to be understandable first and strong later.
 * It gives the project an immediately usable bot while leaving room for more
 * advanced strategies to replace or complement it.
 */
class SimpleHeuristicBotStrategy : BotStrategy {
  /**
   * Chooses between investigation, blocking and direct guessing using a small deterministic heuristic.
   */
  override fun decide(context: BotContext): BotDecision {
    val self = context.selfState
    val opponent = context.opponentState
    val seed = context.stableSeed()

    return when {
      shouldAttempt(context, self, opponent) -> BotDecision(
        action = TurnAction.AttemptCode(buildGuess(seed)),
        confidence = 0.35,
        reason = "A simple finishing attempt looks affordable right now.",
        trace = listOf(
          "mode=attempt",
          "seed=$seed",
          "selfEnergy=${self.energy.value}",
          "opponentEnergy=${opponent.energy.value}",
        ),
      )

      shouldBlock(context, self, opponent) -> BotDecision(
        action = TurnAction.Block(buildBlockAction(seed, context.memory.observations.size)),
        confidence = 0.55,
        reason = "Delay the opponent and create room for future turns.",
        trace = listOf(
          "mode=block",
          "seed=$seed",
          "blockCharges=${self.blockCharges.value}",
        ),
      )

      else -> BotDecision(
        action = TurnAction.Investigate(buildInvestigationQuestion(seed, context.memory.observations.size)),
        confidence = 0.75,
        reason = "Gather more information before spending 5 energy.",
        trace = listOf(
          "mode=investigate",
          "seed=$seed",
          "observations=${context.memory.observations.size}",
        ),
      )
    }
  }

  /**
   * Returns true when the bot believes it is worth spending energy on a direct attempt.
   */
  private fun shouldAttempt(
    context: BotContext,
    self: com.lucasalfare.flpasspass.engine.PlayerState,
    opponent: com.lucasalfare.flpasspass.engine.PlayerState,
  ): Boolean {
    return self.energy.value >= 5 && (
      opponent.energy.value <= 8 ||
        context.memory.observations.size >= 6
      )
  }

  /**
   * Returns true when the bot prefers to disrupt the opponent instead of gathering more data.
   */
  private fun shouldBlock(
    context: BotContext,
    self: com.lucasalfare.flpasspass.engine.PlayerState,
    opponent: com.lucasalfare.flpasspass.engine.PlayerState,
  ): Boolean {
    return self.energy.value >= 3 &&
      self.blockCharges.value > 0 &&
      opponent.energy.value >= self.energy.value &&
      context.memory.observations.size % 2 == 0
  }

  /**
   * Produces a valid four-digit guess with unique digits.
   */
  private fun buildGuess(seed: Int): Code {
    val digits = (0..9).shuffled(Random(seed)).take(4)
    return Code.of(digits[0], digits[1], digits[2], digits[3])
  }

  /**
   * Chooses which block action the bot should spend on the current turn.
   */
  private fun buildBlockAction(seed: Int, observationCount: Int): BlockAction {
    return when ((seed + observationCount) % 3) {
      0 -> BlockAction.AttemptClue
      1 -> BlockAction.EnergySurcharge
      else -> BlockAction.Investigation(
        when ((seed + observationCount) % 4) {
          0 -> InvestigationBlockTarget.DIGIT_EXISTS
          1 -> InvestigationBlockTarget.DIGIT_AT_POSITION
          2 -> InvestigationBlockTarget.POSITION_CHARACTERISTIC
          else -> InvestigationBlockTarget.POSITION_COMPARISON
        },
      )
    }
  }

  /**
   * Generates a concrete investigation question from a small deterministic pattern set.
   */
  private fun buildInvestigationQuestion(seed: Int, observationCount: Int): InvestigationQuestion {
    return when ((seed + observationCount) % 4) {
      0 -> InvestigationQuestion.DigitExists(Digit((seed + observationCount) % 10))
      1 -> InvestigationQuestion.DigitAtPosition(
        position = PositionIndex((seed % 4) + 1),
        digit = Digit(((seed / 10) + observationCount) % 10),
      )
      2 -> InvestigationQuestion.PositionCharacteristic(
        position = PositionIndex(((seed / 3) % 4) + 1),
        characteristic = when ((seed / 7 + observationCount) % 4) {
          0 -> DigitCharacteristic.EVEN
          1 -> DigitCharacteristic.ODD
          2 -> DigitCharacteristic.GREATER_THAN_FIVE
          else -> DigitCharacteristic.LESS_THAN_FIVE
        },
      )
      else -> {
        val leftPosition = ((seed + observationCount) % 4) + 1
        val rightPosition = (((seed / 5) + observationCount) % 4) + 1
        InvestigationQuestion.PositionComparison(
          leftPosition = PositionIndex(leftPosition),
          rightPosition = PositionIndex(if (rightPosition == leftPosition) (rightPosition % 4) + 1 else rightPosition),
        )
      }
    }
  }

  /**
   * Builds a stable pseudo-random seed so the bot remains reproducible across identical states.
   */
  private fun BotContext.stableSeed(): Int {
    return selfPlayerId.value * 31 +
      gameState.players.sumOf { it.energy.value * 7 + it.blockCharges.value } +
      memory.observations.size * 13 +
      (gameState.winnerId?.value ?: 0)
  }
}
