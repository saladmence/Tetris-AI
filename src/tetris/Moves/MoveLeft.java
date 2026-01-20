package tetris.Moves;

import tetris.TetrisBoard;
import tetris.Board.Result;

public class MoveLeft implements Move{
    @Override
    public Result execute(TetrisBoard board){
        if(board.isIntersect(board.getCurrentPiece().getBody(), -1, 0)) return Result.OUT_BOUNDS;

        board.getCurrentPiecePosition().x -= 1;
        return Result.SUCCESS;
    }
}
