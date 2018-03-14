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
import org.eclipse.jdt.core.IField;
import java.util.HashSet;
import java.util.Set;
import java.lang.StringBuilder;

/** 
 *	Main driver class for the scanning functionality 
 */
public class Scanner {
	private static boolean debugging = false;
	private static HashMap<String, Integer> countDecs = new HashMap<String, Integer>();		// create hashmap to hold int count of each declaration
	private static HashMap<String, Integer> countRefs = new HashMap<String, Integer>();		// create hashmap to hold int count of each reference
	
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
	 * Will check the string value provided and either increment the count of the existing type or 
	 * add the the hashmap the new found type and set its count to 1
	 * 
	 * @param key The hashmap key to check for
	 * @param map The flag for which hashmap to adjust for (0 -> countDec, 1 -> countRefs)
	 */
	public static void countIt(String key, int map) {
		Integer v = new Integer(1);
		if (map == 0) {
			if ((v = countDecs.get(key)) != null) {		// the key exist, so increment its value 
				debug("Incrementing " + key);
				countDecs.replace(key, v+1);
			} else {									// key doesn't exist, so add it
				debug("Adding " + key);
				countDecs.put(key, new Integer(1));
			}
		} else { 
			if ((v = countRefs.get(key)) != null) {		// the key exist, so increment its value 
				debug("Incrementing " + key);
				countRefs.replace(key, v+1);
			} else {									// key doesn't exist, so add it
				debug("Adding " + key);
				countRefs.put(key, new Integer(1));
			}
		}
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
		String typeToScan = args[1];
		debug("Scan for: \'" +  typeToScan + "\'");
		for (int i = 0; i < jFiles.size(); i++) {
			File file = jFiles.get(i);
			debug("--------------------------------------------------------------------------------------");
			debug("Scanning file: " + file.getName().toString());
			debug("--------------------------------------------------------------------------------------");
			String fString = convertToString(file);		// get file as string
			ASTParser parser = ASTParser.newParser(AST.JLS8);		// set parser type to JLS8 and create
			parser.setSource(fString.toCharArray());		// parse source the file as a char array
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);		// Make is so compiler provides binding info for AST nodes created
			parser.setBindingsRecovery(true);		// Ask compiler to perform binding recovery
			parser.setEnvironment(null, null, null, true);		// Using .java files only (ie. ICompilationUnit) so need environment setup b/c no IJavaProject
			parser.setUnitName(""); 	// empty string because "file" is a char array fed to setSource method
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);		// create file compilation unit
			cu.accept(new ASTVisitor () {
				/** Keep track of the type declaration for our classes */
				public boolean visit(TypeDeclaration node) {
					String s = node.resolveBinding().getName().toString();		// get name(s) of class(es)
					countIt(s, 0);		// incorporate to hashmap
					return true;
				}
				
				/** Tracks object references in constructor statements */
				public boolean visit(FieldAccess node) {
					try {
						ITypeBinding it = node.getName().resolveTypeBinding();
						String fieldString = it.getQualifiedName();
						countIt(fieldString, 1);
					} catch (NullPointerException e) {
						// do nothing
					}
					return true;
				}
				
				/** Keep track of method references and their parameter references */
				public boolean visit(MethodDeclaration node) {
					// handle the method return value
					Type t = null;
					try {
						t = node.getReturnType2();
						String tb = t.resolveBinding().getQualifiedName(); 
						if (!tb.equals("void")) {
							debug("Method " + node.getName().toString() + " return type references " + tb);
							countIt(tb, 1);		// incorporate into hashmap
						}
					} catch (NullPointerException e) {
						// do nothing
					}
					
					// handle the method parameters 
					for (Object p : node.parameters()) {
						VariableDeclaration vDec = (VariableDeclaration) p;
						ITypeBinding iBind = vDec.resolveBinding().getType();
						String pString = iBind.getQualifiedName();
						countIt(pString, 1);
					}
					
					
					return true;
				}
			
				public boolean visit(VariableDeclarationFragment node) {
					String result = "";
					IVariableBinding b = node.resolveBinding();
					ITypeBinding t = b.getType();
					String n = t.getQualifiedName();
					SimpleName name = node.getName();		// get the simple name
					if (n.equals("enum")) {
						debug("enum found...");	
						String[] fNameOnly = file.getName().split(".java");		// take class name to format properly
						result = fNameOnly[0] + "." + name;
					} else {
						debug("found on " + cu.getLineNumber(name.getStartPosition()));
						debug("Adding: " + n);
						result = n;
					}
					countIt(result, 1);		// incorporate to hashmap
					return false;
				}
				
			});
		}
		// output answer in proper format
		Integer f = countDecs.get(typeToScan);
		Integer finalDecs = countDecs.get(typeToScan);
		Integer finalRefs = countRefs.get(typeToScan);
		if (finalDecs == null)
			finalDecs = new Integer(0);
		if (finalRefs == null)
			finalRefs = new Integer(0);
		System.out.println(typeToScan + ". Declarations found: " + finalDecs + "; References Found: " + finalRefs + ".");
	}
}
