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

  var lastAction: TurnAction? = null

  var response = engine.handle(
    GameCommand.StartGame(
      firstPlayer = PlayerSetupInput(
        humanId,
        Code.of(7, 2, 9, 4)
      ),
      secondPlayer = PlayerSetupInput(
        botId,
        Code.of(7, 2, 9, 4)
      )
    )
  )

  while (true) {

    clearConsole()

    renderState(response.state)

    if (response.state.winnerId != null) {
      println("🏆 Vencedor: ${response.state.winnerId}")
      break
    }

    if (response.state.activePlayerId == humanId) {

      printHumanMenu()

      val input = scanner.nextLine()

      val action = parseHumanAction(input)

      lastAction = action

      response = engine.handle(
        GameCommand.SubmitTurn(
          playerId = humanId,
          action = action
        )
      )

    } else {

      println("Vez do oponente...")

      val botMove = botRuntime.decide(
        response.state,
        botId
      )

      lastAction = botMove.action

      response = engine.handle(
        GameCommand.SubmitTurn(
          playerId = botId,
          action = botMove.action
        )
      )
    }


    response.feedback?.let {
      println()
      println("Resultado do código:")
      println(it)
    }


    response.investigationAnswer?.let {
      renderInvestigationResult(
        lastAction,
        it
      )
    }


    println()
    println("ENTER para continuar...")
    scanner.nextLine()
  }
}


/*
 * =========================
 * CONSOLE
 * =========================
 */

fun clearConsole() {
  print("\u001B[H\u001B[2J")
  System.out.flush()
}


/*
 * =========================
 * MENU
 * =========================
 */

fun printHumanMenu() {

  println("Sua vez. Digite sua jogada:")
  println()

  println("Investigar:")
  println("  1 1 <número>              → Ver se um número existe")
  println("  1 2 <posição> <número>    → Ver número em uma posição")
  println("  1 3 <posição> <tipo>      → Ver característica")
  println("       tipo:")
  println("       1 Par")
  println("       2 Ímpar")
  println("       3 Maior que 5")
  println("       4 Menor que 5")
  println("  1 4 <posição> <posição>   → Comparar posições")

  println()

  println("Tentar código:")
  println("  2 <n1> <n2> <n3> <n4>")

  println()

  println("Bloquear:")
  println("  3 1 <tipo>")
  println("       1 Número existe")
  println("       2 Número na posição")
  println("       3 Característica")
  println("       4 Comparação")
  println("  3 2 → Reduzir dica")
  println("  3 3 → Aumentar custo")

  print("> ")
}


/*
 * =========================
 * PARSER
 * =========================
 */

fun parseHumanAction(input: String): TurnAction {

  val args = input
    .trim()
    .split(" ")
    .filter { it.isNotBlank() }
    .map {
      it.toIntOrNull()
        ?: error("Use apenas números")
    }

  return when (args.firstOrNull()) {

    1 -> parseInvestigation(args.drop(1))

    2 -> parseCode(args.drop(1))

    3 -> parseBlock(args.drop(1))

    else -> error("Ação inválida")
  }
}


fun parseInvestigation(args: List<Int>): TurnAction {

  return when (args.firstOrNull()) {

    1 -> {
      require(args.size == 2)

      TurnAction.Investigate(
        InvestigationQuestion.DigitExists(
          Digit(args[1])
        )
      )
    }


    2 -> {
      require(args.size == 3)

      TurnAction.Investigate(
        InvestigationQuestion.DigitAtPosition(
          PositionIndex(args[1]),
          Digit(args[2])
        )
      )
    }


    3 -> {

      require(args.size == 3)

      val characteristic = when (args[2]) {

        1 -> DigitCharacteristic.EVEN
        2 -> DigitCharacteristic.ODD
        3 -> DigitCharacteristic.GREATER_THAN_FIVE
        4 -> DigitCharacteristic.LESS_THAN_FIVE

        else -> error("Característica inválida")
      }

      TurnAction.Investigate(
        InvestigationQuestion.PositionCharacteristic(
          PositionIndex(args[1]),
          characteristic
        )
      )
    }


    4 -> {

      require(args.size == 3)

      TurnAction.Investigate(
        InvestigationQuestion.PositionComparison(
          PositionIndex(args[1]),
          PositionIndex(args[2])
        )
      )
    }


    else -> error("Investigação inválida")
  }
}


fun parseCode(args: List<Int>): TurnAction {

  require(args.size == 4)

  return TurnAction.AttemptCode(
    Code.of(
      args[0],
      args[1],
      args[2],
      args[3]
    )
  )
}


fun parseBlock(args: List<Int>): TurnAction {

  return when (args.firstOrNull()) {

    1 -> {

      require(args.size == 2)

      val target = when (args[1]) {

        1 -> InvestigationBlockTarget.DIGIT_EXISTS
        2 -> InvestigationBlockTarget.DIGIT_AT_POSITION
        3 -> InvestigationBlockTarget.POSITION_CHARACTERISTIC
        4 -> InvestigationBlockTarget.POSITION_COMPARISON

        else -> error("Tipo inválido")
      }

      TurnAction.Block(
        BlockAction.Investigation(target)
      )
    }


    2 ->
      TurnAction.Block(
        BlockAction.AttemptClue
      )


    3 ->
      TurnAction.Block(
        BlockAction.EnergySurcharge
      )


    else -> error("Bloqueio inválido")
  }
}


/*
 * =========================
 * STATUS
 * =========================
 */

fun renderState(state: GameState) {

  println("=========== STATUS ===========")
  println()

  val human = state.players.first { it.id.value == 1 }
  val opponent = state.players.first { it.id.value == 2 }


  println("Você:")
  println("  Energia: ${human.energy.value}")
  println("  Bloqueios: ${human.blockCharges.value}")


  println()

  println("Oponente:")
  println("  Energia: ${opponent.energy.value}")
  println("  Bloqueios: ${opponent.blockCharges.value}")


  println()

  println(
    "Vez atual: ${
      if (state.activePlayerId == human.id)
        "Você"
      else
        "Oponente"
    }"
  )


  println()

  when (val block = state.pendingBlock) {

    null ->
      println("Efeito ativo: nenhum")


    is PendingBlockState.Investigation ->
      println(
        "Efeito ativo: investigação bloqueada (${block.target})"
      )


    PendingBlockState.AttemptClue ->
      println(
        "Efeito ativo: dica reduzida"
      )


    PendingBlockState.EnergySurcharge ->
      println(
        "Efeito ativo: custo aumentado"
      )
  }


  println()
  println("==============================")
  println()
}


/*
 * =========================
 * INVESTIGATION OUTPUT
 * =========================
 */

fun renderInvestigationResult(
  action: TurnAction?,
  answer: Boolean
) {

  val question =
    (action as? TurnAction.Investigate)?.question


  println()

  println(
    if (answer)
      "🔎 Resultado: SIM"
    else
      "🔎 Resultado: NÃO"
  )


  question?.let {
    println(
      "Pergunta: ${describeQuestion(it)}"
    )
  }
}


fun describeQuestion(
  question: InvestigationQuestion
): String {

  return when (question) {


    is InvestigationQuestion.DigitExists ->
      "O número ${question.digit.value} existe no código?"


    is InvestigationQuestion.DigitAtPosition ->
      "A posição ${question.position.value} contém o número ${question.digit.value}?"


    is InvestigationQuestion.PositionCharacteristic -> {

      val text = when (question.characteristic) {

        DigitCharacteristic.EVEN ->
          "par"

        DigitCharacteristic.ODD ->
          "ímpar"

        DigitCharacteristic.GREATER_THAN_FIVE ->
          "maior que 5"

        DigitCharacteristic.LESS_THAN_FIVE ->
          "menor que 5"
      }

      "O número da posição ${question.position.value} é $text?"
    }


    is InvestigationQuestion.PositionComparison ->
      "O número da posição ${question.leftPosition.value} é maior que o da posição ${question.rightPosition.value}?"
  }
}

fun main() {
  runGameLoop(GameEngine())
}