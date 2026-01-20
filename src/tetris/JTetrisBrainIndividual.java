package tetris;

import java.util.Random;

//Class to represent an individual in the population for the genetic algorithm
public class JTetrisBrainIndividual {
        /**
        * Indexes in weights and what they represent: 
        * 0. Maximum difference in height between columns
        * 1. Number of pillars, weighted by the actual height of the pillar (pillars are columns that must be filled with an I piece)
        * 2. Number of holes
        * 3. Number of pieces above the holes
        * 4. Aggregate difference in height between columns or the bumpiness
        * 5. Rows cleared by last piece
        * 6. Number of times an empty cell is next to a filled cell per row (row transitions)
        * 7. Maximum overall height of the board
        * 8. Number of times an empty cell is next to a filled cell per column (column transitions)
        */
        private double[] weights = new double[JTetrisBrainTrainer.WEIGHT_COUNT];
        private double fitness;
        private double score;

        /**
         * Creates a new Individual with randomized weights. 
         */
        public JTetrisBrainIndividual() {
            Random r = new Random();
            for (int i = 0; i < weights.length; i++) {
                weights[i] = r.nextDouble()*2 - 1;
            }
            fitness = 0;
        }

        /**
         * Sets the given weights to weights
         * @param weights, the specified array of doubles to designate as the Individuals' weights
         */
        public JTetrisBrainIndividual(double[] weights) {
            this.weights = weights;
        }

        /**
         * Fetches the current array representing the Individual's weights
         * @return the instance variable representing the Individual’s weights
         */
        public double[] getWeights() {
            return weights;
        }

        /**
         * Sets the given weights to weights, the instance variable representing the Individual’s weights
         * @param newWeights, the array of doubles to set to the Individual's current weights
         */
        public void setWeights(double[] newWeights) {
            weights = newWeights;
        }

        /**
         * Fetches the current fitness of the Individual
         * @return fitness, the instance variable representing the fitness calculated from calcFitness
         */
        public double getFitness() {
            return fitness;
        }

        /**
         * Sets the input fitness to the fitness instance variable
         * @param fitness, the value to set the current fitness to
         */
        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        /**
         * Sets the score to the given double
         * @param score, the value to set the Individual's score to
         */
        public void setScore(double score){ this.score = score; }

        /**
         * Fetches the value currently stored in the score instance variable
         * @return the current score of the Individual
         */
        public double getScore(){ return score; }

        /**
         * Checks if two Individuals are the same through their weights, fitness, and score
         * @param o, the object to compare the Individual to
         */
        @Override
        public boolean equals(Object o){
            JTetrisBrainIndividual other = (JTetrisBrainIndividual)o;

            for(int i = 0; i < weights.length; i++){
                if(this.weights[i] != other.getWeights()[i]) return false;
            }

            return true;
        }
    }
