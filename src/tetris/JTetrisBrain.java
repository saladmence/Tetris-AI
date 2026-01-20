package tetris;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Timer;

/**
 * For if you want to play a game of Tetris with the brain
 */
public class JTetrisBrain extends JTetrisBrainTrainer{
    static int DELAY = 0;
    static int movesPerFrame = 5000;
    static private Timer loop;
    static private boolean usingFinalWeights = true;
    private double totalScore = 0;
    private int counter = 0;

    /**
     * Initializes the brain’s weights by reading from a file name defined in JTetrisBrain.
     * If the brain is using hard-coded final weights, then a new TetrisBrain is created using those weights (already defined in TetrisBrain).
     * If it’s not, the file defined in JTetrisBrain is read and used to create the brain. 
     */
    JTetrisBrain(){
        super();
        if(usingFinalWeights){
            brain = new TetrisBrain();
            return;
        }

        try(BufferedReader bf = new BufferedReader(new FileReader(JTetrisBrain.POPULATION_FILENAME))){
            bf.readLine();
    
            String[] strWeights = bf.readLine().split(" ");
            double[] weights = new double[JTetrisBrainTrainer.WEIGHT_COUNT];
    
            for(int i = 0; i < weights.length; i++) weights[i] = Double.parseDouble(strWeights[i]);
            brain = new TetrisBrain(weights);
        }
        catch(IOException e){
            return;
        }
    }

    /**
     * Executes the game. 
     * For every tick, the brain will perform a certain number of actions defined in movesPerFrame. 
     */
    public static void main(String[] args) {
        JTetrisBrainTrainer.BOARD_PERCENTAGE = 1;
        JTetrisBrain.usingGUI = true;

        JTetrisBrain game = new JTetrisBrain();
        createGUI(game);

        game.brain.setCurrentIndividual(0);
        loop = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < movesPerFrame && game.gameOn; i++){
                    game.tick(game.brain.nextMove(game.board));
                }
            }
        });
    }

    /**
     * Stops the game and updates the GUI with the pieces per second, average score, and run attempt. 
     * If the number of games ran is less than 100, a new game is immediately started. 
     */
    @Override
    public void stopGame() {
        loop.stop();
        super.stopGame();

        totalScore += ((TetrisBoard)board).getScore();
        ((TetrisBoard)board).displayStatus();
        double delta = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.printf("PPS: %.2f\n", count/delta);
        System.out.printf("Average Score: %.2f\n", totalScore/counter);
        System.out.printf("Run Number: %d\n", counter);
    }

    /**
     * Starts the game and the timer. 
     * The counter is also incremented to keep track of how many games are played. 
     */
    @Override
    public void startGame() {
        counter++;
        loop.start();
        super.startGame();
    }
}
