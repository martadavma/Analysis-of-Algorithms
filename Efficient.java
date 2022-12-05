import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.PrintWriter;

public class Efficient
{
    public static final byte A = (byte)0, T = (byte)1, C = (byte)2, G = (byte)3, GAP = (byte)4;
    public static void main (String[] args)
    { 
        InputExtract input = new InputExtract(args);
        ByteArray sequence1 = input.getSequence1();
        ByteArray sequence2 = input.getSequence2();

        double start = getTimeInMilliseconds();
        double beforeUsedMem = getMemoryInKB();
        Tuple<Integer, Alignment> costAndAlignment = findAlignment(sequence1, sequence2);
        double afterUsedMem = getMemoryInKB();
        double end = getTimeInMilliseconds();
        double timeTaken = end - start;

        double totalUsage = afterUsedMem - beforeUsedMem;

        output(args, costAndAlignment.a, costAndAlignment.b, timeTaken, totalUsage);
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
    public static void output(String[] args, int cost, Alignment a, double time, double mem)
    {
        if(args.length < 2)
        {
            System.out.println("User must input two arguments. Program only received " + args.length + " arguments");
            return;
        }
        try
        {
            PrintWriter writer = new PrintWriter(args[1], "UTF-8");
            writer.println(cost);
            writer.println(a.getXString());
            writer.println(a.getYString());
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
    
    
    /*
     * This function executes the entire alignment algorithm using the 
     * divide and conquer technique
     * 
     * Takes two ByteArrays, which represent the two DNA strings that we want to find 
     * similarity for
     * 
     * This function returns both the cost as well as the alignment (int cost, Alignment alignment)
     * 
     * Coded by: Peter Looi
     */
    public static Tuple<Integer, Alignment> findAlignment(ByteArray x, ByteArray y)                                                                                                                                             {return findAlignment(x, y, 0);}public static Tuple<Integer, Alignment> findAlignment(ByteArray x, ByteArray y, int depth)
    {
        //if both x and y are strings of length 0, then the alignment is trivial
        if(x.length == 0 && y.length == 0)
            return new Tuple<>(0, new Alignment(new byte[]{}, new byte[]{}));

        //if x is empty but y has one character, then the alignment has a gap in x and the one character in y
        else if(x.length == 0 && y.length == 1)
            return new Tuple<>(MinimumPenalty.pGap, new Alignment(new byte[]{GAP}, new byte[]{y.get(0)}));

        //if x has one character but y is empty, then the alignment has a gap in y and the one character in x
        else if(x.length == 1 && y.length == 0)
            return new Tuple<>(MinimumPenalty.pGap, new Alignment(new byte[]{x.get(0)}, new byte[]{GAP}));

        //if both x and y have one character, either you match the two characters together or you have two blanks
        else if(x.length == 1 && y.length == 1)
        {
            int mismatchPenalty = MinimumPenalty.getMismatchPenalty(x.get(0), y.get(0));

            //if the mismatch penalty has a lower cost than two blanks, then match the two characters together
            if(mismatchPenalty < 2*MinimumPenalty.pGap)
                return new Tuple<>(mismatchPenalty, new Alignment(new byte[]{x.get(0)}, new byte[]{y.get(0)}));

            //if two blanks has a lower cost than the mismatch penalty, then do two blanks
            else
                return new Tuple(MinimumPenalty.pGap*2, new Alignment(new byte[]{x.get(0), GAP}, new byte[]{GAP, y.get(0)}));

        }
        
        int xSplit, ySplit, cost;



        /*
         * Choose the longer one out of x or y, and the longer one is
         * the one that gets cut perfectly in half
         */

        //if x is a longer string than y, then x is the one that gets cut perfectly in half 
        //and we will find the optimal split point for y
        if(x.length >= y.length)
        {
            Tuple<Integer,Integer> ysplitinfo = findSplit(x, y);
            xSplit = x.length/2;
            ySplit = ysplitinfo.a;
            cost = ysplitinfo.b;
        }

        //if y is a longer string than x, then y is the one that gets cut perfectly in half 
        //and we will find the optimal split point for x
        else
        {
            Tuple<Integer,Integer> xsplitinfo = findSplit(y, x);
            ySplit = y.length/2;
            xSplit = xsplitinfo.a;
            cost = xsplitinfo.b;
        }

        //divide x into xl (x left portion) and xr (x right portion)
        ByteArray xl = x.slice(0, xSplit);
        ByteArray xr = x.slice(xSplit, x.length);

        //divide y into yl (y left portion) and yr (y right portion)
        ByteArray yl = y.slice(0, ySplit);
        ByteArray yr = y.slice(ySplit, y.length);
         

        //find the optimal alignment for xl with yl, and then find the optimal alignment for xr and yr
        Tuple<Integer, Alignment> leftAlignment = findAlignment(xl, yl, depth+1);
        Tuple<Integer, Alignment> rightAlignment = findAlignment(xr, yr, depth+1);

        //join the two optimal alignments together
        leftAlignment.b.append(rightAlignment.b);
        Alignment alignment = leftAlignment.b;
        
        //return both the cost and the optimal alignment
        return new Tuple<>(cost, alignment);
    }



    /*
     * Finds the optimal split point between byte array x and byte array y where
     * X is always split down the middle, which is floor(x.length/2),
     * and we find the optimal split point of y
     * This function returns the optimal split point in y in the form of an integer
     * as well as the optimal cost of that split, also as an integer
     * returns (int splitPointInY, int costOfThisSplit)
     * 
     * Coded by: Peter Looi
     */
    public static Tuple<Integer, Integer> findSplit(ByteArray x, ByteArray y)
    {
        
        int xSplit = x.length/2;//x split point is always half way

        //find the left and right halves of x
        ByteArray xl = x.slice(0, xSplit);
        ByteArray xr = x.slice(xSplit, x.length);
        
        //run the memory efficient dynamic programming algorithm for
        //1) the left half of x and y
        //2) the right half of x and y

        int[] leftLastColumn = new MinimumPenaltyLessMemory(xl, y).getLastColPenalties();
        int[] rightLastColumn = new MinimumPenaltyLessMemory(xr.reverse(), y.reverse()).getLastColPenalties();

        /*
         * for any k, leftLastColumn[k] is the cost of matching the left half of x with y from 0 to k
         * for any k, rightLastColumn[k] is the cost of matching the right half of x with y from k to the end
         * 
         * leftLastColumn and rightLastColumn have lengths of (y.length + 1) because if y is n characters long,
         * there are n+1 possible places to split y
         */
         

        //find the optimal split point
        int splitCost = -1;//this is the cost of the optimal split point so far
        int split = 0;//this is the index of the optimal split point so far
        for(int k = 0; k < rightLastColumn.length; k++)
        {
            int cost = leftLastColumn[k] + rightLastColumn[rightLastColumn.length-k-1];
            if(splitCost == -1 || cost < splitCost)
            {
                splitCost = cost;
                split = k;
            }
        }

        //return the optimal split point as well as the cost of the optimal split point
        return new Tuple<Integer, Integer>(split, splitCost);


    }
}




/*
 * This class implements converting the input files to two different DNA sequences
 * 
 * Coded by: Peter Looi
 */
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
        if(args.length < 1)
            throw new RuntimeException("User did not input any arguments");
    }

    /*
     *
     * Returns the first DNA sequence in the file, in the form of a byte array where 0=A, 1=T, 2=C, 3=G
     */
    public ByteArray getSequence1()
    {
        return getSequenceI(args[0], 1);
    }

    /*
     *
     * Returns the second DNA sequence in the file, in the form of a byte array where 0=A, 1=T, 2=C, 3=G
     */
    public ByteArray getSequence2()
    {
        return getSequenceI(args[0], 2);
    }

    /*
     * String inputFile: The input file which contains the sequences
     * int se: The sequence number of the sequence you wish to get (either 1 or 2)
     *
     * Returns the DNA sequence in a byte array, where 0=A, 1=T, 2=C, 3=G
     * 
     * Coded by: Peter Looi
     */
    public ByteArray getSequenceI(String inputFile, int se)
    {
        //load in the input file
        ArrayList<String> file = loadFile(inputFile);


        String base=null;
        ArrayList<Integer> indexes=null;


        int j = 0;

        /*
         * this loop will load all the sequences in the file starting 
         * from the first sequence until we get
         * to the sequence specified by the 'se' parameter, and then
         * it will break the loop and then procede with the information
         * 
         * This loop works properly, but is not efficient because
         * it loads all sequences but only returns the one the caller
         * asked for
         * 
         * The goal of this loop is to assign 'base' and 'indexes' to the
         * appropriate values given the information specified in the 
         * input file
         */
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


        //start with the base pattern
        String output = base;
        output = output.toUpperCase();

        /*
         * for each index specified in the file, copy the current string and 
         * insert it into itself at the index
         */
        for(int index : indexes)
        {
            output = output.substring(0, index+1) + output + output.substring(index+1);
        }


        //convert the string into a ByteArray
        ByteArray outputBytes = new ByteArray(output.length());
        for(int i = 0; i < outputBytes.length; i++)
        {
            switch(output.charAt(i))
            {
                case 'A':
                    outputBytes.set(i, (byte)0); break;
                case 'T':
                    outputBytes.set(i, (byte)1); break;
                case 'C':
                    outputBytes.set(i, (byte)2); break;
                case 'G':
                    outputBytes.set(i, (byte)3); break;
            }
        }

        //return the ByteArray
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
            r.close();
        }
        catch(FileNotFoundException e)
        {
            throw new RuntimeException(e.toString());
        }
        return ret;

    }
}


/*
 * This class implements the dynamic programming string matching
 * algorithm while using only O(n) memory
 * 
 * Note that it will maintain an array of size y.length+1,
 * which means that if you get the last column, it will
 * contain the costs for matching all of x with 
 * y[0:0], y[0:1], y[0:2]...y[0:y.length]
 * 
 * 
 * If you want to run the dynamic programming algorithm to match
 * two ByteArrays x and y, you just say
 * int[] lastColumnPenalties = new MinimumPenaltyLessMemory(x, y).getLastColPenalties();
 * 
 * Note that getLastColPenalties() returns an array of size y.length+1,
 * which means it will contain the costs for 
 *     matching all of x with none of y
 *     matchine all of x with the first character of y
 *     matching all of x with the first two characters of y
 *     ...
 *     all the way through
 *     ...
 *     matching all of x with all of y
 * 
 * Coded by: Peter Looi
 * 
 */
class MinimumPenaltyLessMemory extends MinimumPenalty
{
    int[] penaltiesLastRow;

    /*
     * Constructor takes as input the two ByteArrays x and y which
     * we want to run the dynamic programming algorithm on
     */
    public MinimumPenaltyLessMemory(ByteArray x, ByteArray y)
    {
        super();
        sequence1 = x;
        sequence2 = y;
    }
    /*
     * Calculates and returns the cost of the optimal matching
     * between x and y
     */
    @Override
    public int getMinimumPenalty()
    {
        getLastColPenalties();
        return penaltiesLastRow[penaltiesLastRow.length-1];
    }



    
    /*
     * Calculates and returns the costs for 
     *     matching all of x with none of y
     *     matchine all of x with the first character of y
     *     matching all of x with the first two characters of y
     *     ...
     *     all the way through
     *     ...
     *     matching all of x with all of y
     */
    public int[] getLastColPenalties()
    {
        //initialize the two columns that we will use for the dynamic programming solution
        int[] prevPenalties = new int[sequence2.length+1];
        int[] penalties = new int[sequence2.length+1];

        //Right now, prevPenalties[j] should the cost of matching none of x with the first j characters of y
        //which is just j*pGap
        for(int j = 0; j < prevPenalties.length; j++)
            prevPenalties[j] = j * pGap;

        
        
        //fill out the M matrix column by column, dropping unneeded columns as we go
        for(int i = 0; i < sequence1.length; i++)
        {
            /*
             * At each iteration i of the loop,
             * penalties[j] is the cost of matching x0, x1... xi with y0, y1... yj-1
             * and
             * prevPenalties[j] is the cost of matching x0, x1... xi-1 with y0, y1... yj-1
             */


            //matching x0, x1... xi with none of y is just i gaps, or you can just add a gap to the last one
            penalties[0] = prevPenalties[0] + 30;

            //apply the opt equation to each of the values in penalties
            for(int j = 1; j < sequence2.length+1; j++)
            {
                penalties[j] = Math.min(Math.min(
                    penalties[j-1] + pGap, 
                    prevPenalties[j] + pGap),
                    prevPenalties[j-1] + getMismatchPenalty(sequence1.get(i), sequence2.get(j-1))
                    );
            }

            //shift over to the next column and drop the previous unneeded column
            for(int j = 0; j < penalties.length; j++)
            {
                prevPenalties[j] = penalties[j];
                penalties[j] = 0;
            }
             
            
        }
        
                
        penaltiesLastRow = prevPenalties;
        //return the last column
        return penaltiesLastRow;

    }
}


/**
 * MinimumPenalty class computes the minimum penalty of an alignment between sequence X and Sequence Y.
 */
class MinimumPenalty
{
    public static final byte A = (byte)0;
    public static final byte T = (byte)1;
    public static final byte C = (byte)2;
    public static final byte G = (byte)3;
    public static final byte GAP = (byte)4;
   
    public final static int pGap = 30;
    protected int[][] tableM;
    protected ByteArray sequence1;
    protected ByteArray sequence2;
   
   
    /*
     * Construct MinimumPenalty object with no fields initialized.
     */
    public MinimumPenalty(){}
   
   
    /**
     * Construct MinimumPenalty object using String x and String y.
     */
    public MinimumPenalty(ByteArray x, ByteArray y)
    {
        sequence1 = x;
        sequence2 = y;
        int tableSize = sequence1.length + sequence2.length + 1;
        tableM = new int [tableSize][tableSize];
        
        for (int i = 0; i <= tableSize - 1; i++){
         tableM[i][0] = i * pGap;
         tableM[0][i] = i * pGap;
      }
    }
   
    /**
     * Get mismatch penalty between char x and char y.
     */
    public static int getMismatchPenalty(byte x, byte y)
    {
        int mismatch = 0;

        if (x == y){
            mismatch = 0;
        }
        else if(x == GAP || y == GAP)
        {
            mismatch = 30;
        }
        else if ((x == A && y == C) || (x == C && y == A)){
            mismatch = 110;
        }
        else if ((x == A && y == G) || (x == G && y == A)){
            mismatch = 48;
        }
        else if ((x == A && y == T) || (x == T && y == A)){
            mismatch = 94;
        }
        else if ((x == C && y == G) || (x == G && y == C)){
            mismatch = 118;
        }
        else if ((x == C && y == T) || (x == T && y == C)){
            mismatch = 48;
        }
        else if ((x == G && y == T) || (x == T && y == G)){
            mismatch = 110;
        }
        return mismatch;
    }

    /**
     * Compute and return the minimum penalty using MinimumPenalty object.
     */
    public int getMinimumPenalty(){
        for (int i = 1; i <= sequence1.length; i++){
            for (int j = 1; j <= sequence2.length; j++){
                tableM[i][j] = Math.min(Math.min(tableM[i - 1][j - 1] + getMismatchPenalty(sequence1.get(i - 1), sequence2.get(j -1)),
                        tableM[i - 1][j] + pGap),
                  tableM[i][j-1] + pGap);
            }
        }
        return tableM[sequence1.length][sequence2.length];
    }
   
    
}


/*
 * Stores an array of bytes and allows for taking subsections of the byte array without copying data
 * 
 * Public Methods:
 * 
 * public ByteArray(int length) 
 * Constructor that creates a byte array of a specific length 
 * 
 * public ByteArray slice(int start, int end) 
 * create a sub array from start index to end index without copying data
 * 
 * public ByteArray reverse()
 * create a representation of this array with all the elements of this array reversed
 * 
 * public byte get(int index) 
 * get the value at a specific index
 * 
 * public byte set(int index, byte value)
 * set the value at a specific index to the value specified
 * 
 * final int length
 * Gets the length of the byte array. You cannot modify this value
 * 
 * Coded by: Peter Looi
 */
class ByteArray
{
    public static ByteArray New(byte... b)
    {
        return new ByteArray(b, 0, b.length);
    }
    private byte[] buffer;
    private int start;
    public final int length;
    private boolean isReversed = false;

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
    int reverseIndex(int idx)
    {
        return this.length-1 - idx;
    }
    public ByteArray slice(int start, int end)
    {
        if(end < 0) throw new RuntimeException("End index is negative " + end);
        if(end > this.length) throw new RuntimeException("End index " + end + " is larger than the length of the array " + this.length);
        if(start < 0) throw new RuntimeException("Start index is negative " + start);
        if(start > this.length) throw new RuntimeException("Start index " + start + " is larger than the length of the array " + this.length);
        if(isReversed)
        {
            int newEnd = this.length - start;
            int newStart = this.length - end;
            ByteArray ret = new ByteArray(buffer, this.start+newStart, this.start+newEnd);
            ret.isReversed = this.isReversed;
            return ret;
        }
        else
            return new ByteArray(buffer, this.start+start, this.start+end);
    }
    public byte get(int index)
    {
        if(index < 0) throw new RuntimeException("Cannot access negative index " + index);
        if(index > this.length) throw new RuntimeException("Index " + index + " is larger than the length of the array " + this.length);
        if(isReversed)
            index = this.length - index - 1; 
        return buffer[index + this.start];
    }
    public void set(int index, byte value)
    {
        if(index < 0) throw new RuntimeException("Cannot access negative index " + index);
        if(index > this.length) throw new RuntimeException("Index " + index + " is larger than the length of the array " + this.length);
        buffer[index + this.start] = value;
    }
    public String toStringNormal()
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
    public ByteArray reverse()
    {
        ByteArray ret = new ByteArray(this.buffer, this.start, this.start + this.length);
        ret.isReversed = !this.isReversed;
        return ret;

    }
    public String toString()
    {
        String ret = "";
        for(int i = 0; i < this.length; i++)
        {
            switch(get(i))
            {
                case 0:
                    ret += 'A';break;
                case 1:
                    ret += 'T';break;
                case 2:
                    ret += 'C';break;
                case 3:
                    ret += 'G';break;
                case 4:
                    ret += '_';break;
            }
        }
        return ret;

    }
    public byte[] getBuffer()
    {
        return this.buffer;
    }

}

/*
 * Simple class to represent two values together
 * 
 * Coded by: Peter Looi
 */
class Tuple<A,B>
{
    public A a;
    public B b;
    public Tuple(A a, B b)
    {
        this.a = a;
        this.b = b;
    }
}

/*
 * Class representing an alignment of two DNA strings
 * and allowing for O(1) concatenation
 * 
 * note that 
 * 0 represents Adenine
 * 1 represents Thymine
 * 2 represents Cytosene
 * 3 represents Guanine
 * 4 represents a blank
 * 
 * 
 * Public methods:
 * 
 * constructor(byte[] x, byte[] y)
 * creates an alignment between the two DNA sequences x and y
 * 
 * public void append(Alignment other)
 * modifies this alignment by appending the other alignment to this alignment
 * 
 * public String toString()
 * get string representation of this alignment (followed by all appended alignments)
 * 
 * public int calcCost()
 * calcuates the cost of this alignment (followed by all appended alignments)
 * 
 * 
 * Coded by: Peter Looi
 */
class Alignment
{
    public byte[] x;
    public byte[] y;
    public Alignment next;
    public Alignment(byte[] x, byte[] y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void append(Alignment a)
    {
        Alignment x = this;
        while(x.next != null)
        {
            x = x.next;
        }
        x.next = a;
    } 
    
    public String getXString()
    {
        String ret = "";
        Alignment a = this;
        while(a != null)
        {
            for(int i = 0; i < a.x.length; i++)
            {
                switch(a.x[i])
                {
                    case 0:
                        ret += 'A';break;
                    case 1:
                        ret += 'T';break;
                    case 2:
                        ret += 'C';break;
                    case 3:
                        ret += 'G';break;
                    case 4:
                        ret += '_';break;
                }
            }
            a = a.next;
        }
        return ret;
    }
    public String getYString()
    {
        String ret = "";
        Alignment a = this;
        while(a != null)
        {
            for(int i = 0; i < a.y.length; i++)
            {
                switch(a.y[i])
                {
                    case 0:
                        ret += 'A';break;
                    case 1:
                        ret += 'T';break;
                    case 2:
                        ret += 'C';break;
                    case 3:
                        ret += 'G';break;
                    case 4:
                        ret += '_';break;
                }
            }
            a = a.next;
        }
        return ret;
    }
    public String toString()
    {
        return getXString()+"\n"+getYString();
    }
    public int calcCost()
    {
        int cost = 0;
        String x = getXString();
        String y = getYString();
        HashMap<Character, Byte> index = new HashMap<>();
        index.put('A',(byte)0);
        index.put('T',(byte)1);
        index.put('C',(byte)2);
        index.put('G',(byte)3);
        index.put('_',(byte)4);
        for(int i = 0; i < x.length(); i ++)
        {
            char xChar = x.charAt(i);
            char yChar = y.charAt(i);
            byte xByte = index.get(xChar);
            byte yByte = index.get(yChar);
            //System.out.println(xChar+":"+yChar+" Cost="+(MinimumPenalty.getMismatchPenalty(xByte, yByte)));

            cost += MinimumPenalty.getMismatchPenalty(xByte, yByte);
        }
        return cost;
    }
}

