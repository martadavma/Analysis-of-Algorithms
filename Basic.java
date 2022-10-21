/* This class is the main class of the Vasic Dynamic Programming Method.
 * 
 * Given an input.txt file it performs the necessary operations to fill
 * the given output.txt with the optimal sequence alignmenet for DNA.
 */

public class Basic {

    // Main method
    public static void main (String[] args) {

        // create InputExtract instance
        InputExtract inputExtract = new InputExtract(args);
        byte[] sequenceA = inputExtract.getSequenceA();
        byte[] sequenceB = inputExtract.getSequenceB();

        // compute M from both sequences
        int[][][] m = computeM(sequenceA, sequenceB);

        // compute the final optimal alignment
        byte[] optAlignment = computeOptimalAlignment(m);

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
    public static int[][][] computeM(byte[] sequenceA, byte[] sequenceB){

        return null;
    }

    /* This method computes the final optimal aligment given matrix M
     * 
     * input: matrix M
     * output: array with opotimal sequence alignment
     */
    public static byte[] computeOptimalAlignment(int[][][] m){

        return null;
    }
}