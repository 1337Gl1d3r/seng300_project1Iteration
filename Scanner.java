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
	private static boolean debugging = true;
	
	/** 
	 * Prints easily read debug statements
	 * 
	 * @param msg The debug msg to print
	 */
	public static void debug(String msg) {
		if (debugging) 
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
			sb.append('\n');
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
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();		// create hashmap to hold int count of each simple map
		String typeToScan = args[1];
		debug("Scan for: \'" +  typeToScan + "\'");
		for (int i = 0; i < jFiles.size(); i++) {
			File file = jFiles.get(i);
			String fString = convertToString(file);		// get file as string
			ASTParser parser = ASTParser.newParser(AST.JLS8);		// set parser type to JLS8 and create
			parser.setSource(fString.toCharArray());		// parse source the file as a char array
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);		// create file compilation unit
			cu.accept(new ASTVisitor () {
				Set names = new HashSet();		// create abstract hashset for names of variables
				
				public boolean visit(SimpleName node) {
					if (this.names.contains(node.getIdentifier())) {
						System.out.println("FullyQualifiedName: " + node.getFullyQualifiedName());
						Integer cCnt = countMap.get(node.getIdentifier());		// get the current value for count
						countMap.put(node.getIdentifier(), cCnt+1);		// increment current counter
					}
					return false;
				}
				
				
				public boolean visit(QualifiedName node) {
					System.out.println("QualifiedName: " + node);
					return true;
				}
				
				public boolean visit(QualifiedType node) {
					System.out.println("QualifiedType: " + node);
					return true;
				}
				
				public boolean visit(TypeDeclaration node) {
					System.out.println(node.getName().getFullyQualifiedName());
					return true;
				}
				
				public boolean visit(FieldDeclaration node) {
					System.out.println("FieldDeclaration: " + node.getType().toString());
					return true;
				}
				
				/*
				public boolean visit(SimpleType node) {
					System.out.println("SimpleType: " + node.getName().getFullyQualifiedName());
					return true;
				}
				*/
				/*
				public boolean visit(VariableDeclarationFragment node) {
					SimpleName name = node.getName();		// get the simple name
					this.names.add(name.getIdentifier());		// add to names list
					countMap.put(name.getIdentifier(), new Integer(1));		// create new hashmap entry for new variable declaration
					int lineNumber = cu.getLineNumber(name.getStartPosition());		
					debug("Name: \'" + name.toString() + "\' at line: " + lineNumber);
					return false;
				}
				*/
			});
		}
		// output answer in proper format
		Integer f = countMap.get(typeToScan);
		
	}
}
