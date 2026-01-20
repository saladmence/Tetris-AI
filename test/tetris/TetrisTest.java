package tetris;
import org.junit.jupiter.api.Test;

import tetris.Board.Action;
import tetris.Piece.PieceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.awt.Point;

public class TetrisTest {
    @Test
    void testNextMove(){
        TetrisBrain brain = new TetrisBrain(new double[]{-0.510066, 0.760666, -0.35663, -0.184483, -0.35663, -0.760666, -0.510066, -0.184483, -0.760666});
        Board board = new TetrisBoard(10, 24);
        board.nextPiece(new TetrisPiece(PieceType.T), new Point(5, 10));

        Action move = brain.nextMove(board);
        board.nextPiece(null, null);

        assertNotEquals(move, null);
    }

    @Test
    void testGetBoardInfo(){
        TetrisBrain brain = new TetrisBrain(new double[]{-0.510066, 0.760666, -0.35663, -0.184483, -0.35663, -0.760666, -0.510066, -0.184483, -0.760666});
        Board board = new TetrisBoard(5, 5);

        PieceType[][] grid = new PieceType[][]{
            {null, null, null, null, null},
            {PieceType.T, null, PieceType.T, null, null},
            {null, PieceType.T, null, PieceType.T, null},
            {PieceType.T, null, PieceType.T, null, null},
            {null, PieceType.T, null, PieceType.T, null}
        };

        
        ((TetrisBoard)board).setGrid(grid);
        double[] info = brain.getBoardInfo(board);
        double[] expected = new double[]{4, 3, 6, 6, 6, 0, 14, 4, 10};

        assertEquals(info[0], expected[0], 0.01);
        assertEquals(info[1], expected[1], 0.01);
        assertEquals(info[2], expected[2], 0.01);
        assertEquals(info[3], expected[3], 0.01);
        assertEquals(info[4], expected[4], 0.01);
        assertEquals(info[5], expected[5], 0.01);
        assertEquals(info[6], expected[6], 0.01);
        assertEquals(info[7], expected[7], 0.01);
        assertEquals(info[8], expected[8], 0.01);
    }
}
