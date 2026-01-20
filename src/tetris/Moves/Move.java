package tetris.Moves;

import java.util.EnumMap;

import tetris.TetrisBoard;
import tetris.Board.Action;
import tetris.Board.Result;

public interface Move {
    public static final EnumMap<Action, Move> MOVES = new EnumMap<>(Action.class){{
        put(Action.LEFT, new MoveLeft());
        put(Action.RIGHT, new MoveRight());
        put(Action.DOWN, new MoveDown());
        put(Action.DROP, new MoveDrop());
        put(Action.HOLD, new MoveHold());
        put(Action.CLOCKWISE, new MoveCW());
        put(Action.COUNTERCLOCKWISE, new MoveCCW());
        put(Action.NOTHING, new MoveNothing());
    }};

    Result execute(TetrisBoard board);
}
