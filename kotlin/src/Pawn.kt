val makeEpFrom = hashMapOf<String, String>("P" to "5", "p" to "4")

val makeCapFrom = hashMapOf("P" to hashMapOf<String, String>("3" to "2", "4" to "3", "5" to "4", "6" to "5", "7" to "6"),
    "p" to hashMapOf<String, String>("6" to "7", "5" to "6", "4" to "5", "3" to "4", "2" to "3"))

val makePromFrom = hashMapOf<String, String>("P" to "7", "p" to "2")

val makeRegFrom = hashMapOf("P" to hashMapOf<String, List<String>>("3" to listOf("2"), "4" to listOf("3", "2"),
        "5" to listOf("4"), "6" to listOf("5"), "7" to listOf("6")),
    "p" to hashMapOf("6" to listOf("7"), "5" to listOf("6", "7"), "4" to listOf("5"), "3" to listOf("4"),
            "2" to listOf("3")))

fun isEp(move: String, boardView: HashMap<String, String>): Boolean {
    return Regex("[Pp][a-h]x[a-h][2-7]").matchEntire(move) != null && boardView[move.slice(move.length-2 until move.length)] == SPACE
}

fun isCapture(move: String): Boolean {
    return Regex("[Pp][a-h]x[a-h][2-7]").matchEntire(move) != null
}

fun isPromotion(move: String): Boolean {
    return Regex("[Pp][a-h](x[a-h])?[18][RNBQrnbq]").matchEntire(move) != null
}

fun isRegularMove(move: String): Boolean {
    return Regex("[Pp][a-h][2-7]").matchEntire(move) != null
}

fun makeEp(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board{
    val pawn: String = move[0].toString()
    val toSq: String = move.slice(move.length-2 until move.length)
    val fromFile: String = move[1].toString()
    val fromSq: String = fromFile + makeEpFrom[pawn]
    val capAtSq: String
    if (pawn == "P") {
        capAtSq = toSq[0].toString() + (toSq[1].toString().toInt() - 1).toString()
    }
    else {
        capAtSq = toSq[0].toString() + (toSq[1].toString().toInt() + 1).toString()
    }

    boardView[fromSq] = SPACE
    boardView[toSq] = pawn
    boardView[capAtSq] = SPACE
    pieceView[pawn]!!.add(toSq)
    pieceView[pawn]!!.remove(fromSq)
    when(pawn) {
        "P" -> pieceView["p"]!!.remove(capAtSq)
        else -> pieceView["P"]!!.remove(capAtSq)
    }

    return Board(boardView, pieceView)
}

fun makeCapture(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    val pawn: String = move[0].toString()
    val toSq: String = move.slice(move.length - 2 until move.length)
    val fromFile: String = move[1].toString()
    val fromSq = fromFile + makeCapFrom[pawn]!![toSq[1].toString()]
    val capturedPiece = boardView[toSq]
    boardView[fromSq] = SPACE
    boardView[toSq] = pawn
    pieceView[pawn]!!.add(toSq)
    pieceView[pawn]!!.remove(fromSq)
    pieceView[capturedPiece]!!.remove(toSq)

    return Board(boardView, pieceView)
}

fun makePromotion(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    val pawn: String = move[0].toString()
    val toSq: String = move.slice(move.length - 3 until move.length - 1)
    val promotedTo: String = move[move.length - 1].toString()
    val fromFile: String
    val capturedPiece: String
    if (move.contains("x")) {
        fromFile = move[1].toString()
        capturedPiece = boardView[toSq].toString()
    } else {
        fromFile = toSq[0].toString()
        capturedPiece = SPACE
    }
    val fromSq = fromFile + makePromFrom[pawn]

    boardView[toSq] = promotedTo
    boardView[fromSq] = SPACE
    pieceView[pawn]!!.remove(fromSq)
    pieceView[promotedTo]!!.add(toSq)
    if (capturedPiece != SPACE) {
        pieceView[capturedPiece]!!.remove(toSq)
    }
    return Board(boardView, pieceView)
}

fun makeRegularMove(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    val pawn: String = move[0].toString()
    val toSq: String = move.slice(move.length - 2 until move.length)
    val fromFile: String = move[move.length - 2].toString()
    var fromRank: String = SPACE
    if ((makeRegFrom[pawn]!![toSq[1].toString()])!!.size == 1) {
        fromRank = makeRegFrom[pawn]!![toSq[1].toString()]!![0]
    } else {
        for (rank in makeRegFrom[pawn]!![toSq[1].toString()]!!) {
            if (boardView[fromFile + rank] == pawn) {
                fromRank = rank
                break
            }
        }
    }
    val fromSq = fromFile + fromRank

    boardView[fromSq] = SPACE
    boardView[toSq] = pawn
    pieceView[pawn]!!.add(toSq)
    pieceView[pawn]!!.remove(fromSq)

    return Board(boardView, pieceView)
}

fun makePawnMove(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    when {
        isEp(move, boardView) -> {
            return makeEp(move, boardView, pieceView)
        }
        isCapture(move) -> {
            return makeCapture(move, boardView, pieceView)
        }
        isPromotion(move) -> {
            return makePromotion(move, boardView, pieceView)
        }
        isRegularMove(move) -> {
            return makeRegularMove(move, boardView, pieceView)
        }
    }
    return Board(boardView, pieceView)
}