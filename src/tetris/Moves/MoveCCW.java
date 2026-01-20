package tetris.Moves;

import java.awt.Point;

import tetris.Piece;
import tetris.TetrisBoard;
import tetris.TetrisPiece;
import tetris.Board.Result;

public class MoveCCW implements Move{
    @Override
    public Result execute(TetrisBoard board){
        Piece piece = board.getCurrentPiece();

        for(Point offset : TetrisPiece.COUNTERCLOCKWISE_WALLKICK_MAP.get(piece.getType())[piece.getRotationIndex()]){
            if(!board.isIntersect(piece.counterclockwisePiece().getBody(), offset.x, offset.y)){
                board.changePiece(piece.counterclockwisePiece(), offset);
                return Result.SUCCESS;
            }
        }

        return Result.OUT_BOUNDS;
    }
}
