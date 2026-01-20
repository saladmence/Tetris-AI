package tetris.Moves;

import java.awt.Point;

import tetris.Piece;
import tetris.TetrisBoard;
import tetris.TetrisPiece;
import tetris.Board.Result;

public class MoveCW implements Move{
    @Override
    public Result execute(TetrisBoard board){
        Piece piece = board.getCurrentPiece();

        for(Point offset : TetrisPiece.CLOCKWISE_WALLKICK_MAP.get(piece.getType())[piece.getRotationIndex()]){
            if(!board.isIntersect(piece.clockwisePiece().getBody(), offset.x, offset.y)){
                board.changePiece(piece.clockwisePiece(), offset);
                return Result.SUCCESS;
            }
        }

        return Result.OUT_BOUNDS;
    }
}
