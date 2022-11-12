import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Basic2 {
   public static void main (String[] args)
   {
      InputExtract input = new InputExtract(args);
      String sequence1 = inputGenerator1(input);
      String sequence2 = inputGenerator2(input);
      System.out.println(sequence1);
      System.out.println(sequence2);
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