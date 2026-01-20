package tetris.Moves;

import tetris.TetrisBoard;
import tetris.Board.Result;

public class MoveNothing implements Move{
    @Override
    public Result execute(TetrisBoard board){
        return Result.SUCCESS;
    }    
}
