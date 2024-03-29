import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;

public class Basic {

   // Final Alignmenet to be accessible by other classes.
   public static char[][] finalAlignment;

   // Where the alignmenet starts in the matrice above
   public static int startingPointFinalAlignment;

   public static void main(String[] args) 
   {
      InputExtract input = new InputExtract(args);
      String sequence1 = inputGenerator1(input);
      String sequence2 = inputGenerator2(input);
      double start = getTimeInMilliseconds();
      double beforeUsedMem = getMemoryInKB();
      MinimumPenalty computeM = new MinimumPenalty(sequence1, sequence2);
      int[][] matrixM = computeM.getMinimumPenaltyTable();

      // compute the final optimal alignment
      finalAlignment = computeOptimalAlignment(matrixM, sequence1, sequence2);

      double afterUsedMem = getMemoryInKB();
      double end = getTimeInMilliseconds();
      double timeTaken = end - start;
      double totalUsage = afterUsedMem - beforeUsedMem;



      output(args, matrixM[sequence1.length()][sequence2.length()], finalAlignment, timeTaken, totalUsage);
   }
   /*
   * Writes result to an output file 
   * 
   * String[] Args is the original args array that the user inputted (second value is the output file name)
   * int cost is the cost of the final alignment (will be written to the first line of the output file)
   * Alignment a is the actual alignment object (will be written to 2nd and 3rd lines of output file)
   * double time is the time that the execution took (will be written to the 4th line of the output file)
   * double mem is the amount of memeory that the execution took (will be written to the 5th line of the output file)
   */
   public static void output(String[] args, int cost, char[][] a, double time, double mem)
   {
      if(args.length < 2)
      {
         System.out.println("User must input two arguments. Program only received " + args.length + " arguments");
         return;
      }
      try
      {
         String x = "";
         String y = "";
         for(int i = startingPointFinalAlignment+1; i < a[0].length; i++)
         {
            x += a[0][i] == ' '? '_' : a[0][i];
            y += a[1][i] == ' '? '_' : a[1][i]; 
         }

         PrintWriter writer = new PrintWriter(args[1], "UTF-8");
         writer.println(cost);
         writer.println(x);
         writer.println(y);
         writer.println(time);
         writer.println(mem);
         writer.close();
      }
      catch(Exception f)
      {
         System.out.println("???");
      }
   }
   
   /*
   * Gets currently used memory
   */
   public static double getMemoryInKB()
   {
      double total = Runtime.getRuntime().totalMemory();
      return (total - Runtime.getRuntime().freeMemory())/10e3;
   }
   
   
   /*
   * Gets time in milliseconds with nanosecond accuracy
   */
   public static double getTimeInMilliseconds()
   {
      return System.nanoTime()/10e6;
   }

   private static String inputGenerator1(InputExtract input) {
      byte[] sequence1 = input.getSequence1();
      String s = "";
      for (byte b : sequence1) {
         switch (b) {
            case 0:
               s += "A";
               break;
            case 1:
               s += "T";
               break;
            case 2:
               s += "C";
               break;
            case 3:
               s += "G";
               break;
         }

      }
      return s;
   }

   private static String inputGenerator2(InputExtract input) {
      byte[] sequence2 = input.getSequence2();
      String s = "";
      for (byte b : sequence2) {
         switch (b) {
            case 0:
               s += "A";
               break;
            case 1:
               s += "T";
               break;
            case 2:
               s += "C";
               break;
            case 3:
               s += "G";
               break;
         }

      }
      return s;
   }

   /*
    * This method computes the final optimal aligment given matrix M
    * 
    * input: matrix M
    * output: array with opotimal sequence alignment
    */
   public static char[][] computeOptimalAlignment(int[][] m, String sequenceA, String sequenceB) {
      // The best is at position M(i=n, j=m)
      int lenSequenceA = sequenceA.length();
      int lenSequenceB = sequenceB.length();
      // We are going to do recursion through M to get the actual best
      char[][] opt = findNextOpt(m, lenSequenceA-1, lenSequenceB-1,
            new char[2][lenSequenceA + lenSequenceB], lenSequenceA + lenSequenceB - 1,
            sequenceA, sequenceB);
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
    *         0 = A, 1 = T, 2 = C, 3 = G, " " = 4
    */
   public static char[][] findNextOpt(int[][] m, int i, int j, char[][] currentOpt,
         int counter, String sequenceA, String sequenceB) {

      // recursion done, solution found.
      if (i==-1 && j==-1){ // ALL DONE
         //System.out.println("DONE");
         startingPointFinalAlignment = counter;
         finalAlignment = currentOpt;
         return currentOpt;
      }
      

      // on alpha matrix 0 = A, 1 = T, 2 = C, 3 = G.
      // match i with j
      else if (i > -1 && j > -1 && m[i+1][j+1] == ((getMismatchPenalty(sequenceA.charAt(i),sequenceB.charAt(j))) + m[i][j])) {
         currentOpt[0][counter] = sequenceA.charAt(i);
         currentOpt[1][counter] = sequenceB.charAt(j);
         return findNextOpt(m, i - 1, j - 1, currentOpt, counter - 1, sequenceA, sequenceB);
      }

      // space in sequence B
      else if (i > -1 && m[i+1][j+1] == (30 + m[i][j+1])) {
         currentOpt[0][counter] = sequenceA.charAt(i);
         currentOpt[1][counter] = ' ';
         return findNextOpt(m, i -1 , j, currentOpt, counter - 1, sequenceA, sequenceB);
      }

      // space in sequence A
      else if (j > -1 && m[i+1][j+1] == (30 + m[i+1][j])){
         currentOpt[0][counter] = ' ';
         currentOpt[1][counter] = sequenceB.charAt(j);
         return findNextOpt(m, i, j - 1, currentOpt, counter - 1, sequenceA, sequenceB);
      }

      //else there is an error
      else{
         System.out.println("ERROR - no match between penalty matrix and sequence");
         return currentOpt;
      }
   }

   /**
    * Get mismatch penalty between char x and char y.
    */
   private static int getMismatchPenalty(char x, char y) {
      int mismatch = 0;

      if (x == y) {
         mismatch = 0;
      }else if ((x == 'A' && y == 'C') || (x == 'C' && y == 'A')) {
         mismatch = 110;
      } else if ((x == 'A' && y == 'G') || (x == 'G' && y == 'A')) {
         mismatch = 48;
      } else if ((x == 'A' && y == 'T') || (x == 'T' && y == 'A')) {
         mismatch = 94;
      } else if ((x == 'C' && y == 'G') || (x == 'G' && y == 'C')) {
         mismatch = 118;
      } else if ((x == 'C' && y == 'T') || (x == 'T' && y == 'C')) {
         mismatch = 48;
      } else if ((x == 'G' && y == 'T') || (x == 'T' && y == 'G')) {
         mismatch = 110;
      }
      return mismatch;
   }
}

class InputExtract {
   private String[] args;

   /*
    * String[] args: The command line arguments given by the user, where the
    * first element contains the name of the file containing the sequences
    * to be loaded
    */
   public InputExtract(String[] args) {
      this.args = args;
   }

   /*
    *
    * Returns the first DNA sequence in the file, in the form of a byte array where
    * 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequence1() {
      return getSequenceI(args[0], 1);
   }

   /*
    *
    * Returns the second DNA sequence in the file, in the form of a byte array
    * where 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequence2() {
      return getSequenceI(args[0], 2);
   }

   /*
    * String inputFile: The input file which contains the sequences
    * int se: The sequence number of the sequence you wish to get (either 1 or 2)
    *
    * Returns the DNA sequence in a byte array, where 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequenceI(String inputFile, int se) {
      ArrayList<String> file = loadFile(inputFile);
      String base = null;
      ArrayList<Integer> indexes = null;
      int j = 0;
      for (int p = 0; p < se; p++) {
         base = null;
         indexes = new ArrayList<>();
         for (; j < file.size(); j++) {
            if (file.get(j).trim().length() == 0)
               continue;
            try {
               int x = Integer.parseInt(file.get(j));
               indexes.add(x);
            } catch (Exception e) {
               if (base == null)
                  base = file.get(j);
               else
                  break;
            }
         }
      }

      String output = base;
      output = output.toUpperCase();
      for (int index : indexes) {
         output = output.substring(0, index + 1) + output + output.substring(index + 1);
      }

      byte[] outputBytes = new byte[output.length()];

      for (int i = 0; i < outputBytes.length; i++) {
         switch (output.charAt(i)) {
            case 'A':
               outputBytes[i] = 0;
               break;
            case 'T':
               outputBytes[i] = 1;
               break;
            case 'C':
               outputBytes[i] = 2;
               break;
            case 'G':
               outputBytes[i] = 3;
               break;
         }
      }

      return outputBytes;
   }

   private static ArrayList<String> loadFile(String fileName) {
      ArrayList<String> ret = new ArrayList<String>();
      try {
         File f = new File(fileName);
         Scanner r = new Scanner(f);
         while (r.hasNextLine()) {
            String data = r.nextLine();
            ret.add(data);
         }
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e.toString());
      }
      return ret;

   }
}

/**
 * MinimumPenalty class computes the minimum penalty of an alignment between
 * sequence X and Sequence Y.
 */
class MinimumPenalty {

   private final static int pGap = 30;
   private int[][] tableM;
   private String sequence1;
   private String sequence2;

   /**
    * Construct MinimumPenalty object using String x and String y.
    */
   public MinimumPenalty(String x, String y) {
      sequence1 = x;
      sequence2 = y;
      int tableSize = sequence1.length() + sequence2.length() + 1;
      tableM = new int[tableSize][tableSize];
      
      for (int i = 0; i <= tableSize - 1; i++){
         tableM[i][0] = i * pGap;
         tableM[0][i] = i * pGap;
      }
   }

   /**
    * Get mismatch penalty between char x and char y.
    */
   private int getMismatchPenalty(char x, char y) {
      int mismatch = 0;

      if (x == y) {
         mismatch = 0;
      } else if ((x == 'A' && y == 'C') || (x == 'C' && y == 'A')) {
         mismatch = 110;
      } else if ((x == 'A' && y == 'G') || (x == 'G' && y == 'A')) {
         mismatch = 48;
      } else if ((x == 'A' && y == 'T') || (x == 'T' && y == 'A')) {
         mismatch = 94;
      } else if ((x == 'C' && y == 'G') || (x == 'G' && y == 'C')) {
         mismatch = 118;
      } else if ((x == 'C' && y == 'T') || (x == 'T' && y == 'C')) {
         mismatch = 48;
      } else if ((x == 'G' && y == 'T') || (x == 'T' && y == 'G')) {
         mismatch = 110;
      }
      return mismatch;
   }

   /**
    * Compute and return the minimum penalty table using MinimumPenalty object.
    */
   public int[][] getMinimumPenaltyTable() {
      for (int i = 1; i <= sequence1.length(); i++) {
         for (int j = 1; j <= sequence2.length(); j++) {
            tableM[i][j] = Math.min(
                  Math.min(tableM[i - 1][j - 1] + getMismatchPenalty(sequence1.charAt(i - 1), sequence2.charAt(j - 1)),
                        tableM[i - 1][j] + pGap),
                  tableM[i][j - 1] + pGap);
         }
      }
      return tableM;
   }
}
