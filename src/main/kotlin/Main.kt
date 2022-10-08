typealias GameGrid = MutableList<MutableList<String>>

class Player(val name: String, val token: String, var score: Int = 0)

fun main() {
    println("Connect Four") // program name

    println("First player's name:\n>") // first players name
    val firstPlayer = Player(readLine()!!, "o") //assign player a token

    println("Second player's name:\n>") // second players name
    val secondPlayer = Player(readLine()!!, "*") // assign player a different token

    val (rows, columns) = getBoardDimensions() // asks user for input until valid board dimensions are given
    val numberOfGamesToBePlayed = numberOfGamesWanted() // asks for user input until valid number of games given

    println("${firstPlayer.name} VS ${secondPlayer.name}") // who's playing
    println("$rows X $columns board") // board dimensions chosen
    println(numberOfGames(numberOfGamesToBePlayed)) // number of games to be played chosen

    var gameCounter = 0

    out@do {
        gameCounter++
        if(numberOfGamesToBePlayed > 1) println("Game #$gameCounter")
        val currentGameGrid: GameGrid = initialGameGrid(rows, columns)
        printBoard(currentGameGrid) // prints blank board
        var turnCounter = gameCounter % 2
        do {
            turnCounter++
            val player = if (turnCounter % 2 == 0) firstPlayer else secondPlayer
            val opponent = if (turnCounter % 2 != 0) firstPlayer else secondPlayer
            val playerInput = getUserInput(player.name, currentGameGrid) ?: break@out
        } while (playerTurn(playerInput, player, opponent, currentGameGrid))
        if (numberOfGamesToBePlayed > 1) {
            println("Score")
            println("${firstPlayer.name}: ${firstPlayer.score} ${secondPlayer.name}: ${secondPlayer.score}")
        }
    } while (gameCounter < numberOfGamesToBePlayed)

    println("Game over!")
}

fun getBoardDimensions(): MutableList<Int> {
    var rows: Int // rows
    var columns: Int // columns

    while (true) {
        println("Set the board dimensions (Rows x Columns)") // setting board dimensions
        println("Press Enter for default (6 x 7)\n>")
        val dimensionsInputString = readLine()!!.replace(" ","").replace("\t", "").lowercase()
        val regex = Regex("\\d+x\\d+") // check for number x number
        if (dimensionsInputString == "") {
            rows = 6
            columns = 7
        } else if (!dimensionsInputString.matches(regex)) {
            println("Invalid input")
            continue
        } else {
            val split = dimensionsInputString.split("x") // split into rows and columns
            rows = split[0].toInt()
            columns = split[1].toInt()
        }
        if (rows in 5..9 && columns in 5..9) break
        if (rows !in 5..9) println("Board rows should be from 5 to 9")
        if (columns !in 5..9) println("Board columns should be from 5 to 9")
    }

    return mutableListOf(rows, columns)
}

fun numberOfGamesWanted(): Int {
    while(true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:\n>")
        val input = readLine()!!
        if (input.isBlank()) return 1
        val numberOfGames = input.toIntOrNull()
        if (numberOfGames == null || numberOfGames < 1){
            println("Invalid input")
            continue
        } else {
            return numberOfGames
        }
    }
}

fun numberOfGames(numberOfGamesWanted: Int): String {
    return when (numberOfGamesWanted) {
        1 -> "Single game"
        in 1..10_000 -> "Total $numberOfGamesWanted games"
        else -> "too many games - you'll be here all day!"
    }
}

fun printBoard(currentGameGrid: GameGrid) {
    val rows = currentGameGrid.size
    val columns = currentGameGrid[0].size

    // print numbers at the top
    for (i in 1..columns) {
        print(" $i")
    }
    println()

    // print columns
    for (i in 0 until rows) {
        for (j in 0 until columns){
            print("║")
            print(currentGameGrid[i][j])
        }
        println("║")
    }

    // print bottom row
    print("╚═")
    repeat(columns - 1) {
        print("╩═")
    }
    println("╝")
}

fun initialGameGrid(rows:Int, columns:Int): GameGrid {
    val rowsOfGameGrid = mutableListOf<MutableList<String>>()
    repeat(rows) {
        val row = mutableListOf<String>()
        repeat(columns) {
            row.add(" ")
        }
        rowsOfGameGrid.add(row)
    }
    return rowsOfGameGrid
}

fun gameGrid(currentGameGrid: GameGrid, chosenColumn:Int, token: String): GameGrid {
    val rows = currentGameGrid.size
    for (i in rows - 1 downTo 0) {
        if (currentGameGrid[i][chosenColumn - 1] == " ") {
            currentGameGrid[i][chosenColumn - 1] = token
            break
        }
    }
    return currentGameGrid
}

fun getUserInput(playerName: String, currentGameGrid: GameGrid): Int? {
    val columns = currentGameGrid[0].size
    while (true) {
        println("$playerName's turn:\n>")
        val input = readLine()!!
        if (input == "end") return null
        val columnInput = input.toIntOrNull()
        if (columnInput == null) {
            println("Incorrect column number")
            continue
        }
        if (columnInput !in 1..columns) {
            println("The column number is out of range (1 - $columns)")
            continue
        }
        if (currentGameGrid[0][columnInput - 1] != " ") {
            println("Column $columnInput is full")
            continue
        }
        return columnInput
    }
}

fun checkWin(currentGameGrid: GameGrid, token: String): Boolean {
    val columns = currentGameGrid[0].size
    val rows = currentGameGrid.size
    val fourInARow = "$token $token $token $token"
    var stringToCheck: String

    // check rows for fourInARow
    for (i in 0 until rows) {
        stringToCheck = currentGameGrid[i].joinToString().replace(",", "")
        if (stringToCheck.contains(fourInARow)) return true
    }

    // check columns for fourInARow
    for (i in 0 until columns) {
        stringToCheck = ""
        for (j in 0 until rows) {
            stringToCheck += "${currentGameGrid[j][i]} "
        }
        if (stringToCheck.contains(fourInARow)) return true
    }

    // check positive diagonals for fourInARow
    for (i in 3..13) {
        stringToCheck = ""
        for (j in 0 until columns) {
            if (i - j >= rows) continue
            if (i - j < 0) continue
            stringToCheck += "${currentGameGrid[i - j][j]} "
        }
        if (stringToCheck.contains(fourInARow)) return true
    }

    // check negative diagonals for fourInARow
    for (i in 3..13) {
        stringToCheck = ""
        for (j in columns - 1 downTo 0) {
            if (i + j - columns + 1 >= rows) continue
            if (i + j - columns + 1 < 0) continue
            stringToCheck += "${currentGameGrid[i + j - columns + 1][j]} "
        }
        if (stringToCheck.contains(fourInARow)) return true
    }

    return false
}

fun checkDraw(currentGameGrid: GameGrid): Boolean {
    val columns = currentGameGrid[0].size
    var counter = 0

    // check if each space in the top row is not empty in which case the grid will be full
    for (i in 0 until columns) {
        if (currentGameGrid[0][i] != " ") counter++
    }

    return (counter == columns)
}

fun playerTurn(playerInput: Int, player: Player, opponent: Player, currentGameGrid: GameGrid): Boolean {
    printBoard(gameGrid(currentGameGrid, playerInput, player.token))
    if (checkDraw(currentGameGrid)) {
        println("It is a draw")
        player.score ++
        opponent.score ++
        return false
    }
    if (checkWin(currentGameGrid, player.token)) {
        println("Player ${player.name} won")
        player.score += 2
        return false
    }
    return true
}