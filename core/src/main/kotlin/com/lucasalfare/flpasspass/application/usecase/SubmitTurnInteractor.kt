package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PendingBlockEffect
import com.lucasalfare.flpasspass.application.model.SubmitTurnResult
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget
import com.lucasalfare.flpasspass.domain.model.blockTarget
import com.lucasalfare.flpasspass.domain.model.matches
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import com.lucasalfare.flpasspass.domain.model.scoreAttempt
import com.lucasalfare.flpasspass.domain.model.action.TurnAction

internal class SubmitTurnInteractor {
    fun execute(command: SubmitTurnCommand, session: GameSession): SubmitTurnResult {
        require(session.winnerId == null) { "The game is already finished." }
        require(command.playerId == session.activePlayerId) { "Only the active player can submit a turn." }

        ensureActionIsAllowed(command.action, session.pendingBlock)

        val energyCost = command.action.energyCost(session.pendingBlock)
        val activePlayer = session.players.first { it.id == session.activePlayerId }
        require(activePlayer.energy.value >= energyCost) { "Active player does not have enough energy for this action." }

        return when (command.action) {
            is TurnAction.Investigate -> submitInvestigation(session, command.action.question, energyCost)
            is TurnAction.AttemptCode -> submitAttemptCode(session, command.action.code, energyCost)
            is TurnAction.Block -> submitBlock(session, command.action.block, energyCost)
        }
    }

    private fun submitInvestigation(
        session: GameSession,
        question: InvestigationQuestion,
        energyCost: Int,
    ): SubmitTurnResult {
        val activePlayer = session.players.first { it.id == session.activePlayerId }
        val opponent = session.players.first { it.id != session.activePlayerId }
        val remainingEnergy = activePlayer.energy.value - energyCost
        val updatedPlayers = session.players.map { player ->
            if (player.id == activePlayer.id) {
                player.copy(energy = EnergyPoints(remainingEnergy))
            } else {
                player
            }
        }

        val updatedSession = if (remainingEnergy == 0) {
            session.copy(
                players = updatedPlayers,
                winnerId = opponent.id,
                pendingBlock = null,
            )
        } else {
            session.copy(
                players = updatedPlayers,
                activePlayerId = opponent.id,
                pendingBlock = null,
            )
        }

        return SubmitTurnResult(
            session = updatedSession,
            investigationAnswer = opponent.secretCode.matches(question),
        )
    }

    private fun submitAttemptCode(
        session: GameSession,
        guessedCode: Code,
        energyCost: Int,
    ): SubmitTurnResult {
        val activePlayer = session.players.first { it.id == session.activePlayerId }
        val opponent = session.players.first { it.id != session.activePlayerId }
        val remainingEnergy = activePlayer.energy.value - energyCost
        val feedback = opponent.secretCode.scoreAttempt(guessedCode)
        val adjustedFeedback = if (session.pendingBlock is PendingBlockEffect.AttemptClue) {
            feedback.copy(misplacedDigits = 0)
        } else {
            feedback
        }
        val updatedPlayers = session.players.map { player ->
            if (player.id == activePlayer.id) {
                player.copy(energy = EnergyPoints(remainingEnergy))
            } else {
                player
            }
        }

        val updatedSession = when {
            remainingEnergy == 0 -> session.copy(
                players = updatedPlayers,
                winnerId = opponent.id,
                pendingBlock = null,
            )
            adjustedFeedback.correctPositions == Code.LENGTH -> session.copy(
                players = updatedPlayers,
                winnerId = activePlayer.id,
                pendingBlock = null,
            )
            else -> session.copy(
                players = updatedPlayers,
                activePlayerId = opponent.id,
                pendingBlock = null,
            )
        }

        return SubmitTurnResult(
            session = updatedSession,
            feedback = adjustedFeedback,
        )
    }

    private fun submitBlock(
        session: GameSession,
        block: BlockAction,
        energyCost: Int,
    ): SubmitTurnResult {
        val activePlayer = session.players.first { it.id == session.activePlayerId }
        val opponent = session.players.first { it.id != session.activePlayerId }
        require(activePlayer.blockCharges.value > 0) { "Active player does not have any block charges left." }

        val updatedPlayers = session.players.map { player ->
            if (player.id == activePlayer.id) {
                player.copy(
                    energy = EnergyPoints(activePlayer.energy.value - energyCost),
                    blockCharges = com.lucasalfare.flpasspass.domain.model.BlockCharges(activePlayer.blockCharges.value - 1),
                )
            } else {
                player
            }
        }

        val updatedSession = session.copy(
            players = updatedPlayers,
            activePlayerId = opponent.id,
            pendingBlock = block.toPendingEffect(),
        )

        return SubmitTurnResult(session = updatedSession)
    }

    private fun ensureActionIsAllowed(action: TurnAction, pendingBlock: PendingBlockEffect?) {
        if (pendingBlock is PendingBlockEffect.Investigation && action is TurnAction.Investigate) {
            require(action.question.blockTarget() != pendingBlock.target) {
                "That investigation category is blocked."
            }
        }
    }
}

private fun TurnAction.energyCost(pendingBlock: PendingBlockEffect?): Int {
    return when (this) {
        is TurnAction.Investigate -> 1 + if (pendingBlock is PendingBlockEffect.EnergySurcharge) 3 else 0
        is TurnAction.AttemptCode -> 5 + if (pendingBlock is PendingBlockEffect.EnergySurcharge) 3 else 0
        is TurnAction.Block -> 3 + if (pendingBlock is PendingBlockEffect.EnergySurcharge) 3 else 0
    }
}

private fun BlockAction.toPendingEffect(): PendingBlockEffect {
    return when (this) {
        is BlockAction.Investigation -> PendingBlockEffect.Investigation(target)
        BlockAction.AttemptClue -> PendingBlockEffect.AttemptClue
        BlockAction.EnergySurcharge -> PendingBlockEffect.EnergySurcharge
    }
}
