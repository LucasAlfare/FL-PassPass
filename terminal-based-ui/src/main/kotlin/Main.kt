import com.lucasalfare.flpasspass.bot.BotRuntime
import com.lucasalfare.flpasspass.domain.model.Code
import com.lucasalfare.flpasspass.domain.model.Digit
import com.lucasalfare.flpasspass.domain.model.PlayerId
import com.lucasalfare.flpasspass.domain.model.PositionIndex
import com.lucasalfare.flpasspass.domain.model.action.TurnAction
import com.lucasalfare.flpasspass.domain.model.block.BlockAction
import com.lucasalfare.flpasspass.domain.model.block.InvestigationBlockTarget
import com.lucasalfare.flpasspass.domain.model.question.DigitCharacteristic
import com.lucasalfare.flpasspass.domain.model.question.InvestigationQuestion
import com.lucasalfare.flpasspass.engine.GameCommand
import com.lucasalfare.flpasspass.engine.GameEngine
import com.lucasalfare.flpasspass.engine.GameState
import com.lucasalfare.flpasspass.engine.PendingBlockState
import com.lucasalfare.flpasspass.engine.PlayerSetupInput
import java.util.Scanner

fun runGameLoop(engine: GameEngine) {
  val scanner = Scanner(System.`in`)

  val humanId = PlayerId(1)
  val botId = PlayerId(2)

  val botRuntime = BotRuntime()

  var response = engine.handle(
    GameCommand.StartGame(
      firstPlayer = PlayerSetupInput(humanId, Code.of(7, 2, 9, 4)),
      secondPlayer = PlayerSetupInput(botId, Code.of(7, 2, 9, 4))
    )
  )

  while (true) {

    renderState(response.state)

    if (response.state.winnerId != null) {
      println("JOGO ENCERRADO - Vencedor: ${response.state.winnerId}")
      break
    }

    if (response.state.activePlayerId == humanId) {

      println("Sua vez. Escolha uma ação:")
      println("1 - Investigar")
      println("2 - Tentar código")
      println("3 - Bloquear")

      val choice = scanner.nextLine().trim()

      val action = readHumanAction(choice, scanner)

      response = engine.handle(
        GameCommand.SubmitTurn(
          playerId = humanId,
          action = action
        )
      )

      println("Resultado:")
      println(response.feedback ?: response.investigationAnswer)

    } else {

      println("Vez do bot...")

      val botMove = botRuntime.decide(response.state, botId)

      println("Bot fez: ${botMove.action}")

      response = engine.handle(
        GameCommand.SubmitTurn(
          playerId = botId,
          action = botMove.action
        )
      )

      println("Resultado:")
      println(response.feedback ?: response.investigationAnswer)
    }

    println("\n-----------------------------\n")
  }
}


/* ===================== RENDER STATE ===================== */

fun renderState(state: GameState) {
  println("\n========== GAME STATE ==========")

  println("Turno atual: ${state.activePlayerId}")

  state.players.forEach { p ->
    println("\nPlayer ${p.id}")
    println("  Energia: ${p.energy}")
    println("  Block charges: ${p.blockCharges}")
  }

  state.pendingBlock?.let { block ->
    println("\nBLOCK ATIVO:")

    when (block) {
      is PendingBlockState.Investigation ->
        println("  Investigation bloqueado: ${block.target}")

      PendingBlockState.AttemptClue ->
        println("  Attempt clue ativo")

      PendingBlockState.EnergySurcharge ->
        println("  Energy surcharge ativo")
    }
  }

  println("================================\n")
}


/* ===================== HUMAN INPUT ===================== */

fun readHumanAction(choice: String, scanner: Scanner): TurnAction {
  return when (choice) {

    "1" -> {
      println("Tipo de investigação:")
      println("1 - Dígito existe")
      println("2 - Dígito na posição")
      println("3 - Característica")
      println("4 - Comparação")

      when (scanner.nextLine().trim()) {

        "1" -> {
          println("Dígito:")
          val digit = Digit(scanner.nextLine().trim().toInt())

          TurnAction.Investigate(
            InvestigationQuestion.DigitExists(digit)
          )
        }

        "2" -> {
          println("Posição:")
          val pos = PositionIndex(scanner.nextLine().trim().toInt())

          println("Dígito:")
          val digit = Digit(scanner.nextLine().trim().toInt())

          TurnAction.Investigate(
            InvestigationQuestion.DigitAtPosition(pos, digit)
          )
        }

        "3" -> {
          println("Posição:")
          val pos = PositionIndex(scanner.nextLine().trim().toInt())

          println("Característica:")
          val characteristic = DigitCharacteristic.valueOf(scanner.nextLine().trim())

          TurnAction.Investigate(
            InvestigationQuestion.PositionCharacteristic(pos, characteristic)
          )
        }

        "4" -> {
          println("Posição esquerda:")
          val left = PositionIndex(scanner.nextLine().trim().toInt())

          println("Posição direita:")
          val right = PositionIndex(scanner.nextLine().trim().toInt())

          TurnAction.Investigate(
            InvestigationQuestion.PositionComparison(left, right)
          )
        }

        else -> error("Entrada inválida")
      }
    }

    "2" -> {
      println("Código (4 números separados por espaço):")
      val parts = scanner.nextLine().split(" ")

      TurnAction.AttemptCode(
        Code.of(
          parts[0].toInt(),
          parts[1].toInt(),
          parts[2].toInt(),
          parts[3].toInt()
        )
      )
    }

    "3" -> {
      val target = readInvestigationBlockTarget(scanner)

      TurnAction.Block(
        BlockAction.Investigation(target)
      )
    }

    else -> error("Entrada inválida")
  }
}


/* ===================== BLOCK INPUT ===================== */

fun readInvestigationBlockTarget(scanner: Scanner): InvestigationBlockTarget {
  println("Escolha o target do bloqueio:")

  println("1 - DIGIT_EXISTS")
  println("2 - DIGIT_AT_POSITION")
  println("3 - POSITION_CHARACTERISTIC")
  println("4 - POSITION_COMPARISON")

  return when (scanner.nextLine().trim()) {
    "1" -> InvestigationBlockTarget.DIGIT_EXISTS
    "2" -> InvestigationBlockTarget.DIGIT_AT_POSITION
    "3" -> InvestigationBlockTarget.POSITION_CHARACTERISTIC
    "4" -> InvestigationBlockTarget.POSITION_COMPARISON
    else -> {
      println("Entrada inválida, usando DIGIT_EXISTS.")
      InvestigationBlockTarget.DIGIT_EXISTS
    }
  }
}

fun main() {
  runGameLoop(GameEngine())
}