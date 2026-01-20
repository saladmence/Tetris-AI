package tetris.Moves;

import tetris.TetrisBoard;
import tetris.Board.Result;

public class MoveDown implements Move{
    @Override
    public Result execute(TetrisBoard board){
        if(board.isIntersect(board.getCurrentPiece().getBody(), 0, -1)){
            board.placePiece();
            return Result.PLACE;
        }

        board.getCurrentPiecePosition().y -= 1;
        return Result.SUCCESS;
    }
}
