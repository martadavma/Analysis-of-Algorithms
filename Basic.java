/* This class is the main class of the Vasic Dynamic Programming Method.
 * 
 * Given an input.txt file it performs the necessary operations to fill
 * the given output.txt with the optimal sequence alignmenet for DNA.
 */ 

public class Basic {

    // sequence A
    public static byte[] sequenceA;

    // length of sequence A
    public static int lenSequenceA;

    // sequence B
    public static byte[] sequenceB;

    // length of sequence B
    public static int lenSequenceB;

    // matrix containing all alpha 0 = A, 1 = T, 2 = C, 3 = G.
    public static int[][] alpha;

    // delta, cost of puting a space
    public static int delta;

    /**
     * Main method
     * 
     * @param args
     */
    public static void main(String[] args) {

        // create InputExtract instance
        InputExtract inputExtract = new InputExtract(args);
        sequenceA = inputExtract.getSequenceA();
        sequenceB = inputExtract.getSequenceB();

        // compute M from both sequences
        int[][] m = computeM(sequenceA, sequenceB);

        // compute the final optimal alignment
        byte[][] optAlignment = computeOptimalAlignment(m);

        // OuputGenerator overwrites the output.txt file.
        // Basic can terminate afterwards
        new OutputGenerator(optAlignment, args);

        // terminate
    }

    /*
     * This method generates the M matrix
     * 
     * input: initalMatrix
     * output: M matrix (#TODO: I put 3 dimentions, this can be changed)
     */
    public static int[][][][] computeM(byte[] sequenceA, byte[] sequenceB) {

        return null;
    }

    /*
     * This method computes the final optimal aligment given matrix M
     * 
     * input: matrix M
     * output: array with opotimal sequence alignment
     */
    public static byte[][] computeOptimalAlignment(int[][] m) {
        // The best is at position M(i=n, j=m)
        // We are going to do recursion through M to get the actual best
        byte[][] opt = findNextOpt(m, lenSequenceA, lenSequenceB,
                new byte[2][lenSequenceA + lenSequenceB], lenSequenceA + lenSequenceB);
        return opt;
    }

    /**
     * Tail recursive method to avoid memory waist
     * 
     * @param m
     * @param i
     * @param j
     * @param currentOpt
     * @return
     *  0 = A, 1 = T, 2 = C, 3 = G, " " = 4
     */
    public static byte[][] findNextOpt(int[][] m, int i, int j, byte[][] currentOpt, 
        int counter){
        // recursion done, solution find.
        if (i == 0 && j == 0  ){
            return currentOpt;
        }

        // on alpha matrix 0 = A, 1 = T, 2 = C, 3 = G.
        // match i with j
        else if (m[i][j] == (alpha[sequenceA[i]][sequenceB[j]] + m[i-1][j-1])){
            currentOpt[0][counter] = sequenceA[i];
            currentOpt[1][counter] = sequenceB[j];
            return findNextOpt(m, i-1, j-1, currentOpt, counter -1);
        }

        // space in sequence B
        else if (m[i][j] == (delta + m[i-1][j])){
            currentOpt[0][counter] = sequenceA[i];
            currentOpt[1][counter] = 4;
            return findNextOpt(m, i-1, j, currentOpt, counter -1);
        }
        
        // space in sequence B
        else {// if (m[i][j] == (delta + m[i][j-1])){
            currentOpt[0][counter] = 4;
            currentOpt[1][counter] = sequenceA[j];
            return findNextOpt(m, i, j-1, currentOpt, counter - 1);
        }
    }
}