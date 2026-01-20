package tetris.Moves;

import java.awt.Point;

import tetris.Piece;
import tetris.TetrisBoard;
import tetris.TetrisPiece;
import tetris.Board.Result;

public class MoveHold implements Move{
    @Override
    public Result execute(TetrisBoard board){
        if(board.isHeld()) return Result.SUCCESS;
        board.setHold(true);

        if(board.getHeldPieceType() == null){
            board.setHeldPieceType(board.getCurrentPiece().getType());
            return Result.NO_PIECE;
        }
        else{
            Piece temp = new TetrisPiece(board.getHeldPieceType());
            board.setHeldPieceType(board.getCurrentPiece().getType());
            board.changePiece(temp, new Point(0,0));

            board.getCurrentPiecePosition().x = board.getWidth()/2 - board.getCurrentPiece().getWidth()/2;
            board.getCurrentPiecePosition().y = board.getHeight()-4;
            return Result.SUCCESS;
        }
    }
}
