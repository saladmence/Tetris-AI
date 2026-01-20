package tetris;

import java.awt.Point;

/**
 * An abstraction for a Tetris board, which allows for querying it's state and
 * applying actions.
 */
public interface Board {

    /**
     * Possible results of applying a board action.
     */
    enum Result {
        /**
         * The action was a success (eg, applied successfuly).
         */
        SUCCESS,

        /**
         * The action would cause the piece to go out of bounds, or collide with
         * another piece.
         */
        OUT_BOUNDS,

        /**
         * There is no piece on the board to apply an action to.
         */
        NO_PIECE,

        /**
         * The last move caused a new piece to be placed.
         */
        PLACE
    }

    /**
     * The valid actions that can be taken on the board.
     */
    enum Action {
        /**
         * Attempt to move the piece one position to the left; this should never accidentally place
         * the piece (only DOWN and DROP can place a piece).
         */
        LEFT,

        /**
         * Attempt to move the piece one position to the right; this should never accidentally
         * place the piece (only DOWN and DROP can place a piece).
         */
        RIGHT,

        /**
         * Attempt to move the piece one position down; if this movement would cause the piece to
         * intersect with the stack, the piece should instead be placed at it's current position.
         */
        DOWN,

        /**
         * Attempt to drop the piece all the way, placing it wheverever it lands.
         */
        DROP,

        /**
         * Attempt to rotate the piece clockwise, applying wall-kicks if neccessary. If the wall kicks
         * could not be successfully applied, return Result.OUT_BOUNDS.
         */
        CLOCKWISE,

        /**
         * Attempt to rotate the piece counter-clockwise, applying wall-kicks if neccessary. If the
         * wall kicks could not be successfully applied, return Result.OUT_BOUNDS.
         */
        COUNTERCLOCKWISE,

        /**
         * Do nothing.
         */
        NOTHING,

        /**
         * "Hold" a piece until a later time, or "unhold" the piece so it
         * returns to play.
         *
         * Used only in karma; you can treat this as "NOTHING" if you are not
         * implementing it.
         */
        HOLD
    }

    Result move(Action act);
    
    Board testMove(Action act);

    Piece getCurrentPiece();

    Point getCurrentPiecePosition();

    void nextPiece(Piece p, Point startingPosition);

    boolean equals(Object other);

    Result getLastResult();

    Action getLastAction();

    int getRowsCleared();

    int getWidth();

    int getHeight();

    int getMaxHeight();

    int dropHeight(Piece piece, int x);

    int getColumnHeight(int x);

    int getRowWidth(int y);

    Piece.PieceType getGrid(int x, int y);
}
