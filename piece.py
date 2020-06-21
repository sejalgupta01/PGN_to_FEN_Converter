import re

SPACE = " "

def castle(move, board_view, piece_view):
    if move in "OOO":
        home_rank, king, rook = "1", "K", "R"
    else:
        home_rank, king, rook = "8", "k", "r"
    king_before = "e" + home_rank
    if len(move) == 2:
        rook_before = "h" + home_rank
        king_after = "g" + home_rank
        rook_after = "f" + home_rank
    else:
        rook_before = "a" + home_rank
        king_after = "c" + home_rank
        rook_after = "d" + home_rank
    
    board_view[king_before] = SPACE
    board_view[rook_before] = SPACE
    board_view[king_after] = king
    board_view[rook_after] = rook
    piece_view[king].append(king_after)
    piece_view[king].remove(king_before)
    piece_view[rook].append(rook_after)
    piece_view[rook].remove(rook_before)
    
    return board_view, piece_view

def not_blocked(from_sq, to_sq, board_view):
    if from_sq[0] == to_sq[0]:
        between = [from_sq[0] + x for x in "12345678" if (int(from_sq[1]) < int(x) < int(to_sq[1]) or int(to_sq[1]) < int(x) < int(from_sq[1]))]
    elif from_sq[1] == to_sq[1]:
        between = [x + from_sq[1] for x in "abcdefgh" if (ord(from_sq[0]) < ord(x) < ord(to_sq[0]) or ord(to_sq[0]) < ord(x) < ord(from_sq[0]))]
    elif ord(from_sq[0]) < ord(to_sq[1]) and int(from_sq[1]) < int(to_sq[1]):
        between = [chr(x) + str(y) for x, y in zip(range(ord(from_sq[0]) + 1, ord(to_sq[0])), range(int(from_sq[1]) + 1, int(to_sq[1])))]
    elif ord(from_sq[0]) > ord(to_sq[1]) and int(from_sq[1]) < int(to_sq[1]):
        between = [chr(x) + str(y) for x, y in zip(range(ord(to_sq[0]) + 1, ord(from_sq[0])), range(int(to_sq[1]) - 1, int(from_sq[1]) - 2, -1))]
    elif ord(from_sq[0]) < ord(to_sq[1]) and int(from_sq[1]) > int(to_sq[1]):
        between = [chr(x) + str(y) for x, y in zip(range(ord(to_sq[0]) - 1, ord(from_sq[0]) -2, -1), range(int(to_sq[1]) + 1, int(from_sq[1])))]
    elif ord(from_sq[0]) > ord(to_sq[1]) and int(from_sq[1]) > int(to_sq[1]):
        between = [chr(x) + str(y) for x, y in zip(range(ord(to_sq[0]) + 1, ord(from_sq[0])), range(int(to_sq[1]) + 1, int(from_sq[1])))]
    for sq in between:
        if board_view[sq] != " ":
            return False
    return True

def get_from_sq(move, board_view, piece_view):
    piece = move[0]
    all_locations = piece_view[piece]
    possible_locations = []
    for sq in all_locations:
        if check_move(piece, sq, move[-2:]):
            possible_locations.append(sq)
    if len(possible_locations) == 1:
        return possible_locations[0]
    limit_possible_locations = []
    if piece not in "Nn":
        for sq in possible_locations:
            if not_blocked(sq, move[-2:], board_view):
                limit_possible_locations.append(sq)
        if len(limit_possible_locations) == 1:
            return limit_possible_locations[0]
        else:
            return remove_ambiguity(move, limit_possible_locations)
    else:
        return remove_ambiguity(move, possible_locations)

def remove_ambiguity(move, possibilities): 
    if re.search("[a-h][1-8]", move[1:-2]):
        location = re.findall("[a-h][1-8]", move[1:-2])[0]
    elif re.search("[a-h]", move[1:-2]):
        for i in possibilities:
            if re.findall("[a-h]", move[1:-2])[0] in i:
                location = i
                break
    elif re.search("[1-8]", move[1:-2]):
        for i in possibilities:
            if re.findall("[1-8]", move[1:-2])[0] in i:
                location = i
                break
    return location

def check_move(piece, location, destination):
    moved_by = abs(ord(location[0]) - ord(destination[0])), abs(ord(location[1]) - ord(destination[1]))
    
    def can_rook_move():
        return moved_by[0] == 0 or moved_by[1] == 0

    def can_knight_move():
        return moved_by in [(2, 1), (1, 2)]

    def can_bishop_move():
        return moved_by[0] == moved_by[1]

    def can_queen_move():
        return can_rook_move() or can_bishop_move()

    def can_king_move():
        return moved_by in [(0, 1), (1, 0), (1, 1)]

    return {"R": can_rook_move, "N": can_knight_move, "B": can_bishop_move, "Q": can_queen_move, "K": can_king_move}[piece.upper()]()

def move_piece(move, board_view, piece_view):
    from_sq = get_from_sq(move, board_view, piece_view)
    to_sq = move[-2:]
    
    if "x" in move:
        piece_view[board_view[to_sq]].remove(to_sq)

    piece_view[move[0]].append(to_sq)
    piece_view[move[0]].remove(from_sq)
    board_view[from_sq] = SPACE
    board_view[to_sq] = move[0]

    return board_view, piece_view
