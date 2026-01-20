package tetris;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import tetris.Board.Action;


public class TetrisBrain {
    private int currIndividual;
    private JTetrisBrainIndividual[] population;
    private Queue<Action> moveSequence = new LinkedList<Action>();

    /**
     * Sets up a single individual with a specific set of weights obtained from training the brain. 
     */
    public TetrisBrain(){
        population = new JTetrisBrainIndividual[1];
        double[] weights = new double[]{-3.079854632964414, -7.449884706642696, -16.62165886851931, -2.1674633428837424, -0.5474455153196136, -1.1402069503570305, -4.7857142119386635, -0.36342677092258685, -7.136217177275804};
        
        population[0] = new JTetrisBrainIndividual(weights);
        currIndividual = 0;
    }

    /**
     * Reads from a given text file, with each row containing all of the weight values for a single brain in one generation and their maximum fitness. 
     * The brains are sorted in descending order from highest to lowest fitness. 
     * @param fileName, the file to read the weights from
     */
    public TetrisBrain(String fileName) {
        population = new JTetrisBrainIndividual[JTetrisBrainTrainer.INITIAL_POPULATION_SIZE];
        try(BufferedReader bf = new BufferedReader(new FileReader(fileName))){
            bf.readLine();
            int starters = 0;
            Random r = new Random();

            for (int i = 0; i < JTetrisBrainTrainer.INITIAL_POPULATION_SIZE; i++) {
                double[] weights = new double[JTetrisBrainTrainer.WEIGHT_COUNT];

                population[i] = new JTetrisBrainIndividual();

                if(!bf.ready()){
                    if(starters == 0) continue;

                    JTetrisBrainIndividual parent1 = population[r.nextInt(starters)];
                    JTetrisBrainIndividual parent2 = population[r.nextInt(starters)];
                    while(parent1.equals(parent2)) parent2 = population[r.nextInt(starters)];

                    JTetrisBrainIndividual child = crossOver(parent1, parent2);
                    mutate(child);

                    population[i] = child;

                    continue;
                }

                String line = bf.readLine();
                line = line.substring(0, line.indexOf('|')-1);
                String[] strWeights = line.split(" ");

                if(strWeights == null || strWeights.length < JTetrisBrainTrainer.WEIGHT_COUNT) continue;
                for(int j = 0; j < JTetrisBrainTrainer.WEIGHT_COUNT; j++){
                    if(j >= strWeights.length) continue;

                    weights[j] = Double.parseDouble(strWeights[j]);
                }
                population[i].setWeights(weights);
                starters++;
            }

        }
        catch(IOException e){
            for (int i = 0; i < JTetrisBrainTrainer.INITIAL_POPULATION_SIZE; i++) {
                population[i] = new JTetrisBrainIndividual();
            }
        }
    }

    /**
     * Create a TetrisBrain with predetermined weights. 
     * Mainly used for the trained bot and sets the top individual in the population’s weights to the given weights. 
     * @param weights, the array of doubles to set the weights to
     */
    public TetrisBrain(double[] weights){
        population = new JTetrisBrainIndividual[]{new JTetrisBrainIndividual()};
        population[0].setWeights(weights);
    }

    /**
     * Determines the next sequence of moves the brain should make, as calculated from the fitness scoring method.
     * The sequence is stored in a queue called moveSequence.
     * @param currentBoard, the current state of the board
     */
    public Action nextMove(Board currentBoard) {
        // If the current piece has a move sequence, and it is not finished, return the next move in the sequence
        if(!moveSequence.isEmpty()){
            return moveSequence.poll();
        }

        // Casts the current board to a tetrisboard to use custom testPlacement method
        TetrisBoard tetrisBoard = (TetrisBoard)currentBoard;
        // if(tetrisBoard.getHeldPieceType() == null) return Action.HOLD;

        // Sets tracker variables for the ideal x value and rotation index of the piece
        double maxFitness = Double.NEGATIVE_INFINITY;
        int bestX = -1;
        int bestRotationIndex = 0;
        boolean holdUsed = false;
        double fitness;

        // Gets the current piece to be placed and tries every combination of horizontal position and rotations, assuming the piece is dropped from the top of the board
        Piece testPiece = new TetrisPiece(tetrisBoard.getCurrentPiece().getType());
        for(int i = 0; i < 4; i++){
            for(int x = -2; x < currentBoard.getWidth()+2; x++){
                if(tetrisBoard.isIntersectTest(testPiece.getBody(), x, currentBoard.getHeight()-4)) continue;

                Board newBoard = tetrisBoard.testPlacement(testPiece, x);
                // If the fitness of the current rotation and x value shift is better than the current best fitness, update the tracker values
                if(newBoard != null){
                    fitness = calcFitness(newBoard);

                    if (fitness > maxFitness){
                        maxFitness = fitness;
                        bestX = x;
                        bestRotationIndex = testPiece.getRotationIndex();
                        holdUsed = false;
                    }
                }
            }
            // Rotate the piece for the next set of positions
            testPiece = testPiece.clockwisePiece();
        }

        // Hold piece implementation for the bot
        Piece heldPiece = new TetrisPiece(tetrisBoard.getHeldPieceType());
        for(int i = 0; i < 4; i++){
            for(int x = -2; x < currentBoard.getWidth()+2; x++){
                Board newHeldBoard = tetrisBoard.testPlacement(heldPiece, x);
                
                if(newHeldBoard != null){
                     fitness = calcFitness(newHeldBoard);

                    if (fitness > maxFitness && !tetrisBoard.isHeld()){
                        maxFitness = fitness;
                        bestX = x;
                        bestRotationIndex = heldPiece.getRotationIndex();
                        holdUsed = true;
                    }
                }
            }
            heldPiece = heldPiece.clockwisePiece();
        }

        // Gets the current rotation index and x value of the current Tetris piece
        int currentRotationIndex = tetrisBoard.getCurrentPiece().getRotationIndex();
        int currentX = tetrisBoard.getCurrentPiecePosition().x;

        if (holdUsed){
            moveSequence.offer(Action.HOLD);
            currentX = tetrisBoard.getWidth()/2 - heldPiece.getWidth()/2;
            currentRotationIndex = 0;
        }

        // Loops until the currentRotationIndex and currentX match the best rotation and x values
        while(!(currentRotationIndex == bestRotationIndex && currentX == bestX)){
            // Rotate the piece and store the move in the queue
            if(currentRotationIndex != bestRotationIndex){
                if((bestRotationIndex-currentRotationIndex+4)%4 == 3){
                    moveSequence.offer(Action.COUNTERCLOCKWISE);
                    currentRotationIndex = (currentRotationIndex+3)%4;
                }
                else{
                    moveSequence.offer(Action.CLOCKWISE);
                    currentRotationIndex = (currentRotationIndex+1)%4;
                }
            }

            // Shift the piece left/right depending on the best x value
            if(currentX < bestX){
                moveSequence.offer(Action.RIGHT);
                currentX++;
            }
            if(currentX > bestX){
                moveSequence.offer(Action.LEFT);
                currentX--;
            }
        }

        //The last move of any move sequence should be to drop it
        moveSequence.offer(Action.DROP);

        // Return the first move of the sequence
        return moveSequence.poll();
    }

    /**
     * A method to sort the weights such that it incentivizes row clears
     * @param board, the current TetrisBoard
     * @return the total number of rows cleared as the indicator of how high the brain scores
     */
    public double calcScore(TetrisBoard board){
        return board.getTotalRowsCleared();
    }

    /**
     * Uses the weight values and the amounts of those values to calculate the overall score for the current Tetris board 
     * by adding the product of each weight and its corresponding value
     * @param currentBoard, the current TetrisBoard
     * @return the total fitness
     */
    public double calcFitness(Board currentBoard) {
        if(currentBoard.getMaxHeight() > JTetris.HEIGHT) return -100000;

        //Gets the weights for the current individual and the board info
        double[] weights = population[currIndividual].getWeights();
        double[] boardInfo = getBoardInfo(currentBoard);

        double fitness = 0;

        //Add weight x heuristic for each one
        for(int i = 0; i < JTetrisBrainTrainer.WEIGHT_COUNT; i++) fitness += weights[i]*boardInfo[i];

        return fitness;
    }

    /** Method to get information on the current board state
     * Calculates: 
     * 0. Maximum difference in height between columns
     * 1. Number of pillars, weighted by the actual height of the pillar (pillars are columns that must be filled with an I piece)
     * 2. Number of holes
     * 3. Number of pieces above the holes
     * 4. Aggregate difference in height between columns or the bumpiness
     * 5. Rows cleared by last piece
     * 6. Number of times an empty cell is next to a filled cell per row (row transitions)
     * 7. Maximum overall height of the board
     * 8. Number of times an empty cell is next to a filled cell per column (column transitions)
     * with each value stored in a array of doubles.
     * @param board, the current Tetris Board
     * @return the array of doubles that store all of the values
    */
    public double[] getBoardInfo(Board board){
        int width = board.getWidth();
        int height = board.getHeight();

        double[] rslt = new double[JTetrisBrainTrainer.WEIGHT_COUNT];

        // Rows Cleared
        rslt[5] = ((TetrisBoard)board).getRowsCleared();

        for(int c = 0; c < width; c++){
            boolean isHole = false;

            // Max Height Diff
            rslt[0] = Math.max(rslt[0], board.getMaxHeight()-board.getColumnHeight(c));

            // Pillars
            if(c >= 1 && c < width-1){
                int left, middle, right;
                left = board.getColumnHeight(c-1);
                middle = board.getColumnHeight(c);
                right = board.getColumnHeight(c+1);

                if(left >= 3+middle && right >= 3+middle) rslt[1] += Math.min(left,right)-middle;   
            }
            
            for(int r = 0; r < Math.min(board.getMaxHeight(), height); r++){
                // Holes
                if (board.getGrid(c, r) == null && board.getColumnHeight(c) > r) rslt[2]++;

                // Pieces Above Holes
                if(board.getGrid(c, r) == null) isHole = true;
                if(isHole && board.getGrid(c, r) != null) rslt[3]++;

                // Row transitions
                if(r < board.getMaxHeight() && c < width-1){
                    if (board.getGrid(c, r) != null && board.getGrid(c+1, r) == null) {
                        rslt[6]++;
                    }
                    if (board.getGrid(c, r) == null && board.getGrid(c+1, r) != null) {
                        rslt[6]++;
                    }
                }

                // Column transitions
                if(r < board.getColumnHeight(c)-1){
                    if (board.getGrid(c, r) != null && board.getGrid(c, r+1) == null) {
                        rslt[8]++;
                    }
                    if (board.getGrid(c, r) == null && board.getGrid(c, r+1) != null) {
                        rslt[8]++;
                    }
                }
            }

            // Bumpiness of the columns
            if(c >= 1){
                rslt[4] += Math.abs(board.getColumnHeight(c) - board.getColumnHeight(c-1));
            }
        }

        // Edge cases for the pillars not caught in the section above
        if(width >= 2 && board.getColumnHeight(1) >= 3+board.getColumnHeight(0)) rslt[1] += board.getColumnHeight(1)-board.getColumnHeight(0);
        if(width >= 2 && board.getColumnHeight(width-2) >= 3+board.getColumnHeight(width-1)) rslt[1] += board.getColumnHeight(width-2)-board.getColumnHeight(width-1);

        // Overall maximum height 
        rslt[7] = board.getMaxHeight();

        return rslt;
    }

    /**
     * Takes two Individuals’ weights and creates a new Individual by randomly picking which of the two parents’ weights to copy
     * @param parent1, the first Individual to potentially copy weights from
     * @param parent2, the second Individual to potentially copy weights from
     * @return the final Individual with the copied weights from the first and second parents
     */
    public JTetrisBrainIndividual crossOver(JTetrisBrainIndividual parent1, JTetrisBrainIndividual parent2) {
        double[] weights = new double[JTetrisBrainTrainer.WEIGHT_COUNT];
        double[] weightsParent1 = parent1.getWeights();
        double[] weightsParent2 = parent2.getWeights();
        Random r = new Random();

        for (int i = 0; i < weights.length; i++) {
            // 50/50 chance of getting weight from either parent
            weights[i] = (r.nextDouble() < 0.5) ? weightsParent1[i] : weightsParent2[i];
        }

        return new JTetrisBrainIndividual(weights);
    }

    /**
     * Randomly increases or decreases the weight values of an Individual
     * @param toMutate, the Individual whose weights will potentially be changed
     */
    public void mutate(JTetrisBrainIndividual toMutate) {
        double[] currWeights = toMutate.getWeights();
        Random r = new Random();
        
        // If a random double between 0 and 1 is less than the mutation rate constant, 
        // add a number from (-1, 1) from a Gaussian distribution to the weight
        for (int i = 0; i < currWeights.length; i++) {
            currWeights[i] += (r.nextDouble() < JTetrisBrainTrainer.MUTATION_RATE) ? r.nextGaussian() : 0;
        }

        toMutate.setWeights(currWeights);
    }

    /**
     * Fetches the array of Individuals
     * @return the array of Individuals representing all of the Individuals in a generation/epoch
     */
    public JTetrisBrainIndividual[] getPopulation() {
        return population;
    }

    /**
     * Sets the current index/Individual examined in the population array to the input integer
     * @param index, the given index in the population array to set the currentIndividual variable to
     */
    public void setCurrentIndividual(int index) {
        this.currIndividual = index;
    }
}
