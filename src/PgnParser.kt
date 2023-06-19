import java.io.File
import java.io.InputStream

fun pgnToMoves(pgnFile: String): MutableList<List<String>> {
    val inputStream: InputStream = File(pgnFile).inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    val moves = lineList.last()

    var pattern = Regex("[1-9][0-9]* *\\. *")
    var movesList: List<String> = pattern.split(moves)
    movesList = movesList.drop(1)
    movesList = movesList.toMutableList()
    var lastMove: String =  movesList.removeLast()
    pattern = Regex("( *1 *- *0 *| *0 *- *1 *| *1/2 *- *1/2 *)")
    lastMove = pattern.replace(lastMove, "")
    movesList.add(lastMove)
    movesList = clean(movesList)
    return preprocessMoves(movesList)
}

fun clean(moves: List<String>): MutableList<String> {
    val cleanMoves: MutableList<String> = mutableListOf()
    var cleanedMove: String
    for (move in moves){
        val pattern = Regex("e\\.p\\.|[^a-zA-Z0-9 ]")
        cleanedMove = pattern.replace(move, "")
        cleanedMove = cleanedMove.trimEnd()
        cleanMoves.add(cleanedMove)
    }
    return cleanMoves
}

fun preprocessMove(move: String): List<String> {
    var processedMove: List<String> = listOf()
    if (move.split(" ").count() == 1) {
        var wmove = move
        if (wmove[0] in 'a'..'h') {
            wmove = "P" + wmove
        }
        processedMove = listOf(wmove)
    }
    else {
        var wmove = move.split(" ")[0]
        var bmove = move.split(" ")[1]
        if (wmove[0] in 'a'..'h') {
            wmove = "P" + wmove
        }
        if (bmove[0] in 'a'..'h') {
            bmove = "p" + bmove
        }
        bmove = bmove.toLowerCase()
        processedMove = listOf(wmove, bmove)
    }
    return processedMove
}

fun preprocessMoves(moves: List<String>): MutableList<List<String>> {
    var processedMoves = mutableListOf<List<String>>()
    for (move in moves){
        if (move.isNotEmpty()){
            processedMoves.add(preprocessMove(move))
        }
    }
    return processedMoves
}