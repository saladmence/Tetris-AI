package tetris;

import java.awt.*;
import java.util.Arrays;

import tetris.Moves.Move;
import tetris.Piece.PieceType;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {
    private int width;
    private int height;
    private int maxHeight;
    private int[] columnHeight;
    private int[] rowWidth;
    private PieceType[][] grid;
    private int score;
    private int rowsCleared;
    private int totalRowsCleared;

    private Piece currentPiece;
    private PieceType heldPieceType;
    private Point location;
    private int[] skirt;
    private Result lastResult;
    private Action lastAction;
    private boolean held = false;

    /**
     * Initializes all of the instance variables (score, rowsCleared, maxHeight, totalRowsCleared, grid, and columnHeight) to their respective default states
     * and verifies if the board width and height are valid. 
     * @param width, the value to set the board width to
     * @param height, the value to set the board height to
     */
    public TetrisBoard(int width, int height) {
        score = 0;
        rowsCleared = 0;
        if (width < 4 || height < 4) {
            System.err.println("board is too small!");
            return;
        }
        grid = new PieceType[width][height];
        columnHeight = new int[width];
        Arrays.fill(columnHeight, -1);
        maxHeight = 0;
        rowWidth = new int[height];
        this.width = width; 
        this.height = height;
        totalRowsCleared = 0;
    }

    /**
     * Prints out attributes of the board (maximum height, the score, and the total number of rows cleared)
     */
    public void displayStatus(){
        System.out.println("----------BOARD STATUS-----------");
        System.out.println("Max Height: " + maxHeight);
        System.out.println("Piece Placed: " + score);
        System.out.println("Rows Cleared: " + totalRowsCleared);
    }

    /**
     * Creates a new TetrisBoard and copies over all of the values of the current board 
     * (e.x, the grid, height, width, piece location, last action, last result of the last action, etc.)
     * @return the copied TetrisBoard
     */
    public TetrisBoard cloneBoard(){
        TetrisBoard newBoard = new TetrisBoard(this.width, this.height);
        for(int i = 0; i < width; i++){
            newBoard.grid[i] = this.grid[i].clone();
        }

        newBoard.rowWidth = this.rowWidth.clone();
        newBoard.columnHeight = this.columnHeight.clone();
        newBoard.score = this.score;
        newBoard.currentPiece = this.currentPiece;
        newBoard.heldPieceType = this.heldPieceType;
        newBoard.held = this.held;
        newBoard.location = this.location == null ? null : new Point(this.location);
        newBoard.maxHeight = this.maxHeight;
        newBoard.lastAction = this.lastAction;
        newBoard.lastResult = this.lastResult;
        newBoard.held = this.held;
        newBoard.skirt = newBoard.currentPiece == null ? null : newBoard.currentPiece.getSkirt().clone();
        newBoard.totalRowsCleared = this.totalRowsCleared;
        newBoard.rowsCleared = this.rowsCleared;

        return newBoard;
    }

    /**
     * Uses a separate Move class to execute different types of moves.
     * Validates if the current piece is null or the action is a legitimate action. 
     * @param act, the action to execute in the board
     * @return the result of the input action
     */
    @Override
    public Result move(Action act){
        lastAction = act;
        if(currentPiece == null && act != Action.NOTHING){
            return lastResult = Result.NO_PIECE;
        }

        return lastResult = Move.MOVES.get(act).execute(this);
    }

    /**
     * Sets the current Tetris piece to the input new Piece. Then, the skirt is updated with the new piece’s skirt values.
     * @param newPiece, the new Piece to set the current piece to
     * @param offset, the amount to offset the location of the current piece by (mainly used for wallkicks)
     */
    public void changePiece(Piece newPiece, Point offset){
        currentPiece = newPiece;
        skirt = currentPiece.getSkirt();
        location.x += offset.x;
        location.y += offset.y;
    }

    /**
     * Places the current piece on the board by updating the board's 2D grid representation. 
     * The max height, affected columns' heights, and the row widths are all updated as a result of placing the piece. 
     * Row clears are also considered after placing the piece.  
     */
    public void placePiece(){
        rowsCleared = 0;
        for(Point p : currentPiece.getBody()){
            grid[location.x+p.x][location.y+p.y] = currentPiece.getType();
            columnHeight[location.x+p.x] = Math.max(columnHeight[location.x+p.x], location.y+p.y);
            maxHeight = Math.max(getColumnHeight(location.x+p.x), maxHeight);
            rowWidth[location.y+p.y]++;
        }

        updateRowClears();

        //Sets held variable to false so the next piece can be held
        held = false;
        score++;

        location = null;
        skirt = null;
        currentPiece = null;
    }

    /**
     * Checks if the current row can be cleared after placing a Tetris piece, and if it does, updates the
     * number of rows cleared and the total number of rows cleared. 
     */
    private void updateRowClears(){
        int currentRow = Math.max(location.y, 0);

        for(int y = currentRow; y < height; y++){
            if(rowWidth[y] < width){
                if(currentRow != y) copyRow(y, currentRow);
                currentRow++;
            }
            else{
                rowsCleared++;
                totalRowsCleared++;
            }
        }

        if(rowsCleared > 0) updateColumnHeights();
    }

    /**
     * For a specified row in the grid, copy it and its rowWidth to another specified row in the grid (and update the rowWidth accordingly).
     * @param from, the index of the row to copy
     * @param to, the index of the row to copy onto
     */
    private void copyRow(int from, int to){
        for(int x = 0; x < width; x++){
            grid[x][to] = grid[x][from];
            grid[x][from] = null;
        }

        rowWidth[to] = rowWidth[from];
        rowWidth[from] = 0;
    }

    /**
     * Updates the column heights for each column and the overall maximum height for the board
     * following row clears. 
     */
    private void updateColumnHeights(){
        maxHeight = 0;

        for(int x = 0; x < width; x++){
            int y = columnHeight[x];
            while(y >= 0 && grid[x][y] == null) y--;

            columnHeight[x] = y;
            maxHeight = Math.max(maxHeight, getColumnHeight(x));
        }
    }

    /**
     * Determines if there is any point in the TetrisPiece that will intersect with an existing placed piece or the wall. 
     * @param body, the array of points of the current Tetris piece's body
     * @param dx, the amount to shift the piece by horizontally
     * @param dy, the amount to shift the piece by vertically
     * @return whether the Tetris piece will intersect with a wall or another piece
     */
    public boolean isIntersect(Point[] body, int dx, int dy){
        for(Point p : body){
            if(location.x+p.x+dx < 0 || location.x+p.x+dx >= width) return true;
            if(location.y+p.y+dy < 0 || location.y+p.y+dy >= height) return true;
            if(grid[location.x+p.x+dx][location.y+p.y+dy] != null) return true;
        }

        return false;
    }

    public boolean isIntersectTest(Point[] body, int x, int y){
        for(Point p : body){
            if(x+p.x < 0 || x+p.x >= width) return true;
            if(y+p.y < 0 || y+p.y >= height) return true;
            if(grid[x+p.x][y+p.y] != null) return true;
        }

        return false;
    }

    /**
     * Tests the move by performing the move on a copy of the TetrisBoard.
     * @param act, the action to test
     * @return a copy of the original TetrisBoard with the input move executed 
     */
    @Override
    public Board testMove(Action act) {
        TetrisBoard newBoard = this.cloneBoard();
        
        newBoard.move(act);
        return newBoard;
    }

    /**
     * Tests the placement of a given piece on a copy of the current TetrisBoard to ensure that it does nto intersect with anything. 
     * @param piece, the TetrisPiece to place on the TetrisBoard copy
     * @param x, the horizontal value to spawn the input TetrisPiece at
     * @return the copy of the original board with the input TetrisPiece placed on it
     */
    public Board testPlacement(Piece piece, int x){
        if(piece == null) return null;

        TetrisBoard testBoard = this.cloneBoard();
        testBoard.changePiece(piece, new Point(0, 0));
        testBoard.location.x = x;

        if(testBoard.isIntersect(testBoard.currentPiece.getBody(), 0, 0)) return null;
        
        testBoard.move(Action.DROP);
        return testBoard;
    }

    /**
     * Retrieves the current Tetris piece. 
     * @return the piece currently stored in the currentPiece instance variable
     */
    @Override
    public Piece getCurrentPiece() { return currentPiece; }

    /**
     * Retrieves the location of the current Tetris piece on the board
     * @return the point currently stored in the location instance variable, representing the current piece’s position on the board
     */
    @Override
    public Point getCurrentPiecePosition() { return location; }

    /**
     * The current piece is set to the input piece and spawned at the given location. 
     * The input piece is validated and verified to not intersect with anything on the board.
     * @param p, the piece to spawn on the board
     * @param spawnPosition, the location to spawn the input Tetris piece at
     */
    @Override
    public void nextPiece(Piece p, Point spawnPosition){
        if (p == null || spawnPosition == null) {
            System.err.println("invalid next piece or spawn position!");
            return;
        }
        currentPiece = p;
        location = new Point(spawnPosition);
        skirt = currentPiece.getSkirt().clone();

        //If the placement is invalid, set currentPiece, location, and skirt to null
        if(isIntersect(currentPiece.getBody(), 0, 0)){
            currentPiece = null;
            location = null;
            skirt = null;
        }
    }

    /**
     * Verifies if two TetrisBoards are equal by comparing their width, height, current Tetris piece and its current location, rows cleared, grid, and its position, 
     * and the values in every index of the grid array
     * @param other, the object to compare to the current TetrisBoard
     * @return whether the TetrisBoard matches the input object
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Board)) return false;
        Board temp = (Board)other;

        if(temp.getWidth() != width) return false;
        if(temp.getHeight() != height) return false;
        if(temp.getRowsCleared() != rowsCleared) return false;

        if(temp.getCurrentPiece() == null){
            if(currentPiece != null) return false;
        }
        else if(!temp.getCurrentPiece().equals(currentPiece)) return false;

        if(temp.getCurrentPiecePosition() == null){
            if(location != null) return false;
        }
        else if(!temp.getCurrentPiecePosition().equals(location)) return false;

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(grid[x][y] != temp.getGrid(x, y)) return false;
            }
        }

        return true;
    }

    /**
     * Retrieves the result of the last move executed.
     * @return the result of the last move
     */
    @Override
    public Result getLastResult() { return lastResult; }

    /**
     * Retrieves the last action executed.
     * @return the last action executed
     */
    @Override
    public Action getLastAction() { return lastAction; }

    /**
     * Retrieves the number of rows cleared in the last move.
     * @return the number of rows cleared
     */
    @Override
    public int getRowsCleared() { return rowsCleared; }

    /**
     * Retrieves the current score.
     * @return the current score of the TetrisBoard
     */
    public int getScore() { return score; }

    /**
     * Retrieves the width of the TetrisBoard
     * @return the TetrisBoard's width
     */
    @Override
    public int getWidth() { return width; }

    /**
     * Retrieves the height of the TetrisBoard
     * @return the TetrisBoard's height
     */
    @Override
    public int getHeight() { return height; }

    /**
     * Retrieves the maximum height of the TetrisBoard
     * @return the TetrisBoard's max height
     */
    @Override
    public int getMaxHeight() { return maxHeight; }

    /**
     * Calculates drop height by finding the distance between the skirt and column height for each column as long as 
     * the skirt is not Integer.MAX_VALUE.
     * Assumes that the piece is dropped from the top.
     * @param piece, the TetrisPiece to drop
     * @param x, the column
     * @return the column height where the piece would land
     */
    @Override
    public int dropHeight(Piece piece, int x) {
        int[] pieceSkirt = piece.getSkirt();

        int height = Integer.MIN_VALUE;
        for(int i = 0; i < pieceSkirt.length; i++){
            if(pieceSkirt[i] == Integer.MAX_VALUE) continue;
            if(x+i < 0 || x+i >= width) continue;

            height = Math.max(height, getColumnHeight(x+i)-pieceSkirt[i]);
        }

        return height;
    }

    /**
     * Calculates drop height if the piece was dropped from its current position 
     * by finding the distance between the skirt and column height for each column as long as the skirt is not Integer.MAX_VALUE.
     * @param x, the column
     * @return the distance a piece will fall if it was dropped at the current position
     */
    public int dropHeightReal(int x){
        int distance = Integer.MAX_VALUE;

        for(int i = 0; i < skirt.length; i++){
            if(skirt[i] == Integer.MAX_VALUE) continue;

            if(location.y+skirt[i] >= getColumnHeight(x+i)){
                distance = Math.min(distance, location.y+skirt[i] - getColumnHeight(x+i));
                continue;
            }

            for(int j = location.y; j >= 0; j--){
                if(grid[x+i][j] != null){
                    distance = Math.min(distance, location.y+skirt[i]-(j+1));
                    break;
                }

                if(j == 0){
                    distance = Math.min(distance, location.y+skirt[i]-j);
                    break;
                }
            }  
        }

        return distance;
    }

    /**
     * Validates and retrieves the column height given the column index.
     * @param x, the column index
     * @return the corresponding value in columnHeight plus 1 or Integer.MAX_VALUE if the column index is not valid.
     */
    @Override
    public int getColumnHeight(int x) {
        return x < 0 || x >= width ? Integer.MAX_VALUE : columnHeight[x]+1;
    }

    /**
     * Validates and retrieves the row width given the row index.
     * @param y, the row index
     * @return the corresponding value in rowWidth plus 1 or 0 if the row is not valid.
     */
    @Override
    public int getRowWidth(int y) {
        return y < 0 || y >= height ? 0 : rowWidth[y];
    }

    /**
     * Validates the x and y indexes of the grid. 
     * If they are not, returns null; otherwise, the value at the x and y location at the 2D grid array is returned.
     * @param x, the row of the location to access on the grid
     * @param y, the column of the location to access on the grid
     * @return the piece type of the piece at that location
     */
    @Override
    public Piece.PieceType getGrid(int x, int y) { 
        if (x >= width || y >= height || x < 0 || y < 0) {
            return null;
        }
        
        return grid[x][y];
    }

    /**
     * Sets the input grid to the current TetrisBoard's grid.
     * Updates columnHeight, rowWidth, and maxHeight to the grid's new values. 
     * @param grid, the 2D array of PieceTypes to copy over into the current TetrisBoard's grid
     */
    public void setGrid(PieceType[][] grid){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                this.grid[i][j] = grid[i][j];
            }
        }

        Arrays.fill(columnHeight, -1);
        Arrays.fill(rowWidth, 0);
        maxHeight = 0;

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(grid[x][y] != null){
                    columnHeight[x] = Math.max(columnHeight[x], y);
                    rowWidth[y]++;
                }
            }
            maxHeight = Math.max(maxHeight, getColumnHeight(x));
        }
    }

    /**
     * Updates the instance variable held to the input boolean.
     * @param held, a boolean indicating if there is a a held piece
     */
    public void setHold(boolean held){this.held = held;}

    /**
     * Retrieves whether there is a held piece or not.
     * @return a boolean depending on whether a held piece exists
     */
    public boolean isHeld(){return held;}

    /**
     * Retrieves the piece type of the held piece. 
     * @return the piece type of the held piece
     */
    public PieceType getHeldPieceType(){return heldPieceType;}

    /**
     * Sets the held piece type to the input type
     * @param type, the TetrisPiece type to set the held piece's type to
     */
    public void setHeldPieceType(PieceType type){heldPieceType = type;}

    /**
     * Retrieves the total number of rows cleared
     * @return the total number of rows cleared
     */
    public int getTotalRowsCleared() { return totalRowsCleared; }
}
