/**
 * Functionality of this program is to count the number of declarations of a user inputed 
 * data type and to count the number of references to each occurrence of that type within 
 * the specified directory
 * 
 * @version 1.0.0
 * @author Jessica Pelley, Katie Tieu, and Nathanael Carrigan
 */

package seng_project_iteration_1;

/** Import required libraries/modules */
import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.*;
import java.lang.Class;
import java.util.HashSet;
import java.util.Set;
import java.lang.StringBuilder;

/** 
 *	Main driver class for the scanning functionality 
 */
public class Scanner {	
	/** 
	 * Prints easily read debug statements
	 * 
	 * @param msg The debug msg to print
	 */
	public static void debug(String msg) {
		System.out.println("[DEBUG]:\t " + msg);
	}
	
	/**
	 * Outputs the usage message for users 
	 */
	public static void usage() {
		System.out.println("Usage:\tjava Scanner <path to directory> <java type of interest>");
	}
	
	/**
	 * Converts the inputed file to a String to be used by the AST parser 
	 * 
	 * @param file The file to be converted
	 * @return String of the file
	 */
	public static String convertToString(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file.getPath()));		// open file to be converted
		String line = "";
		List<String> fString = new ArrayList<String>();
		while ((line = r.readLine()) != null) {
			fString.add(line);
		}
		String[] fileString = new String[fString.size()];		// create a String array equal to size of the arraylist
		fileString = fString.toArray(fileString);	// convert the arraylist to an array String[]
		StringBuilder sb = new StringBuilder();
		for (String s : fileString) {
			sb.append(s);		// add each element from array to stringbuilder sb
		}
		String result = sb.toString();		// combines all the appended strings together
		r.close();		// close BufferedReader
		
		return result;
	}
	
	/**
	 * Main driver for the project1 iteration functionality
	 * 
	 * @param args The arguments passed to the program
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {		// checks for proper argument count
			usage();
			System.exit(0);
		}
		
		File dir = new File(args[0]);		// create file object for directory
		File[] filesArr = dir.listFiles();		// gets list of files in directory
		if (filesArr == null) {
			System.out.println("[Scanner]:\tInvalid directory or path");
			System.exit(0);
		}
		debug(Arrays.toString(filesArr));
		
		List<File> jFiles = new ArrayList<File>();		// creates ArrayList to add java files to get parsed from directory
		// get list of just .java files
		if (filesArr.length <= 0) {
			System.out.println("[Scanner]:\tNo java files in the specified directory.");
			System.exit(0);
		} else {
			for (int i = 0; i < filesArr.length; i++) {
				boolean isJava = (filesArr[i].toString()).contains(".java");		// check if a java file
				if (isJava) {
					jFiles.add(filesArr[i]);		// add to list if java file
				}
			}
		}
		debug("Number of java files: " +jFiles.size());
		debug(Arrays.toString(jFiles.toArray()));
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		for (int i = 0; i < jFiles.size(); i++) {
			File file = jFiles.get(i);
			String fString = convertToString(file);
			debug(fString);
			
			parser.setSource(fString.toCharArray());	
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			//****************************************************************
			cu.accept(new ASTVisitor() {
				 
				Set names = new HashSet();
	 
				public boolean visit(VariableDeclarationFragment node) {
					SimpleName name = node.getName();
					this.names.add(name.getIdentifier());
					System.out.println("Declaration of '" + name + "' at line"
							+ cu.getLineNumber(name.getStartPosition()));
					return false; // do not continue 
				}
	 
				public boolean visit(SimpleName node) {
					if (this.names.contains(node.getIdentifier())) {
						System.out.println("Usage of '" + node + "' at line "
								+ cu.getLineNumber(node.getStartPosition()));
					}
					return true;
				}
			});
			//****************************************************************
		}
	}
}
