import sys
import pgnparser
import piece
import pawn

def setup():
    squares = [y+x for x in "12345678" for y in "abcdefgh"]
    start = "RNBQKBNR" + "P" * 8 + " " * 32 + "p" * 8 + "rnbqkbnr"
    
    board_view = {square: piece for square, piece in zip(squares, start)}
    piece_view = {_: [] for _ in "BKNPQRbknpqr"}
    for sq in board_view:
        piece = board_view[sq]
        if piece != " ":
            piece_view[piece].append(sq)
            
    return board_view, piece_view

def convert_to_fen(board_view):
    squares = [y+x for x in "87654321" for y in "abcdefgh"]
    ordered_board = {square: board_view[square] for square in squares}
    pieces = list(ordered_board.values())
    fen = ""
    for i in range(8):
        row = pieces[(i * 8): (i + 1) * 8]
        fen += "".join(row) + "/"

    return replace_spaces(fen)

def replace_spaces(fen):
    for i in range(8, 0, -1):
        if " " * i in fen:
            fen = fen.replace(" " * i, str(i))
    return fen[:-1]

def make_one_move(move, board_view, piece_view):
    if move in "OOOooo":
        return piece.castle(move, board_view, piece_view)
    elif move[0] in "Pp":
        return pawn.make_pawn_move(move, board_view, piece_view)
    else:
        return piece.move_piece(move, board_view, piece_view)

def make_moves(gamefile):
    board_view, piece_view = setup()
    moves = pgnparser.pgn_to_moves(gamefile)
    for move in moves[:-1]:
        wmove, bmove = move
        board_view, piece_view = make_one_move(wmove, board_view, piece_view)
        board_view, piece_view = make_one_move(bmove, board_view, piece_view)
    if len(moves[-1]) == 1:
        wmove = moves[-1][0]
        board_view, piece_view = make_one_move(wmove, board_view, piece_view)
    else:
        wmove, bmove = moves[-1]
        board_view, piece_view = make_one_move(wmove, board_view, piece_view)
        board_view, piece_view = make_one_move(bmove, board_view, piece_view)

    return convert_to_fen(board_view)

def final_notation(gamefile):
    return make_moves(gamefile)

pgn = sys.argv[1]
print(final_notation(pgn))
