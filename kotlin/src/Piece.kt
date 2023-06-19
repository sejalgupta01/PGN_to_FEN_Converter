import kotlin.math.absoluteValue

const val SPACE: String = " "

fun castle(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    val homeRank: String
    val king: String
    val rook: String
    if (move in "OOO") {
        homeRank = "1"
        king = "K"
        rook = "R"
    }
    else{
        homeRank = "8"
        king = "k"
        rook = "r"
    }

    val kingBefore = "e$homeRank"
    val rookBefore: String
    val kingAfter: String
    val rookAfter: String
    if (move.length == 2) {
        rookBefore = "h$homeRank"
        kingAfter = "g$homeRank"
        rookAfter = "f$homeRank"
    }
    else {
        rookBefore = "a$homeRank"
        kingAfter = "c$homeRank"
        rookAfter = "d$homeRank"
    }
    boardView[kingBefore] = SPACE
    boardView[rookBefore] = SPACE
    boardView[kingAfter] = king
    boardView[rookAfter] = rook
    pieceView[king]!!.add(kingAfter)
    pieceView[king]!!.remove(kingBefore)
    pieceView[rook]!!.add(rookAfter)
    pieceView[rook]!!.remove(rookBefore)
    return Board(boardView, pieceView)
}

fun notBlocked(fromSq: String, toSq: String, boardView: HashMap<String, String>): Boolean {
    val fromFile: Char = fromSq[0]
    val toFile: Char = toSq[0]
    val fromRank: Int = fromSq[1].toString().toInt()
    val toRank: Int = toSq[1].toString().toInt()
    var noBlocking: Boolean = true
    if (fromFile == toFile && fromRank < toRank){
        for (rank in fromRank+1 until toRank){
            if (boardView[fromFile + rank.toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile == toFile && fromRank > toRank){
        for (rank in toRank+1 until fromRank){
            if (boardView[fromFile + rank.toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile > toFile && fromRank == toRank){
        for (file in toFile+1 until fromFile){
            if (boardView[file + fromRank.toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile < toFile && fromRank == toRank){
        for (file in fromFile+1 until toFile){
            if (boardView[file + fromRank.toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile < toFile && fromRank < toRank){
        for (diff in 1..toRank-fromRank){
            if (boardView[(fromFile.toInt()+diff).toChar() + (fromRank+diff).toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile < toFile && fromRank > toRank){
        for (diff in 1..fromRank-toRank){
            if (boardView[(fromFile.toInt()+diff).toChar() + (fromRank-diff).toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile > toFile && fromRank < toRank){
        for (diff in 1 until toRank-fromRank){
            if (boardView[(fromFile.toInt()-diff).toChar() + (fromRank+diff).toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    else if (fromFile > toFile && fromRank > toRank){
        for (diff in 1..fromRank-toRank){
            if (boardView[(fromFile.toInt()-diff).toChar() + (fromRank-diff).toString()] != SPACE){
                noBlocking = false
                break
            }
        }
    }
    return noBlocking
}

fun removeAmbiguity(move: String, possibilities: MutableList<String>): String{
    val extra: String = move.slice(1..move.length-3)
    var location: String = SPACE
    if (Regex("[a-h][1-8]").containsMatchIn(extra)){
        location = (Regex("[a-h][1-8]").find(extra)?.value).toString()
    }
    else if(Regex("[a-h]").containsMatchIn(extra)){
        for (i in possibilities){
            if (((Regex("[a-h]").find(extra))?.value).toString() in i){
                location = i
            }
        }
    }
    else if(Regex("[1-8]").containsMatchIn(extra)){
        for (i in possibilities){
            if (((Regex("[1-8]").find(extra))?.value).toString() in i){
                location = i
            }
        }
    }
    return location
}

fun checkMove(piece: String, location: String, destination: String): Boolean{
    val movedBy: List<Int> = listOf((location[0]-destination[0]).toInt().absoluteValue, (location[1]-destination[1]).toInt().absoluteValue)

    fun canRookMove(): Boolean{
        return ((movedBy[0] == 0) || (movedBy[1] == 0))
    }
    fun canKnightMove(): Boolean{
        return ((movedBy == listOf(2, 1)) || movedBy == listOf(1, 2))
    }
    fun canBishopMove(): Boolean{
        return movedBy[0] == movedBy[1]
    }
    fun canQueenMove(): Boolean{
        return (canRookMove() || canBishopMove())
    }
    fun canKingMove(): Boolean{
        return ((movedBy == listOf(0, 1)) || (movedBy == listOf(1, 0)) || (movedBy == listOf(1, 1)))
    }
    return when(piece.toUpperCase()){
        "R" -> canRookMove()
        "K" -> canKingMove()
        "B" -> canBishopMove()
        "Q" -> canQueenMove()
        else -> canKnightMove()
    }
}

fun getFromSq(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): String{
    val piece = move[0]
    val allLocations = pieceView[piece.toString()]
    val possibleLocs = mutableListOf<String>()
    val toSq = move.slice(move.length-2 until move.length)
    if (allLocations != null) {
        for (sq in allLocations){
            if (checkMove(piece.toString(), sq, toSq)){
                possibleLocs.add(sq)
            }
        }
    }
    if (possibleLocs.size == 1){
        return possibleLocs[0]
    }
    val limitPossibleLocs = mutableListOf<String>()
    if (!"nN".contains(piece)) {
        for (sq in possibleLocs) {
            if (notBlocked(sq, toSq, boardView)) {
                limitPossibleLocs.add(sq)
            }
        }
        if (limitPossibleLocs.size == 1) {
            return limitPossibleLocs[0]
        } else {
            return removeAmbiguity(move, limitPossibleLocs)
        }
    }
    else{
        return removeAmbiguity(move, possibleLocs)
    }
}

fun movePiece(move: String, boardView: HashMap<String, String>, pieceView: HashMap<String, MutableList<String>>): Board {
    val fromSq = getFromSq(move, boardView, pieceView)
    val toSq = move.slice(move.length - 2 until move.length)
    if (move.contains('x')) {
        pieceView[boardView[toSq]]!!.remove(toSq)
    }
    pieceView[move[0].toString()]!!.add(toSq)
    pieceView[move[0].toString()]!!.remove(fromSq)
    boardView[fromSq] = SPACE
    boardView[toSq] = move[0].toString()

    return Board(boardView, pieceView)
}