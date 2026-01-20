package tetris;

import java.util.ArrayList;
import java.util.Collections;

// JTetris variant that uses the 7-bag system for piece generation
public class JTetrisExtra extends JTetris{
    //Uses a bag to store the pieces
    private ArrayList<Integer> bag = new ArrayList<>();

    public static void main(String[] args) {
        createGUI(new JTetrisExtra());
    }

    /**
     * Fills the bag containing all 7 distinct Tetris pieces 
     * and starts the game.
     */
    @Override
    public void startGame() {
        //Fill the bag at the start of the game
        fillBag();
        super.startGame();
    }

    /**
     * If the bag is empty, the bag is first refilled. 
     * Picks the next piece by taking the first index in the shuffled array of piece indexes. 
     */
    @Override
    public Piece pickNextPiece() {
        //Refill bag if empty
        if(bag.isEmpty()) fillBag();
        //Gets the piece from the front of the bag
        return PIECES[bag.remove(0)];
    }

    /**
     * If the bag is not empty, it is emptied. 
     * Then every index from 0-6 is added into the bag and shuffled. 
     */
    private void fillBag(){
        if(!bag.isEmpty()) bag.clear();
        for(int i = 0; i < 7; i++) bag.add(i);
        Collections.shuffle(bag);
    }
}
