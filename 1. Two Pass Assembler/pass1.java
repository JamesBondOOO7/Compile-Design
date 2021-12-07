import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.util.*;

public class pass1 {
	public static void main(String[] args) {
		
		BufferedReader br = null;
		FileReader fr = null;

		FileWriter fw = null;
		BufferedWriter bw = null;

		try {

			// Input file which contains the Assembly language code
			String inputfilename = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/Input.txt";
			fr = new FileReader(inputfilename);
			br = new BufferedReader(fr);

			// Output file which contains the Intermediate code
			String OUTPUTFILENAME = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/IC.txt";
			fw = new FileWriter(OUTPUTFILENAME);
			bw = new BufferedWriter(fw);

			// Imperative Statements Hashtable
			Hashtable<String, String> is = new Hashtable<String, String>();
			is.put("STOP", "00");
			is.put("ADD", "01");
			is.put("SUB", "02");
			is.put("MULT", "03");
			is.put("MOVER", "04");
			is.put("MOVEM", "05");
			is.put("COMP", "06");
			is.put("BC", "07");
			is.put("DIV", "08");
			is.put("READ", "09");
			is.put("PRINT", "10");

			// Declarative Statement Hashtable
			Hashtable<String, String> dl = new Hashtable<String, String>();
			dl.put("DC", "01");
			dl.put("DS", "02");

			// Assembler Directives Hashtable
			Hashtable<String, String> ad = new Hashtable<String, String>();
			ad.put("START", "01");
			ad.put("END", "02");
			ad.put("ORIGIN", "03");
			ad.put("EQU", "04");
			ad.put("LTORG", "05");

			// Symbol Table Hashtable
			Hashtable<String, String> symtab = new Hashtable<String, String>();

			// Literal Table Hashtable
			Hashtable<String, String> littab = new Hashtable<String, String>();

			// Pooltable ArrayList, to store the indices
			ArrayList<Integer> pooltab=new ArrayList<Integer>();

			String sCurrentLine;	// Current Line
			int locptr = 0;			// Location Pointer
			int litptr = 1;			// Literal Pointer
			int symptr = 1;			// Symbol Table Pointer
			int pooltabptr = 1;		// Pool Table Pointer

			sCurrentLine = br.readLine();	// Reading the input file

			String s1 = sCurrentLine.split(" ")[1];				// Split the current line, and take the first word
			if (s1.equals("START")) {							// if s1 is `START`
				bw.write("AD \t 01 \t");						// write Intermediate Code in output file || AD : Assembler Directive || 01 : opcode
				String s2 = sCurrentLine.split(" ")[2];			// Split the current line, and take the second word
				bw.write("C \t" + s2 + "\n");					// As it is `START`, we will get the initial address
				locptr = Integer.parseInt(s2);					// location pointer = start address
			}

			// Start Reading the rest of the file
			while ((sCurrentLine = br.readLine()) != null) {
				int mind_the_LC = 0;		// Used to modify the location pointer
				String type = null;			// Used to specify the type of current statement

				int flag2 = 0;		// checks whether address is assigned to current symbol
				
				String s = sCurrentLine.split(" |\\,")[0];	// consider the first word in the line

				if (symtab.containsKey(s)){					// allocating addr to arrived symbols
					symtab.put(s, locptr + "");				// updating the symbol table if it is present
					flag2 = 1;
				}

				if (s.length() != 0 && flag2 == 0) {		// if current string is not " " or addr is not assigned,
															// then the current string must be a new symbol.			
					symtab.put(s, String.valueOf(locptr));	// Put it in the symbol table
					symptr++;								// Increment the symbol table ptr
				}

				int isOpcode = 0;							// checks whether current word is an opcode or not
				
				s = sCurrentLine.split(" |\\,")[1];			// consider the second word in the line

				if(is.containsKey(s))						// if it is found in the Imperative Statement Hashtable
				{
					bw.write("IS\t" + is.get(s) + "\t");	// if match found in imperative stmt,
					type = "is";
					isOpcode = 1;
				}

				if(ad.containsKey(s))						// Similarly, if it isn't Imperative Statement, then check next
				{
					bw.write("AD\t" + ad.get(s) + "\t");	// if match found in Assembler Directive
					type = "ad";
					isOpcode = 1;
				}

				if(dl.containsKey(s))
				{
					bw.write("DL\t" + dl.get(s) + "\t");		// if match found in declarative stmt
					type = "dl";
					isOpcode = 1;
				}
				
				
				// If current word is `LTORG` --> Assign address to the literals in the current pool
				if (s.equals("LTORG")) {
					pooltab.add(pooltabptr);
					// runs for all literals that don't have any of the addresses
					for (Map.Entry<String, String> m : littab.entrySet()) {
						if (m.getValue() == "") {				// if addr is not assigned to the literal
							m.setValue(locptr + "");
							locptr++;
							pooltabptr++;
							mind_the_LC = 1;					// this tells that we have already modified the location ptr
							isOpcode = 1;
						}
					}
				}
				
				
				// If current word is `END` --> Assign address to the literals in the current pool
				// Then similar as above
				if (s.equals("END")) {
					pooltab.add(pooltabptr);
					for (Map.Entry<String, String> m : littab.entrySet()) {
						if (m.getValue() == "") {
							m.setValue(locptr + "");
							locptr++;
							mind_the_LC = 1;
						}
					}
				}
				
				
				if(s.equals("EQU")){
					symtab.put("equ", String.valueOf(locptr));
				}
				
				
				if (sCurrentLine.split(" |\\,").length > 2) {		// if there are 3 words
					s = sCurrentLine.split(" |\\,")[2];				// consider the 3rd word => which is 1st operand
																	
																	// this is our first operand.
																	// it must be either a Register/Declaration/Symbol
					if (s.equals("AREG")) {
						bw.write("1\t");
						isOpcode = 1;
					} else if (s.equals("BREG")) {
						bw.write("2\t");
						isOpcode = 1;
					} else if (s.equals("CREG")) {
						bw.write("3\t");
						isOpcode = 1;
					} else if (s.equals("DREG")) {
						bw.write("4\t");
						isOpcode = 1;
					} else if (type == "dl") {			// if it is declarative (dc/ds)
						bw.write("C\t" + s + "\t");
					} else {
						symtab.put(s, "");				// forward referenced symbol (--> no address)
					}
				}
				
				
				if (sCurrentLine.split(" |\\,").length > 3) {		// if there are 4 words
					
					s = sCurrentLine.split(" |\\,")[3];			// consider 4th word.
																// this is our 2nd operand
																// Hence, it can't be a Declaration or Register
																// it is either a literal, or a symbol
					if (s.contains("=")) {
						littab.put(s, "");
						bw.write("L\t" + litptr + "\t");
						isOpcode = 1;
						litptr++;					// Increment the literal table ptr.
					} else {
						symtab.put(s, "");			// what if the current symbol is already present in SYMTAB? -- not handled yet!!

						bw.write("S\t" + symptr + "\t");		
						symptr++;
						
					}
				}

				bw.write("\n");		//done with a line.

				if (mind_the_LC == 0)	// only increment the location ptr if we haven't done it already!!
					locptr++;			// i.e if not `LTORG` and `END`
			}

			// ******************* OUTPUT FILES ***********************
			String f1 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/SYMTAB.txt";
			FileWriter fw1 = new FileWriter(f1);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			for (Map.Entry<String, String> m : symtab.entrySet()) {
				bw1.write(m.getKey() + "\t" + m.getValue()+"\n");				
				System.out.println(m.getKey() + " " + m.getValue());
			}

			String f2 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/LITTAB.txt";
			FileWriter fw2 = new FileWriter(f2);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			for (Map.Entry<String, String> m : littab.entrySet()) {
				bw2.write(m.getKey() + "\t" + m.getValue()+"\n");
				System.out.println(m.getKey() + " " + m.getValue());
			}
			
			String f3 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/POOLTAB.txt";
			FileWriter fw3 = new FileWriter(f3);
			BufferedWriter bw3 = new BufferedWriter(fw3);
			for (Integer item : pooltab) {  
				bw3.write(item+"\n");
			    System.out.println(item);
			}

			bw.close();
			bw1.close();
			bw2.close();
			bw3.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
