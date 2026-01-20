package tetris;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import tetris.Piece.PieceType;

public class TetrisPieceTest {
    final int MAX = Integer.MAX_VALUE;

    Point[][] tRotations = new Point[][]{
        {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
        {new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2, 1)},
        {new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(1, 0)},
        {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 1)},
    };

    Point[][] oRotations = new Point[][]{
        {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
        {new Point(0, 1), new Point(1, 1), new Point(0, 0), new Point(1, 0)},
        {new Point(1, 1), new Point(1, 0), new Point(0, 1), new Point(0, 0)},
        {new Point(1, 0), new Point(0, 0), new Point(1, 1), new Point(0, 1)}
    };

    Point[][] iRotations = new Point[][]{
        {new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2)},
        {new Point(2, 3), new Point(2, 2), new Point(2, 1), new Point(2, 0)},
        {new Point(3, 1), new Point(2, 1), new Point(1, 1), new Point(0, 1)},
        {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
    };

    Point[][] zRotations = new Point[][]{
        {new Point(0, 2), new Point(1, 2), new Point(1, 1), new Point(2, 1)},
        {new Point(2, 2), new Point(2, 1), new Point(1, 1), new Point(1, 0)},
        {new Point(2, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1)},
        {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
    };

    Point[][] sRotations = new Point[][]{
        {new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
        {new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
        {new Point(2, 1), new Point(1, 1), new Point(1, 0), new Point(0, 0)},
        {new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(0, 2)}
    };

    Point[][] lRotations = new Point[][]{
        {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
        {new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2, 0)},
        {new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(0, 0)},
        {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)}
    };

    Point[][] jRotations = new Point[][]{
        {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
        {new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2, 2)},
        {new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(2, 0)},
        {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
    };

    int[][] tSkirts = new int[][]{
        {1,1,1},
        {MAX,0,1},
        {1,0,1},
        {1,0,MAX}
    };

    int[][] oSkirts = new int[][]{
        {0,0},
        {0,0},
        {0,0},
        {0,0}
    };

    int[][] iSkirts = new int[][]{
        {2,2,2, 2},
        {MAX,MAX,0,MAX},
        {1,1,1,1},
        {MAX,0,MAX,MAX}
    };

    int[][] zSkirts = new int[][]{
        {2,1,1},
        {MAX,0,1},
        {1,0,0},
        {0,1,MAX}
    };

    int[][] sSkirts = new int[][]{
        {1,1,2},
        {MAX,1,0},
        {0,0,1},
        {1,0,MAX}
    };

    int[][] lSkirts = new int[][]{
        {1,1,1},
        {MAX,0,0},
        {0,1,1},
        {2,0,MAX}
    };

    int[][] jSkirts = new int[][]{
        {1,1,1},
        {MAX,0,2},
        {1,1,0},
        {0,0,MAX}
    };

    @SuppressWarnings("deprecation")
    @Test
    void testClockwise() {
        Piece tPiece = new TetrisPiece(PieceType.T);
        Piece oPiece = new TetrisPiece(PieceType.SQUARE);
        Piece iPiece = new TetrisPiece(PieceType.STICK);
        Piece zPiece = new TetrisPiece(PieceType.LEFT_DOG);
        Piece jPiece = new TetrisPiece(PieceType.LEFT_L);
        Piece sPiece = new TetrisPiece(PieceType.RIGHT_DOG);
        Piece lPiece = new TetrisPiece(PieceType.RIGHT_L);

        for(int i = 0; i < 5; i++){
            assertEquals(tPiece.getBody(), tRotations[i%4]);
            assertEquals(oPiece.getBody(), oRotations[i%4]);
            assertEquals(iPiece.getBody(), iRotations[i%4]);
            assertEquals(zPiece.getBody(), zRotations[i%4]);
            assertEquals(sPiece.getBody(), sRotations[i%4]);
            assertEquals(lPiece.getBody(), lRotations[i%4]);
            assertEquals(jPiece.getBody(), jRotations[i%4]);

            tPiece = tPiece.clockwisePiece();
            oPiece = oPiece.clockwisePiece();
            iPiece = iPiece.clockwisePiece();
            zPiece = zPiece.clockwisePiece();
            sPiece = sPiece.clockwisePiece();
            lPiece = lPiece.clockwisePiece();
            jPiece = jPiece.clockwisePiece();
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    void testCounterclockwise() {
        Piece tPiece = new TetrisPiece(PieceType.T);
        Piece oPiece = new TetrisPiece(PieceType.SQUARE);
        Piece iPiece = new TetrisPiece(PieceType.STICK);
        Piece zPiece = new TetrisPiece(PieceType.LEFT_DOG);
        Piece jPiece = new TetrisPiece(PieceType.LEFT_L);
        Piece sPiece = new TetrisPiece(PieceType.RIGHT_DOG);
        Piece lPiece = new TetrisPiece(PieceType.RIGHT_L);

        for(int i = 4; i > 0; i--){
            assertEquals(tPiece.getBody(), tRotations[(i+4)%4]);
            assertEquals(oPiece.getBody(), oRotations[(i+4)%4]);
            assertEquals(iPiece.getBody(), iRotations[(i+4)%4]);
            assertEquals(zPiece.getBody(), zRotations[(i+4)%4]);
            assertEquals(sPiece.getBody(), sRotations[(i+4)%4]);
            assertEquals(lPiece.getBody(), lRotations[(i+4)%4]);
            assertEquals(jPiece.getBody(), jRotations[(i+4)%4]);

            tPiece = tPiece.counterclockwisePiece();
            oPiece = oPiece.counterclockwisePiece();
            iPiece = iPiece.counterclockwisePiece();
            zPiece = zPiece.counterclockwisePiece();
            sPiece = sPiece.counterclockwisePiece();
            lPiece = lPiece.counterclockwisePiece();
            jPiece = jPiece.counterclockwisePiece();
        }
    }

    @Test
    void testSkirt(){
        Piece tPiece = new TetrisPiece(PieceType.T);
        Piece oPiece = new TetrisPiece(PieceType.SQUARE);
        Piece iPiece = new TetrisPiece(PieceType.STICK);
        Piece zPiece = new TetrisPiece(PieceType.LEFT_DOG);
        Piece jPiece = new TetrisPiece(PieceType.LEFT_L);
        Piece sPiece = new TetrisPiece(PieceType.RIGHT_DOG);
        Piece lPiece = new TetrisPiece(PieceType.RIGHT_L);

        for(int i = 0; i < 4; i++){
            assertTrue(Arrays.equals(tPiece.getSkirt(), tSkirts[i]));
            assertTrue(Arrays.equals(oPiece.getSkirt(), oSkirts[i]));
            assertTrue(Arrays.equals(iPiece.getSkirt(), iSkirts[i]));
            assertTrue(Arrays.equals(zPiece.getSkirt(), zSkirts[i]));
            assertTrue(Arrays.equals(sPiece.getSkirt(), sSkirts[i]));
            assertTrue(Arrays.equals(lPiece.getSkirt(), lSkirts[i]));
            assertTrue(Arrays.equals(jPiece.getSkirt(), jSkirts[i]));

            tPiece = tPiece.clockwisePiece();
            oPiece = oPiece.clockwisePiece();
            iPiece = iPiece.clockwisePiece();
            zPiece = zPiece.clockwisePiece();
            sPiece = sPiece.clockwisePiece();
            lPiece = lPiece.clockwisePiece();
            jPiece = jPiece.clockwisePiece();
        }
    }

    @Test
    void testEquals(){
        Piece first = new TetrisPiece(PieceType.T);
        Piece second = new TetrisPiece(PieceType.T).clockwisePiece();

        second = second.clockwisePiece().clockwisePiece().clockwisePiece();

        assertTrue(first.equals(second));
    }
}
