package tetris;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 */
public final class TetrisPiece implements Piece {

    private Piece cwPiece;
    private Piece ccwPiece;
    private PieceType type;
    private int rotationIndex;
    private int[] skirt;
    private Point[] body;
    private int width;
    private int height;

    //Data Structures to store wall kicks, makes it easier to access the correct wall kick data based on a piece's type
    public static final Point[][] SQUARE_WALL_KICKS = new Point[][] {
        new Point[] { new Point(0, 0)},
        new Point[] { new Point(0, 0)},
        new Point[] { new Point(0, 0)},
        new Point[] { new Point(0, 0)}
    };

    public static final EnumMap<PieceType, Point[][]> CLOCKWISE_WALLKICK_MAP = new EnumMap<>(PieceType.class){{
        put(PieceType.T, NORMAL_CLOCKWISE_WALL_KICKS);
        put(PieceType.LEFT_DOG, NORMAL_CLOCKWISE_WALL_KICKS);
        put(PieceType.LEFT_L, NORMAL_CLOCKWISE_WALL_KICKS);
        put(PieceType.RIGHT_DOG, NORMAL_CLOCKWISE_WALL_KICKS);
        put(PieceType.RIGHT_L, NORMAL_CLOCKWISE_WALL_KICKS);
        put(PieceType.SQUARE, SQUARE_WALL_KICKS);
        put(PieceType.STICK, I_CLOCKWISE_WALL_KICKS);
    }};

    public static final EnumMap<PieceType, Point[][]> COUNTERCLOCKWISE_WALLKICK_MAP = new EnumMap<>(PieceType.class){{
        put(PieceType.T, NORMAL_COUNTERCLOCKWISE_WALL_KICKS);
        put(PieceType.LEFT_DOG, NORMAL_COUNTERCLOCKWISE_WALL_KICKS);
        put(PieceType.LEFT_L, NORMAL_COUNTERCLOCKWISE_WALL_KICKS);
        put(PieceType.RIGHT_DOG, NORMAL_COUNTERCLOCKWISE_WALL_KICKS);
        put(PieceType.RIGHT_L, NORMAL_COUNTERCLOCKWISE_WALL_KICKS);
        put(PieceType.SQUARE, SQUARE_WALL_KICKS);
        put(PieceType.STICK, I_COUNTERCLOCKWISE_WALL_KICKS);
    }};

    /**
     * Construct a tetris piece of the given type. The piece should be in its spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * Sets all of the fields to default state and precomputes the new points in all of the clockwise and counterclockwise rotated versions of the piece 
     * for the given piece type and store them in an array of TetrisPieces.
     * The input is also validated to make sure it is a legitimate piece with a piece body, valid height, and valid weight. 
     * @param type, the specific type of Tetris piece to create
     */
    public TetrisPiece(PieceType type) {
        if(type == null){
            System.err.println("PieceType is null");
            return;
        }

        this.type = type;
        rotationIndex = 0;
        body = type.getSpawnBody();
        width = (int)type.getBoundingBox().getWidth();
        height = (int)type.getBoundingBox().getHeight();

        if(width < 1 || height < 1 || body == null){
            System.err.println("Invalid piece attributes");
            return;
        }

        calculateSkirt();
        
        TetrisPiece[] rotations = new TetrisPiece[4];
        rotations[0] = this;
        for(int i = 1; i < 4; i++){
            Point[] rotatedBody = rotatePiece(rotations[i-1].getBody(), rotations[i-1].getWidth());
            rotations[i] = new TetrisPiece(type, i, rotatedBody, width, height);
        }

        for(int i = 0; i < 4; i++){
            rotations[i].setClockwisePiece(rotations[(i+1)%4]);
            rotations[i].setCounterClockwisePiece(rotations[(i+3)%4]);
        }
    }

    /**
     * Creates a fully custom Tetris piece with the parameters described below and calculated skirts. 
     * @param type, the type of Tetris piece to make the new piece from
     * @param rotationIndex, the current rotation index of the new piece
     * @param body, the array of Points to represent the new piece's body
     * @param width, the width of the new Tetris piece
     * @param height, the height of the new Tetris piece
     */
    private TetrisPiece(PieceType type, int rotationIndex, Point[] body, int width, int height) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = body;
        this.width = width;
        this.height = height;
        calculateSkirt();
    }

    /**
     * Rotates each point in the body of the current Tetris piece by accessing the Point and its x and y coordinates.
     * @param oldBody, the pre-rotated Tetris piece's array of points representing its body
     * @param width, the width of the pre-rotated Tetris piece
     */
    private Point[] rotatePiece(Point[] oldBody, int width){
        Point[] rslt = new Point[4];
        for(int i = 0; i < 4; i++){
            int x = oldBody[i].x;
            int y = oldBody[i].y;

            Point temp = new Point(y, width-x-1);
            rslt[i] = temp;
        }

        return rslt;
    }

    /**
     * Creates an array with the same size as the Tetris piece’s bounding box and updates it with 
     * Integer.MAX_VALUE (no block exists in that column) or the minimum y-value of the blocks in the column.
     */
    private void calculateSkirt() {
        skirt = new int[(int) type.getBoundingBox().getWidth()]; //check if bounding box is acc an int
        Arrays.fill(skirt, Integer.MAX_VALUE);

        for(Point p : this.body){
            skirt[p.x] = Math.min(p.y, skirt[p.x]);
        }
    }

    /**
     * Takes the given Tetris piece and sets it equal to the instance variable tracking the details of the current Tetris piece turned 90 degrees clockwise.
     * @param cwPiece, the Tetris piece rotated 90 degrees clockwise
     */
    private void setClockwisePiece(Piece cwPiece){
        this.cwPiece = cwPiece;
    }

    /**
     * Takes the given Tetris piece and sets it equal to the instance variable tracking the details of the current Tetris piece turned 90 degrees counterclockwise.
     * @param ccwPiece, the Tetris piece rotated 90 degrees counterclockwise
     */
    private void setCounterClockwisePiece(Piece ccwPiece){
        this.ccwPiece = ccwPiece;
    }

    /**
     * Retrieves the current Tetris piece's type.
     * @return the TetrisPiece type
     */
    @Override
    public PieceType getType() {
        return type;
    }

    /**
     * Retrieves the current Tetris piece's rotation index.
     * @return the current rotation index
     */
    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    /**
     * Retrieves the current Tetris piece rotated 90 degrees clockwise.
     * @return the current Tetris piece turned 90 degrees clockwise
     */
    @Override
    public Piece clockwisePiece() {
        return cwPiece;
    }

    /**
     * Retrieves the current Tetris piece rotated 90 degrees counterclockwise.
     * @return the current Tetris piece turned 90 degrees counterclockwise
     */
    @Override
    public Piece counterclockwisePiece() {
        return ccwPiece;
    }

    /**
     * Retrieves the current Tetris piece's bounding box width.
     * @return the Tetris piece's width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the current Tetris piece's bounding box width.
     * @return the Tetris piece's width
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the current Tetris piece's body.
     * @return the array of Points representing the Tetris piece
     */
    @Override
    public Point[] getBody() {
        return body;
    }

    /**
     * Retrieves the skirt of the Tetris piece.
     * @return the array of integers representing the minimum y values of the blocks per column or Integer.MAX_VALUE if no blocks exist
     */
    @Override
    public int[] getSkirt() {
        return skirt;
    }

    /**
     * Verifies that the inputted object is a TetrisPiece by checking if it is an instance of TetrisPiece and matching its type and rotation index
     * @param other, the object to verify its equality with the current Tetris piece
     * @return whether the input object is equal to the current TetrisPiece
     */
    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        return type == otherPiece.getType() && rotationIndex == otherPiece.getRotationIndex();
    }
}
