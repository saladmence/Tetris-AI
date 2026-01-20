package tetris;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import tetris.Board.Action;
import tetris.Board.Result;
import tetris.Piece.PieceType;

public class TetrisBoardTest {
    private void assertGridEquals(Board board, PieceType[][] expected, int[] columnHeights, int[] rowWidths) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                assertEquals(expected[board.getHeight() - 1 - y][x], board.getGrid(x, y), "Board not equal at ("+x+","+y+")");
                assertEquals(rowWidths[y], board.getRowWidth(y), "Row width not equal for "+y);
            }

            assertEquals(columnHeights[x], board.getColumnHeight(x), "Column height not equal for "+x);
        }
    }

    /**
     * Runs {@link TetrisBoard#move} on the given board, and then verifies that
     * the {@link TetrisBoard#getLastAction()} ()} and {@link TetrisBoard#getLastResult()}
     * are the same as the given action and result.
     */
    private void moveVerification(Board board, Board.Action action, Board.Result expectedResult) {
        // ensure testMove properly updates the state
        assertEquals(action, board.testMove(action).getLastAction());
        assertEquals(expectedResult, board.testMove(action).getLastResult());

        // ensure move properly updates the state
        assertEquals(expectedResult, board.move(action));
        assertEquals(action, board.getLastAction());
        assertEquals(expectedResult, board.getLastResult());
    }

    /**
     * Given an empty grid and the expected grid after placing a piece, 
     * verifies that performing the respective move matches the expected grid. 
     * Then, for every possible rotation of a piece, it will keep going left and verify
     * that the piece will return out of bounds after a certain number of moves. 
     * This is also repeated using right moves. 
     */
    private void piecePlacerTester(PieceType[][] expectedGrid, PieceType[][] placedPieceGrid, PieceType p) {
        TetrisBoard board = new TetrisBoard(10, 20);
        TetrisBoard bigBoard = new TetrisBoard(10, 24);

        board.nextPiece(new TetrisPiece(p), new Point(0, 0));

        for (int x = 0; x < board.getWidth(); x++){
            for (int y = 0; y < board.getHeight(); y++) {
                assertEquals(board.getGrid(x,y), expectedGrid[y][x]);
            }
        }

        moveVerification(board, Action.DROP, Result.PLACE);
        board.move(Board.Action.DROP);

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++){
                assertEquals(board.getGrid(x,Math.abs(board.getHeight()-y-1)), placedPieceGrid[y][x]);
            }
        }

        for (int rot = 0; rot <= 4; rot++) {
            TetrisPiece tp = new TetrisPiece(p);
            for (int spin = 0; spin < rot; spin++) {
                tp.clockwisePiece();
            }
            bigBoard.nextPiece(tp, new Point(bigBoard.getWidth() / 2 - tp.getWidth() / 2, 20));

            int left = (bigBoard.getWidth() / 2 - tp.getWidth() / 2);
            for (int i = 0; i < left; i++) {
                moveVerification(bigBoard, Action.LEFT, Result.SUCCESS);
                board.move(Board.Action.LEFT);
            }

            moveVerification(bigBoard, Action.LEFT, Result.OUT_BOUNDS);
            Result r = bigBoard.move(Board.Action.LEFT);
            assertEquals(r, Result.OUT_BOUNDS);

            int right = (tp.getWidth() % 2 == 0) ? left*2 : 11-left;
            for (int i = 0; i < right; i++) {
                moveVerification(bigBoard, Action.RIGHT, Result.SUCCESS);
                board.move(Board.Action.RIGHT);
            }

            moveVerification(bigBoard, Action.RIGHT, Result.OUT_BOUNDS);
            r = bigBoard.move(Board.Action.RIGHT);
            assertEquals(r, Result.OUT_BOUNDS);
        }
    }

    @Test
    void testMove(){
        TetrisBoard board = new TetrisBoard(5, 7);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 1));
        board.move(Action.DROP);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(2, 1));
        board.move(Action.DROP);
        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(2, 1));
        board.move(Action.DROP);

        PieceType[][] expectedGrid = new PieceType[][]{
            {null, null, null, null, null},
            {null, null, null, null, null},
            {null, null, null, null, null},
            {null, null, null, null, null},
            {null, null, null, null, null},
            {null, null, null, null, PieceType.STICK},
            {null, null, null, null, PieceType.STICK},
        };

        assertGridEquals(board, expectedGrid, new int[]{0, 0, 0, 0, 2}, new int[]{1, 1, 0, 0, 0, 0, 0});
    }

    @Test
    void testTestMove(){
        Board board = new TetrisBoard(5, 7);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 1));
        board.move(Action.DROP);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(2, 1));
        board.move(Action.DROP);

        Board board2 = new TetrisBoard(5, 7);
        board2.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 1));
        board2 = board2.testMove(Action.DROP);
        board2.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(2, 1));
        board2 = board2.testMove(Action.DROP);

        assertTrue(board.equals(board2));
    }

    @Test
    void testPlacePiece() {
        PieceType[][] expectedGrid = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null}
        };
        
        PieceType[][] expectedGridStick = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {PieceType.STICK, PieceType.STICK, PieceType.STICK, PieceType.STICK, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridSquare = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {PieceType.SQUARE, PieceType.SQUARE, null, null, null, null, null, null, null, null},
            {PieceType.SQUARE, PieceType.SQUARE, null, null, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridT = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, PieceType.T, null, null, null, null, null, null, null, null},
            {PieceType.T, PieceType.T, PieceType.T, null, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridLeftDog = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {PieceType.LEFT_DOG, PieceType.LEFT_DOG, null, null, null, null, null, null, null, null},
            {null, PieceType.LEFT_DOG, PieceType.LEFT_DOG, null, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridRightDog = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, PieceType.RIGHT_DOG, PieceType.RIGHT_DOG, null, null, null, null, null, null, null},
            {PieceType.RIGHT_DOG, PieceType.RIGHT_DOG, null, null, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridLeftL = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {PieceType.LEFT_L, null, null, null, null, null, null, null, null, null},
            {PieceType.LEFT_L, PieceType.LEFT_L, PieceType.LEFT_L, null, null, null, null, null, null, null}
        };

        PieceType[][] expectedGridRightL = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, PieceType.RIGHT_L, null, null, null, null, null, null, null},
            {PieceType.RIGHT_L, PieceType.RIGHT_L, PieceType.RIGHT_L, null, null, null, null, null, null, null}
        };

        piecePlacerTester(expectedGrid, expectedGridStick, PieceType.STICK);
        piecePlacerTester(expectedGrid, expectedGridSquare, PieceType.SQUARE);
        piecePlacerTester(expectedGrid, expectedGridT, PieceType.T);
        piecePlacerTester(expectedGrid, expectedGridLeftDog, PieceType.LEFT_DOG);
        piecePlacerTester(expectedGrid, expectedGridRightDog, PieceType.RIGHT_DOG);
        piecePlacerTester(expectedGrid, expectedGridLeftL, PieceType.LEFT_L);
        piecePlacerTester(expectedGrid, expectedGridRightL, PieceType.RIGHT_L);
    }

    @Test
    void testIsIntersect() {

    }

    @Test
    void testGetCurrentPiece(){
        Piece piece = new TetrisPiece(PieceType.T).clockwisePiece();

        Board board = new TetrisBoard(4, 4);

        board.nextPiece(new TetrisPiece(PieceType.T), new Point(0, 5));
        assertEquals(board.getCurrentPiece(), null);

        board.nextPiece(new TetrisPiece(PieceType.T), new Point(0, 0));
        assertNotEquals(board.getCurrentPiece(), piece);
        
        board.nextPiece(new TetrisPiece(PieceType.T).clockwisePiece(), new Point(0, 0));
        assertEquals(board.getCurrentPiece(), piece);
    }

    @Test
    void testEquals(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0,0));

        Board board2 = new TetrisBoard(4, 4);
        board2.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0,0));

        assertTrue(board.equals(board2));
        board.move(Action.DROP);

        assertFalse(board.equals(board2));
        board2.move(Action.DOWN);

        assertTrue(board.equals(board2));
    }

    @Test
    void testGetLastResult(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 2));

        board.move(Action.LEFT);
        assertEquals(board.getLastResult(), Result.OUT_BOUNDS);

        board.move(Action.DOWN);
        assertEquals(board.getLastResult(), Result.SUCCESS);

        board.move(Action.NOTHING);
        assertEquals(board.getLastResult(), Result.SUCCESS);

        board.move(Action.DOWN);
        board.move(Action.DOWN);
        assertEquals(board.getLastResult(), Result.PLACE);

        board.move(Action.LEFT);
        assertEquals(board.getLastResult(), Result.NO_PIECE);

        board.nextPiece(new TetrisPiece(PieceType.STICK), new Point(0, 1));
        board.move(Action.CLOCKWISE);
        assertEquals(board.getLastResult(), Result.OUT_BOUNDS);
    }

    @Test
    void testGetLastAction(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(2, 2));

        board.move(Action.LEFT);
        assertEquals(board.getLastAction(), Action.LEFT);

        board.move(Action.DOWN);
        assertEquals(board.getLastAction(), Action.DOWN);

        board.move(Action.NOTHING);
        assertEquals(board.getLastAction(), Action.NOTHING);

        board.move(Action.RIGHT);
        assertEquals(board.getLastAction(), Action.RIGHT);

        board.move(Action.COUNTERCLOCKWISE);
        assertEquals(board.getLastAction(), Action.COUNTERCLOCKWISE);

        board.move(Action.CLOCKWISE);
        assertEquals(board.getLastAction(), Action.CLOCKWISE);

        board.move(Action.DROP);
        assertEquals(board.getLastAction(), Action.DROP);
    }

    @Test
    void testGetRowsCleared(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.STICK), new Point(0, 0));
        board.move(Action.DROP);

        assertEquals(board.getRowsCleared(), 1);

        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(-2, 0));
        board.move(Action.DROP);
        assertEquals(board.getRowsCleared(), 0);

        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(-1, 0));
        board.move(Action.DROP);
        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(0, 0));
        board.move(Action.DROP);

        assertEquals(board.getRowsCleared(), 0);
        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(1, 0));
        board.move(Action.DROP);

        assertEquals(board.getRowsCleared(), 4);
    }

    @Test
    void testGetMaxHeight(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.STICK), new Point(0, 0));
        board.move(Action.DROP);

        assertEquals(board.getMaxHeight(), 0);

        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 0));
        board.move(Action.DROP);

        assertEquals(board.getMaxHeight(), 2);

        board.nextPiece(new TetrisPiece(PieceType.RIGHT_L).counterclockwisePiece(), new Point(1, 0));
        board.move(Action.DROP);

        assertEquals(board.getMaxHeight(), 3);

        board.nextPiece(new TetrisPiece(PieceType.STICK).clockwisePiece(), new Point(1, 0));
        board.move(Action.DROP);

        assertEquals(board.getMaxHeight(), 2);
    }

    @Test
    void testDropHeight(){
        Board board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, 0));
        board.move(Action.DROP);

        assertEquals(board.dropHeight(new TetrisPiece(PieceType.LEFT_L).clockwisePiece(), 1), 0);
    }

    @Test
    void testGetGrid() {
        TetrisBoard tb = new TetrisBoard(10, 24);

        for (int x = 0; x < tb.getWidth(); x++){
            for (int y = 0; y < tb.getHeight(); y++) {
                assertEquals(tb.getGrid(x, y), null);
            }
        }

        TetrisPiece tp = new TetrisPiece(PieceType.STICK);
        tb.nextPiece(tp, new Point(tb.getWidth() / 2 - tp.getWidth() / 2, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        tb.nextPiece((new TetrisPiece(PieceType.STICK)).clockwisePiece(), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        tb.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);
        
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_L)).counterclockwisePiece(), new Point(0, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);
        
        // drops a left dog in between left l and horizontal stick, grid should clear the row
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_DOG)).clockwisePiece(), new Point(1, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        PieceType[][] expected = new PieceType[][]{ 
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, PieceType.STICK},
            {null, PieceType.LEFT_L, null, PieceType.LEFT_DOG, null, null, null, null, null, PieceType.STICK},
            {null, PieceType.LEFT_L, PieceType.LEFT_DOG, PieceType.LEFT_DOG, null, null, null, PieceType.SQUARE, PieceType.SQUARE, PieceType.STICK}
        };

        for (int y = 0; y < tb.getHeight(); y++) {
            for (int x = 0; x < tb.getWidth(); x++){
                assertEquals(tb.getGrid(x,Math.abs(tb.getHeight()-y-1)), expected[y][x]);
            }
        }

    }

    @Test
    void testGetColumnHeight() {
        TetrisBoard tb = new TetrisBoard(10, 24);

        for (int x = 0; x < tb.getWidth(); x++){
            for (int y = 0; y < tb.getHeight(); y++) {
                assertEquals(tb.getRowWidth(y), 0);
            }
        }

        TetrisPiece tp = new TetrisPiece(PieceType.STICK);
        tb.nextPiece(tp, new Point(tb.getWidth() / 2 - tp.getWidth() / 2, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);


        // drop horizontal stick
        assertEquals(tb.getColumnHeight(3), 1);
        assertEquals(tb.getColumnHeight(4), 1);
        assertEquals(tb.getColumnHeight(5), 1);
        assertEquals(tb.getColumnHeight(6), 1);

        tb.nextPiece((new TetrisPiece(PieceType.STICK)).clockwisePiece(), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        // drop a vertical stick to the far right
        assertEquals(tb.getColumnHeight(3), 1);
        assertEquals(tb.getColumnHeight(4), 1);
        assertEquals(tb.getColumnHeight(5), 1);
        assertEquals(tb.getColumnHeight(6), 1);
        assertEquals(tb.getColumnHeight(9), 4);

        tb.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        //drop a square to the right of horizontal stick
        assertEquals(tb.getColumnHeight(3), 1);
        assertEquals(tb.getColumnHeight(4), 1);
        assertEquals(tb.getColumnHeight(5), 1);
        assertEquals(tb.getColumnHeight(6), 1);
        assertEquals(tb.getColumnHeight(7), 2);
        assertEquals(tb.getColumnHeight(8), 2);
        assertEquals(tb.getColumnHeight(9), 4);
        
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_L)).counterclockwisePiece(), new Point(0, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        //drop a left l to the left of horizontal stick
        for (int x = 0; x < tb.getWidth(); x++) {
            if (x == 1) assertEquals(tb.getColumnHeight(x), 3);
            else if (x == 2) assertEquals(tb.getColumnHeight(x), 0);
            else if (x == 7 || x == 8) assertEquals(tb.getColumnHeight(x), 2);
            else if (x == 9) assertEquals(tb.getColumnHeight(x), 4);
            else assertEquals(tb.getColumnHeight(x), 1);
        }

        
        // drops a left dog in between left l and horizontal stick, grid should clear the row
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_DOG)).clockwisePiece(), new Point(1, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        assertEquals(tb.getColumnHeight(1), 2);
        assertEquals(tb.getColumnHeight(2), 1);
        assertEquals(tb.getColumnHeight(3), 2);
        assertEquals(tb.getColumnHeight(7), 1);
        assertEquals(tb.getColumnHeight(8), 1);
        assertEquals(tb.getColumnHeight(9), 3);
    }

    @Test
    void testGetRowWidth() {
        TetrisBoard tb = new TetrisBoard(10, 24);

        for (int x = 0; x < tb.getWidth(); x++){
            for (int y = 0; y < tb.getHeight(); y++) {
                assertEquals(tb.getRowWidth(y), 0);
            }
        }

        TetrisPiece tp = new TetrisPiece(PieceType.STICK);
        tb.nextPiece(tp, new Point(tb.getWidth() / 2 - tp.getWidth() / 2, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);


        // drop horizontal stick
        assertEquals(tb.getRowWidth(0), 4);

        tb.nextPiece((new TetrisPiece(PieceType.STICK)).clockwisePiece(), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        // drop a vertical stick to the far right
        assertEquals(tb.getRowWidth(0), 5);

        tb.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(7, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        //drop a square to the right of horizontal stick
        assertEquals(tb.getRowWidth(0), 7);
        
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_L)).counterclockwisePiece(), new Point(0, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        //drop a left l to the left of horizontal stick
        assertEquals(tb.getRowWidth(0), 9);

        
        // drops a left dog in between left l and horizontal stick, grid should clear the row
        tb.nextPiece((new TetrisPiece(PieceType.LEFT_DOG)).clockwisePiece(), new Point(1, 20));
        moveVerification(tb, Action.DROP, Result.PLACE);
        tb.move(Board.Action.DROP);

        assertEquals(tb.getRowWidth(0), 6);
        assertEquals(tb.getRowWidth(1), 3);
        assertEquals(tb.getRowWidth(2), 1);
    }
}

