import re

def pgn_to_moves(gamefile):
    raw_pgn = " ".join([line.strip() for line in open(gamefile)])

    comments_marked = raw_pgn.replace("{","<").replace("}",">")
    STRC = re.compile("<[^>]*>")
    comments_removed = STRC.sub(" ", comments_marked)

    STR_marked = comments_removed.replace("[","<").replace("]",">")
    str_removed = STRC.sub(" ", STR_marked)

    MOVE_NUM = re.compile("[1-9][0-9]* *\.")
    just_moves = [_.strip() for _ in MOVE_NUM.split(str_removed)]

    last_move = just_moves[-1]
    RESULT = re.compile("( *1 *- *0 *| *0 *- *1 *| *1/2 *- *1/2 *)")
    last_move = RESULT.sub("", last_move)
    moves = just_moves[:-1] + [last_move]
    moves = clean(moves)

    return pre_process_moves(moves)

def clean(moves):
    cleaned_moves = []
    for move in moves:
        if "e.p." in move:
            cleaned_move = move.replace("e.p.", "")
        SPECIAL_CHARS = re.compile("[^a-zA-Z0-9 ]")
        cleaned_move = SPECIAL_CHARS.sub("", move)
        cleaned_moves.append(cleaned_move)
    
    return cleaned_moves

def pre_process_a_move(move):
    if len(move.split()) == 1:
        wmove = move
        if wmove[0] in "abcdefgh":
            wmove = "P" + wmove
        return (wmove, )
    wmove, bmove = move.split()
    if wmove[0] in "abcdefgh":
        wmove = "P" + wmove
    if bmove[0] in "abcdefgh":
        bmove = "p" + bmove
    bmove = bmove.lower()
    
    return wmove, bmove

def pre_process_moves(moves):
    return [pre_process_a_move(move) for move in moves if len(move) > 0]
