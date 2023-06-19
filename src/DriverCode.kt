data class Board(var boardView: HashMap<String, String>, var pieceView: HashMap<String, MutableList<String>>)

fun setup(): Board {
    val squares: MutableList<String> = mutableListOf()
    for (rank in '1'..'8'){
        for (file in 'a'..'h') {
            squares.add(file.toString() + rank.toString())
        }
    }
    val start = ("RNBQKBNR" + "P".repeat(8) + " ".repeat(32) + "p".repeat(8) + "rnbqkbnr").toList()
    val boardView: HashMap<String, String> = hashMapOf()
    for (i in 0..63){
        boardView[squares[i]] = start[i].toString()
    }
    val pieceView: HashMap<String, MutableList<String>> = hashMapOf()
    val pieces = ("RNBQKPprnbqk").toList()
    for (piece in pieces){
        pieceView[piece.toString()] = mutableListOf()
    }
    for (sq in squares){
        var piece = boardView[sq]
        if (piece != " ") {
            pieceView[piece.toString()]!!.add(sq)
        }
    }
    return Board(boardView, pieceView)
}

fun makeOneMove(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    return when {
        "OOOooo".contains(move) -> {
            castle(move, boardView, pieceView)
        }
        "Pp".contains(move[0].toString()) -> {
            makePawnMove(move, boardView, pieceView)
        }
        else -> {
            movePiece(move, boardView, pieceView)
        }
    }
}

fun replaceSpaces(fen: String): String {
    var final: String = fen
    for (i in 8 downTo 1) {
        if (fen.contains(SPACE.repeat(i))) {
            final = final.replace(SPACE.repeat(i), i.toString())
        }
    }
    return final.slice(0 until final.length-1)
}

fun convertToFen(boardView: HashMap<String, String>): String{
    var squares: MutableList<String> = mutableListOf()
    for (rank in '8' downTo '1'){
        for (file in 'a'..'h') {
            squares.add(file.toString() + rank.toString())
        }
    }
    var orderedBoard: LinkedHashMap<String, String> = linkedMapOf()
    for (square in squares){
        orderedBoard[square] = boardView[square].toString()
    }
    var pieces = orderedBoard.values.toList()
    var fen = ""
    for (i in 0..7) {
        val row = pieces.slice((i * 8) until (i+1) * 8)
        for (i in row){
            fen += i
        }
        fen += "/"
    }
    return replaceSpaces(fen)
}

fun makeMoves(gameFile: String): String{
    var Board = setup()
    val moves = pgnToMoves(gameFile)
    for (move in moves.slice(0 until moves.size-1)) {
        val whiteMove = move[0]
        val blackMove = move[1]
        Board = makeOneMove(whiteMove, Board.boardView, Board.pieceView)
        Board = makeOneMove(blackMove, Board.boardView, Board.pieceView)
    }
    if (moves[moves.size-1].size == 1) {
        val whiteMove = moves[moves.size - 1][0]
        Board = makeOneMove(whiteMove, Board.boardView, Board.pieceView)
    }
    else {
        val whiteMove = moves[moves.size - 1][0]
        val blackMove = moves[moves.size - 1][1]
        Board = makeOneMove(whiteMove, Board.boardView, Board.pieceView)
        Board = makeOneMove(blackMove, Board.boardView, Board.pieceView)
    }
    return convertToFen(Board.boardView)
}

fun main(){
    println(makeMoves("src/PGN.txt"))
}