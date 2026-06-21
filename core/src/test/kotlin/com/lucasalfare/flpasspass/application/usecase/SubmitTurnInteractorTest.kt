package com.lucasalfare.flpasspass.application.usecase

import com.lucasalfare.flpasspass.application.model.GameSession
import com.lucasalfare.flpasspass.application.model.PendingBlockEffect
import com.lucasalfare.flpasspass.application.model.PlayerSnapshot
import com.lucasalfare.flpasspass.domain.model.BlockCharges
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.EnergyPoints
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.GuessFeedback
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubmitTurnInteractorTest {

    private val interactor = SubmitTurnInteractor()

    private val session = GameSession(
        players = listOf(
            PlayerSnapshot(PlayerId(1), Code.of(7, 2, 9, 4), EnergyPoints(25), BlockCharges(2)),
            PlayerSnapshot(PlayerId(2), Code.of(1, 3, 5, 8), EnergyPoints(25), BlockCharges(2)),
        ),
        activePlayerId = PlayerId(1),
    )

    @Test
    fun `submit turn accepts action from the active player`() {
        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
            ),
            session,
        )

        assertEquals(24, result.session.players.first { it.id == PlayerId(1) }.energy.value)
        assertEquals(PlayerId(2), result.session.activePlayerId)
        assertEquals(null, result.session.winnerId)
        assertEquals(null, result.feedback)
        assertEquals(true, result.investigationAnswer)
    }

    @Test
    fun `submit turn rejects action from a non active player`() {
        assertFailsWith<IllegalArgumentException> {
            interactor.execute(
                SubmitTurnCommand(
                    playerId = PlayerId(2),
                    action = TurnAction.AttemptCode(Code.of(7, 2, 9, 4)),
                ),
                session,
            )
        }
    }

    @Test
    fun `submit turn rejects finished games`() {
        val finishedSession = session.copy(winnerId = PlayerId(1))

        assertFailsWith<IllegalArgumentException> {
            interactor.execute(
                SubmitTurnCommand(
                    playerId = PlayerId(1),
                    action = TurnAction.Block(
                        com.lucasalfare.flpasspass.domain.model.block.BlockAction.AttemptClue,
                    ),
                ),
                finishedSession,
            )
        }
    }

    @Test
    fun `submit investigation rejects when active player has no energy`() {
        val exhaustedSession = session.copy(
            players = session.players.map {
                if (it.id == PlayerId(1)) it.copy(energy = EnergyPoints(0)) else it
            },
        )

        assertFailsWith<IllegalArgumentException> {
            interactor.execute(
                SubmitTurnCommand(
                    playerId = PlayerId(1),
                    action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
                ),
                exhaustedSession,
            )
        }
    }

    @Test
    fun `submit investigation ends the game when the last energy is spent`() {
        val lastEnergySession = session.copy(
            players = session.players.map {
                if (it.id == PlayerId(1)) it.copy(energy = EnergyPoints(1)) else it
            },
        )

        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
            ),
            lastEnergySession,
        )

        assertEquals(0, result.session.players.first { it.id == PlayerId(1) }.energy.value)
        assertEquals(PlayerId(2), result.session.winnerId)
    }

    @Test
    fun `submit turn rejects when the player cannot afford the action cost`() {
        val lowEnergySession = session.copy(
            players = session.players.map {
                if (it.id == PlayerId(1)) it.copy(energy = EnergyPoints(4)) else it
            },
        )

        assertFailsWith<IllegalArgumentException> {
            interactor.execute(
                SubmitTurnCommand(
                    playerId = PlayerId(1),
                    action = TurnAction.AttemptCode(Code.of(7, 2, 9, 4)),
                ),
                lowEnergySession,
            )
        }
    }

    @Test
    fun `submit code attempt returns feedback and winner when code is correct`() {
        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.AttemptCode(Code.of(1, 3, 5, 8)),
            ),
            session,
        )

        assertEquals(GuessFeedback(correctPositions = 4, misplacedDigits = 0), result.feedback)
        assertEquals(PlayerId(1), result.session.winnerId)
        assertEquals(20, result.session.players.first { it.id == PlayerId(1) }.energy.value)
    }

    @Test
    fun `submit code attempt consumes energy and passes turn when code is wrong`() {
        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.AttemptCode(Code.of(7, 9, 4, 1)),
            ),
            session,
        )

        assertEquals(GuessFeedback(correctPositions = 0, misplacedDigits = 1), result.feedback)
        assertEquals(20, result.session.players.first { it.id == PlayerId(1) }.energy.value)
        assertEquals(PlayerId(2), result.session.activePlayerId)
        assertEquals(null, result.session.winnerId)
    }

    @Test
    fun `submit code attempt loses the game when the last energy is spent`() {
        val lastEnergySession = session.copy(
            players = session.players.map {
                if (it.id == PlayerId(1)) it.copy(energy = EnergyPoints(5)) else it
            },
        )

        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.AttemptCode(Code.of(1, 3, 5, 8)),
            ),
            lastEnergySession,
        )

        assertEquals(0, result.session.players.first { it.id == PlayerId(1) }.energy.value)
        assertEquals(PlayerId(2), result.session.winnerId)
        assertEquals(GuessFeedback(correctPositions = 4, misplacedDigits = 0), result.feedback)
    }

    @Test
    fun `submit block action creates a pending investigation block`() {
        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(1),
                action = TurnAction.Block(BlockAction.Investigation(InvestigationBlockTarget.DIGIT_EXISTS)),
            ),
            session,
        )

        assertEquals(22, result.session.players.first { it.id == PlayerId(1) }.energy.value)
        assertEquals(1, result.session.players.first { it.id == PlayerId(1) }.blockCharges.value)
        assertEquals(PlayerId(2), result.session.activePlayerId)
        assertEquals(PendingBlockEffect.Investigation(InvestigationBlockTarget.DIGIT_EXISTS), result.session.pendingBlock)
    }

    @Test
    fun `submit blocked investigation category is rejected`() {
        val blockedSession = session.copy(
            activePlayerId = PlayerId(2),
            pendingBlock = PendingBlockEffect.Investigation(InvestigationBlockTarget.DIGIT_EXISTS),
        )

        assertFailsWith<IllegalArgumentException> {
            interactor.execute(
                SubmitTurnCommand(
                    playerId = PlayerId(2),
                    action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
                ),
                blockedSession,
            )
        }
    }

    @Test
    fun `submit attempt clue block removes misplaced digits from feedback`() {
        val blockedSession = session.copy(
            activePlayerId = PlayerId(2),
            pendingBlock = PendingBlockEffect.AttemptClue,
        )

        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(2),
                action = TurnAction.AttemptCode(Code.of(7, 9, 4, 1)),
            ),
            blockedSession,
        )

        assertEquals(GuessFeedback(correctPositions = 1, misplacedDigits = 0), result.feedback)
        assertEquals(null, result.session.pendingBlock)
    }

    @Test
    fun `submit energy surcharge block increases the next action cost`() {
        val blockedSession = session.copy(
            players = session.players.map {
                if (it.id == PlayerId(2)) it.copy(energy = EnergyPoints(4)) else it
            },
            activePlayerId = PlayerId(2),
            pendingBlock = PendingBlockEffect.EnergySurcharge,
        )

        val result = interactor.execute(
            SubmitTurnCommand(
                playerId = PlayerId(2),
                action = TurnAction.Investigate(InvestigationQuestion.DigitExists(Digit(8))),
            ),
            blockedSession,
        )

        assertEquals(0, result.session.players.first { it.id == PlayerId(2) }.energy.value)
        assertEquals(PlayerId(1), result.session.winnerId)
        assertEquals(null, result.session.pendingBlock)
    }
}
