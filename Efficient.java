import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Efficient {
   public static void main (String[] args)
   {
      InputExtract input = new InputExtract(args);
      String sequence1 = inputGenerator1(input);
      String sequence2 = inputGenerator2(input);
      System.out.println(sequence1);
      System.out.println(sequence2);
      MinimumPenalty computeM = new MinimumPenalty(sequence1, sequence2);
      System.out.println("Minimum Penalty is " + computeM.getMinimumPenalty());
   }

   private static String inputGenerator1(InputExtract input){
      byte[] sequence1 = input.getSequence1();
      String s = "";
      for(byte b : sequence1)
      {
         switch(b)
         {
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
   private static String inputGenerator2(InputExtract input){
      byte[] sequence2 = input.getSequence2();
      String s = "";
      for(byte b : sequence2)
      {
         switch(b)
         {
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
     * Finds the optimal split point between byte array x and array y
     * X is always split down the middle which is floor(x.length/2)
     * This function returns the optimal split point in y in the form of an integer
     * WARNING this has not been tested yet as of 11/19
     */
    public static int findSplit(ByteArray x, ByteArray y)
    {
        int xSplit = x.length/2;
        ByteArray xl = x.slice(0, xSplit);
        ByteArray xr = x.slice(xSplit, x.length);

        int[][] ml = computeM(xl, y);
        int[][] mr = computeM(xr, y);

        int splitCost = -1;
        int split = -1;

        for(int k = 0; k < y.length; k++)
        {
            int cost = ml[k][x.length-1] + mr[y.length-k][xr.length-1];
            if(split == -1 || cost < splitCost)
            {
                splitCost = cost;
                split = k;
            }
        }
        return split;


    }
}
class InputExtract
{
   private String[] args;

   /*
    * String[] args: The command line arguments given by the user, where the
    * first element contains the name of the file containing the sequences
    * to be loaded
    */
   public InputExtract(String[] args)
   {
      this.args = args;
   }

   /*
    *
    * Returns the first DNA sequence in the file, in the form of a byte array where 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequence1()
   {
      return getSequenceI(args[0], 1);
   }

   /*
    *
    * Returns the second DNA sequence in the file, in the form of a byte array where 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequence2()
   {
      return getSequenceI(args[0], 2);
   }

   /*
    * String inputFile: The input file which contains the sequences
    * int se: The sequence number of the sequence you wish to get (either 1 or 2)
    *
    * Returns the DNA sequence in a byte array, where 0=A, 1=T, 2=C, 3=G
    */
   public byte[] getSequenceI(String inputFile, int se)
   {
      ArrayList<String> file = loadFile(inputFile);
      String base=null;
      ArrayList<Integer> indexes=null;
      int j = 0;
      for(int p = 0; p < se; p++)
      {
         base = null;
         indexes = new ArrayList<>();
         for(; j < file.size(); j++)
         {
            if(file.get(j).trim().length() == 0)
               continue;
            try
            {
               int x = Integer.parseInt(file.get(j));
               indexes.add(x);
            }
            catch(Exception e)
            {
               if(base == null)
                  base = file.get(j);
               else
                  break;
            }
         }
      }

      String output = base;
      output = output.toUpperCase();
      for(int index : indexes)
      {
         output = output.substring(0, index+1) + output + output.substring(index+1);
      }

      byte[] outputBytes = new byte[output.length()];

      for(int i = 0; i < outputBytes.length; i++)
      {
         switch(output.charAt(i))
         {
            case 'A':
               outputBytes[i] = 0; break;
            case 'T':
               outputBytes[i] = 1; break;
            case 'C':
               outputBytes[i] = 2; break;
            case 'G':
               outputBytes[i] = 3; break;
         }
      }

      return outputBytes;
   }

   private static ArrayList<String> loadFile(String fileName)
   {
      ArrayList<String> ret = new ArrayList<String>();
      try
      {
         File f = new File(fileName);
         Scanner r = new Scanner(f);
         while(r.hasNextLine())
         {
            String data = r.nextLine();
            ret.add(data);
         }
      }
      catch(FileNotFoundException e)
      {
         throw new RuntimeException(e.toString());
      }
      return ret;

   }
}

/**
 * MinimumPenalty class computes the minimum penalty of an alignment between sequence X and Sequence Y.
 */
class MinimumPenalty{

   private final static int pGap = 30;
   private int[][] tableM;
   private String sequence1;
   private String sequence2;

   /**
    * Construct MinimumPenalty object using String x and String y.
    */
   public MinimumPenalty(String x, String y){
      sequence1 = x;
      sequence2 = y;
      int tableSize = sequence1.length() + sequence2.length() + 1;
      tableM = new int [tableSize][tableSize];
   }

   /**
    * Get mismatch penalty between char x and char y.
    */
   private int getMismatchPenalty(char x, char y){
      int mismatch = 0;

      if (x == y){
         mismatch = 0;
      }
      else if ((x == 'A' && y == 'C') || (x == 'C' && y == 'A')){
         mismatch = 110;
      }
      else if ((x == 'A' && y == 'G') || (x == 'G' && y == 'A')){
         mismatch = 48;
      }
      else if ((x == 'A' && y == 'T') || (x == 'T' && y == 'A')){
         mismatch = 94;
      }
      else if ((x == 'C' && y == 'G') || (x == 'G' && y == 'C')){
         mismatch = 118;
      }
      else if ((x == 'C' && y == 'T') || (x == 'T' && y == 'C')){
         mismatch = 48;
      }
      else if ((x == 'G' && y == 'T') || (x == 'T' && y == 'G')){
         mismatch = 110;
      }
      return mismatch;
   }

   /**
    * Compute and return the minimum penalty using MinimumPenalty object.
    */
   public int getMinimumPenalty(){
      for (int i = 1; i <= sequence1.length(); i++){
         for (int j = 1; j <= sequence2.length(); j++){
            tableM[i][j] = Math.min(Math.min(tableM[i - 1][j - 1] + getMismatchPenalty(sequence1.charAt(i - 1), sequence2.charAt(j -1)),
                        tableM[i - 1][j] + pGap),
                  tableM[i][j-1] + pGap);
         }
      }
      return tableM[sequence1.length()][sequence2.length()];
   }
   
    
}


/*
* Stores an array of bytes and allows for taking subsections of the byte array without copying data
* 
* public ByteArray(int length) 
* Constructor that breates a byte array of a specific length 
* 
* public ByteArray slice(int start, int end) 
* create a sub array from start index to end index without copying data
* 
* public byte get(int index) 
* get the value at a specific index
* 
* public byte set(int index, byte value)
* set the value at a specific index to the value specified
* 
* final int length
* The length of the byte array. You cannot modify this value
*/
class ByteArray
{
    private byte[] buffer;
    private int start;
    public final int length;


    public ByteArray(int length)
    {
        buffer = new byte[length];
        this.start = 0;
        this.length = length;
    }
    private ByteArray(byte[] buffer, int start, int end)
    {
        this.buffer = buffer;
        this.length = end-start;
        this.start = start;
        
    }
    public ByteArray slice(int start, int end)
    {
        if(end < 0) throw new RuntimeException("End index is negative " + end);
        if(end > this.length) throw new RuntimeException("End index " + end + " is larger than the length of the array " + this.length);
        if(start < 0) throw new RuntimeException("Start index is negative " + start);
        if(start > this.length) throw new RuntimeException("Start index " + start + " is larger than the length of the array " + this.length);
        return new ByteArray(buffer, this.start+start, this.start+end);
    }
    public byte get(int index)
    {
        if(index < 0) throw new RuntimeException("Cannot access negative index " + index);
        if(index > this.length) throw new RuntimeException("Index " + index + " is larger than the length of the array " + this.length);
        return buffer[index + this.start];
    }
    public void set(int index, byte value)
    {
        if(index < 0) throw new RuntimeException("Cannot access negative index " + index);
        if(index > this.length) throw new RuntimeException("Index " + index + " is larger than the length of the array " + this.length);
        buffer[index + this.start] = value;
    }
    public String toString()
    {
        String ret = "[";
        for(int i = 0; i < this.length; i++)
        {
            ret += get(i) + ", ";
        }
        if(ret.endsWith(", "))
        {
            ret = ret.substring(0, ret.length()-2);
        }
        ret += "]";
        
        return ret;

    }

}
