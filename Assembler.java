import com.sun.security.auth.NTUserPrincipal;
/*
   2-PASS ASSEMBLER
    HERE IS A IMPLEMENTATION OF A 2 PASS ASSEMBLER THAT CONVERTS A GIVEN ASSEMBLY LANGUAGE INTO OBJECT CODE. ALSO, THE PROGRAM WILL REPORT
    ANY ERROR THAT WILL BE PRESENT IN THE ASSEMBLY CODE AND EXIT.
    THIS ASSEMBLER IS DESIGNED BY AMAN AGGARWAL( 2018327) AND PEEYUSH JAIN (2018402) OF IIITD COLLEGE
 */

import java.util.*;
import java.util.Map.Entry;
import java.io.*;
// Assembler class containing all the relevant details and functions to run the assembler
public class Assembler {
    // HashMaps are used to store symbols, literals, lables,etc.
    static char c='A';
    static LinkedHashMap<String,String> symbol;
    static LinkedHashMap<String,String> label;
    static LinkedHashMap<String,String> literal;
    static LinkedHashMap<String,String> opcode;
    // constructor that will initiate the hashmaps that will store data
    Assembler()
    {
        symbol=new LinkedHashMap<>();
        label=new LinkedHashMap<>();
        literal=new LinkedHashMap<>();
        opcode=new LinkedHashMap<>();
    }
    // function to check existence of symbols
    public static void TwoCheck(String[] a,int loc)
    {
        if(a[0].compareTo("ADD")==0||a[0].compareTo("SUB")==0||a[0].compareTo("MUL")==0||a[0].compareTo("DIV")==0||a[0].compareTo("DSP")==0)
        {
            try {

                Integer.parseInt(a[1].substring(1));

            } catch (Exception e) {
                if(!symbol.containsKey(a[1]))
                {
                    System.out.println("Symbol don't exist");
                    System.exit(0);
                }
            }
        }
        else if(a[0].compareTo("SAC")==0)
        {
            try {
                symbol.get(a[1]);
            }
            catch (Exception e) {
                System.out.println("No such Symbol");
                System.exit(0);
            }
        }
        else if(a[0].compareTo("CLA")==0||a[0].compareTo("STP")==0)
        {
            System.out.println("Too many parameters");
            System.exit(0);
        }
    }
    // function to check whether parameter use is correct or not with clear and stop opcode
    public static void OneCheck(String a)
    {
        if(a.compareTo("CLA")!=0 && a.compareTo("STP")!=0) {
            System.out.println("Less Parameters");
            System.exit(0);
        }
    }
    // function to check whether a opcode is valid or not
    public static void NoOpcode(String line)
    {	String[] temp=line.split(" ");
        if(temp.length==1)
            return;
        if(line.contains(":"))
        {
            if(!opcode.containsKey(temp[1]))
            {
                System.out.println("Wrong Opcode");
                System.exit(0);
            }

        }
        else if(!opcode.containsKey(temp[0]))
        {
            System.out.println("Wrong Opcode");
            System.exit(0);
        }
    }
    // function to delete file content
    public static void error_check() throws java.io.IOException
    {
        BufferedWriter output=new BufferedWriter(new FileWriter("D:/aggar/Desktop/assembler/Output.txt"));
        output.write("");
        output.close();
    }
    // main function to run assembler
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Assembler r=new Assembler();
        BufferedReader read,MainReader;
        try {
            read= new BufferedReader(new FileReader("D:/aggar/Desktop/assembler/OPCODE.txt"));
            String line=read.readLine();
            while(line!=null) {
                String[] pair=line.split(" ");
                opcode.put(pair[1],pair[0]);
                line=read.readLine();
            }
            read.close();
        }catch(Exception e)
        {
            System.out.println("Error");
        }
        boolean stop_counter=false;
        try {
            read= new BufferedReader(new FileReader("D:/aggar/Desktop/assembler/Input.txt"));
            String line=read.readLine();
            if(line==null)
            {
                System.out.println("No text in File");
                System.exit(0);
            }
            int nooflines=0;
            while(line!=null)
            {
                nooflines++;
                line=read.readLine();
            }
            read.close();
            MainReader= new BufferedReader(new FileReader("D:/aggar/Desktop/assembler/Input.txt"));
            line=MainReader.readLine();
            int counter=0;
            //System.out.println(nooflines);
            while(line!=null)
            {
                //System.out.println(line);
                if(line.contains("//"))
                {
                    int ind=line.indexOf("//");
                    line=line.substring(0,ind);
                    if(line.equals(""))
                        line=MainReader.readLine();
                }
                else {
                    String[] temp = line.split(" ");
                    NoOpcode(line);
                    if (temp.length == 1) {
                        if (line.compareTo("STP") == 0)
                            stop_counter = true;
                        if (line.compareTo("CLA") != 0 && line.compareTo("STP") != 0) {
                            System.out.println("Not enough Parameters/Wrong Opcode");
                            System.exit(0);
                        }
                    }
                    if (line.contains(":")) {
                        int find = line.indexOf(" ");
                        if (temp.length == 3) {
                            TwoCheck(Arrays.copyOfRange(temp, 1, 3), nooflines);
                            LabelAdd(line, counter);
                            if (temp[1].compareTo("INP") == 0) {
                                Symboladd(line.substring(find + 1), nooflines);
                                nooflines++;
                            } else if (line.contains("=")) {
                                LiteralAdd(line, nooflines);
                                nooflines++;
                            }


                        } else if (temp.length == 2) {
                            if (temp[1].compareTo("STP") == 0)
                                stop_counter = true;
                            OneCheck(temp[1]);
                            LabelAdd(line, counter);
                        } else {
                            System.out.println("Label not defined");
                            System.exit(0);
                        }
                    } else if (temp.length == 2) {
                        TwoCheck(temp, nooflines);
                        if (temp[0].compareTo("INP") == 0) {
                            Symboladd(line, nooflines);
                            nooflines++;
                        } else if (line.contains("=")) {
                            LiteralAdd(line, nooflines);
                            nooflines++;
                        }
                    }
                    counter++;
                    line = MainReader.readLine();
                }
            }
            System.out.println("\n\nSymbol");
            BufferedWriter symbol_file=new BufferedWriter(new FileWriter("D:/aggar/Desktop/assembler/Symbol.txt"));
            for(Entry<String, String> temp:symbol.entrySet())
            {
                symbol_file.write(temp.getKey()+"   "+temp.getValue()+"\n");
                System.out.println(temp.getKey()+"   "+temp.getValue());
            }
            symbol_file.close();
            BufferedWriter label_file=new BufferedWriter(new FileWriter("D:/aggar/Desktop/assembler/label.txt"));
            System.out.println("\n\nLabels");
            for(Entry<String, String> temp:label.entrySet())
            {
                label_file.write(temp.getKey()+"   "+temp.getValue()+"\n");
                System.out.println(temp.getKey()+"   "+temp.getValue());
            }
            label_file.close();
            System.out.println("\n\nLiteral");
            BufferedWriter literal_file=new BufferedWriter(new FileWriter("D:/aggar/Desktop/assembler/literal.txt"));
            for(Entry<String, String> temp:literal.entrySet())
            {
                literal_file.write(temp.getKey()+"   "+temp.getValue()+"\n");
                System.out.println(temp.getKey()+"   "+temp.getValue());
            }
            literal_file.close();
            if(stop_counter==false)
                System.out.println("No STOP statement present.");
            MainReader.close();
            /// 2nd PASS STARTS FROM HERE/////
            MainReader = new BufferedReader(new FileReader("D:/aggar/Desktop/assembler/Input.txt"));
            BufferedWriter output=new BufferedWriter(new FileWriter("D:/aggar/Desktop/assembler/Output.txt"));
            line = MainReader.readLine();
            counter = 0;
            boolean error=false;
            while (line != null) {
                if(line.contains("//"))
                {
                    int ind=line.indexOf("//");
                    line=line.substring(0,ind);
                    if(line.equals(""))
                        line=MainReader.readLine();
                }
                else {
                    String[] temp = line.split(" ");
                    //System.out.print(temp.length);
                    if (temp.length == 1) {
                        String str = opcode.get(temp[0]);
                        str = str.concat("00000000");
                        output.write(str + "\n");
                    } else {
                        int i = 2;
                        if (line.contains(":"))
                            i = 3;
                        String str = opcode.get(temp[i - 2]);
                        String sorl = "";
                        String address = "";
                        if (str.equals("0000") || str.equals("1100")) {

                        } else if (str.equals("0101") || str.equals("0110") || str.equals("0111")) {
                            sorl = temp[i - 1];
                            try {
                                address = address.concat(label.get(sorl));
                            } catch (NullPointerException e) {
                                System.out.println("Label not found");
                                error = true;
                            }
                        } else {
                            sorl = temp[i - 1];
                            if (sorl.contains("=")) {
                                address = address.concat(literal.get(sorl.substring(1)));
                            } else {
                                //System.out.print("execute"+sorl);
                                address = address.concat(symbol.get(sorl));
                            }
                        }
                        int len = address.length();
                        int start = 8 - len;
                        String starting = "";
                        for (i = 0; i < start; ++i)
                            starting = starting.concat("0");
                        starting = starting.concat(address);
                        str = str.concat(starting);
                        output.write(str.concat("\n"));
                    }
                    counter++;
                    line = MainReader.readLine();
                }
            }
            MainReader.close();
            output.close();
            if(error)
            {
                error_check();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.print("No file");
            System.exit(0);
        }
    }
    public	static void Symboladd(String a,int loc)
    {
        String[] m=a.split(" ");
        //System.out.println(m.length);
        //System.out.println(m[1]+"  "+Integer.toBinaryString(loc));
        symbol.put(m[1],Integer.toBinaryString(loc));
        c++;
    }
    public static void LabelAdd(String a,int loc)
    {
        String location=Integer.toBinaryString(loc);
        int place=a.indexOf(":");
        label.put(a.substring(0,place), location);
    }
    public static void LiteralAdd(String a,int loc)
    {
        String location=Integer.toBinaryString(loc);
        int place=a.indexOf("=");
        literal.put(a.substring(place+1), location);
    }

}
