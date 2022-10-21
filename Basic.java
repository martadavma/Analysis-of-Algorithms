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

    /**
     * Main method
     * 
     * @param args
     */
    public static void main (String[] args) {

        // create InputExtract instance
        InputExtract inputExtract = new InputExtract(args);
        sequenceA = inputExtract.getSequenceA();
        sequenceB = inputExtract.getSequenceB();

        // compute M from both sequences
        int[][][][] m = computeM(sequenceA, sequenceB);

        // compute the final optimal alignment
        byte[][] optAlignment = computeOptimalAlignment(m);

        // OuputGenerator overwrites the output.txt file.
        // Basic can terminate afterwards
        new OutputGenerator(optAlignment, args);

        // terminate
    }

    /* This method generates the M matrix
     * 
     * input: initalMatrix
     * output: M matrix (#TODO: I put 3 dimentions, this can be changed)
     */
    public static int[][][][] computeM(byte[] sequenceA, byte[] sequenceB){

        return null;
    }

    /* This method computes the final optimal aligment given matrix M
     * 
     * input: matrix M
     * output: array with opotimal sequence alignment
     */
    public static byte[][] computeOptimalAlignment(int[][][][] m){
        // The best is at position M(i=0, j=n-1)
        // We are going to do recursion through M to get the actual best 
        int x = 100; //TODO: find way to get this max
        byte[][] opt = findNextOpt(m, 0, m[0].length, new byte[2][x]);
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
     */
    public static byte[][] findNextOpt(int[][][][] m, int i, int j, byte[][] currentOpt){
        if (i == 0 && j == 0  ){
            return currentOpt;
        }
        else if (m[i][j][lenSequenceA][lenSequenceB] == m[i-1][j][lenSequenceA][lenSequenceB]){
            
        }
        


        return null;
    }
}