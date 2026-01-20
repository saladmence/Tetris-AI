package tetris.Moves;

import tetris.TetrisBoard;
import tetris.Board.Result;

public class MoveDrop implements Move{
    @Override
    public Result execute(TetrisBoard board){
        int dy = board.dropHeightReal(board.getCurrentPiecePosition().x);

        board.getCurrentPiecePosition().y -= dy;

        board.placePiece();
        return Result.PLACE;
    }
}
