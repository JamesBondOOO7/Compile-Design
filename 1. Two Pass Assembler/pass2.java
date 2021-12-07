import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Pass2 {

	public static void main(String[] args) {

		try {
			
			String f = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/IC.txt";
			FileReader fw = new FileReader(f);
			BufferedReader IC_file = new BufferedReader(fw);

			String f1 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/SYMTAB.txt";
			FileReader fw1 = new FileReader(f1);
			BufferedReader symtab_file = new BufferedReader(fw1);
			symtab_file.mark(500);													// mark is used to create a buffer space

			String f2 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/LITTAB.txt";
			FileReader fw2 = new FileReader(f2);
			BufferedReader littab_file = new BufferedReader(fw2);
			littab_file.mark(500);

			String littab[][]=new String[10][2];									// literable table in our program
			
			Hashtable<String, String> symtab = new Hashtable<String, String>();		// Symbol table
			String str;
			int z=0;

			// reading the literable table file from pass1
			while ((str = littab_file.readLine()) != null) {

				littab[z][0]=str.split("\t")[0];	// literal value
				littab[z][1]=str.split("\t")[1];	// and its address
				z++;
			}

			// reading the symbol table file from pass1
			while ((str = symtab_file.readLine()) != null) {
				symtab.put(str.split("\t")[0], str.split("\t")[1]);		// key, value : symbol_name, address
			}

			// Initializing for Pool table and Machine code files
			String f3 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/POOLTAB.txt";
			FileReader fw3 = new FileReader(f3);
			BufferedReader pooltab_file = new BufferedReader(fw3);

			String f4 = "/home/james/Desktop/POCC practicals/1. Two Pass Assembler/MACHINE_CODE.txt";
			FileWriter fw4 = new FileWriter(f4);
			BufferedWriter machine_code_file = new BufferedWriter(fw4);

			// Reading the pool table
			ArrayList<Integer> pooltab = new ArrayList<Integer>();
			String t;
			while ((t = pooltab_file.readLine()) != null) {
				pooltab.add(Integer.parseInt(t));
			}
			
			// pointers for reading the pool table
			int pooltabptr = 1;				// line number
			int temp1 = pooltab.get(0);		// 0th value
			int temp2 = pooltab.get(1);		// 1st value
			
			
			String sCurrentLine;										// Current line of Intermediate Code
			sCurrentLine = IC_file.readLine();
			int locptr=0;
			locptr = Integer.parseInt(sCurrentLine.split("\t")[3]);		// 4th word, which is the start address ( in memory )
			
			while ((sCurrentLine = IC_file.readLine()) != null) {		// Reading the rest of the file
				
				machine_code_file.write(locptr + "\t");					// always write the Location ptr
				
				String s0 = sCurrentLine.split("\t")[0];				// 1st word of the current line
				String s1 = sCurrentLine.split("\t")[1];				// 2nd word of the current line
				
				if (s0.equals("IS")) {								// If it is Imperative Statement
					machine_code_file.write(s1 + "\t");				// Write the 2nd word
					if (sCurrentLine.split("\t").length == 5) {
						
						machine_code_file.write(sCurrentLine.split("\t")[2] + "\t");		// write the 3rd word (1st operand, which is a register/ DC statement/ Symbol)
						
						
						if (sCurrentLine.split("\t")[3].equals("L")) {						// If 4th word == `L`, i.e a literal
							int add = Integer.parseInt(sCurrentLine.split("\t")[4]);		// retreive the address from the literal table
							machine_code_file.write(littab[add-1][1]);						// write it in the machine code output
						
						}
						
						if (sCurrentLine.split("\t")[3].equals("S")) {						// If 4th word == `S`, i.e a Symbol
							int add1 = Integer.parseInt(sCurrentLine.split("\t")[4]);		// Retreive its index
							int i = 1;
							for (Map.Entry<String, String> m : symtab.entrySet()) {			// Write the symbols and their index in machine output file
								if (i == add1) {
									machine_code_file.write((String) m.getValue());
								}
								i++;
							}
							
						}
					} else {
						machine_code_file.write("0\t000");
					}
				}
				
				if (s0.equals("AD")) {								// If it is Assembler Directive
					littab_file.reset();
					if (s1.equals("05")) {							// if it is LTORG (opcode = 05)
						int j = 1;
						while (j < temp1) {
							littab_file.readLine();
						}
						while (temp1 < temp2) {
							machine_code_file.write("00\t0\t00" + littab_file.readLine().split("'")[1]);
							if(temp1<(temp2-1)){
								locptr++;
								machine_code_file.write("\n");
								machine_code_file.write(locptr+"\t");
							}
							temp1++;
						}
						temp1 = temp2;
						pooltabptr++;
						if (pooltabptr < pooltab.size()) {
							temp2 = pooltab.get(pooltabptr);
						}
					}

					int j = 1;
					if (s1.equals("02")) {							// if it is END (opcode = 02)
						String s;
						while ((s = littab_file.readLine()) != null) {
							if (j >= temp1)
								machine_code_file.write("00\t0\t00" + s.split("'")[1]);		// split the line to get the constant value
							j++;
						}
					}
				}
				
				if(s0.equals("DL")&&s1.equals("01")){				// if it is DL (declarative type) and in that it is DC (constant value) (opcode = 01)
					machine_code_file.write("00\t0\t00"+sCurrentLine.split("'")[1]);	
				}
				
				locptr++;		// Increment the location ptr
				machine_code_file.write("\n");
			}

			IC_file.close();
			symtab_file.close();
			littab_file.close();
			pooltab_file.close();
			machine_code_file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
