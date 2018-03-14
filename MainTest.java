/**
 * JUnit test for Scanner.java.
 * Known bugs: tests involving System.exit() throw ExitSecurityExceptions
 * 
 * @author Jessica Pelley, Katie Tieu, and Nathanael Carrigan
 */

package seng_project_iteration_1;

import seng_project_iteration_1.ExitDeniedSecurityManager.ExitSecurityException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.Permission;

public class MainTest {
	public static String BASEDIR = "C:\\Users\\katie\\";

	/**
	 * Set a security manager to catch System.exit() calls
	 */
	@Before
	public void setup() {
		System.setSecurityManager(new ExitDeniedSecurityManager());
	}
	
	
	/**
	 * Tests that running the program with more or less than 2 arguments prints a usage string and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testInvalidUsage() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents";
		String[] args = {pathname};
		
		Scanner sc = new Scanner();
		
		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("Usage:	java Scanner <path to directory> <java type of interest>\r\n" + "",
					outContent.toString());
		}
		
		
		// TODO: Still throws ExitSecurityException?

	}
	
	
	/**
	 * Checks that when given a nonexistent directory, the program prints an error message and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testFakeDir() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = "fakepath";
		String[] args = {pathname, "java.lang.String"};
		Scanner sc = new Scanner();
		
		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("[Scanner]:\tInvalid directory or path\r\n" + "", outContent.toString());
		}
	}

	
	/**
	 * Checks that when given a directory with no java files, the program prints an error message and exits
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testDirWithNoJavaFiles() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents\\EmptyDir";
		String type = "java.lang.String";
		String[] args = {pathname, type};
		
		Scanner sc = new Scanner();

		try {
			sc.main(args);
		} catch (ExitSecurityException e) {
			assertEquals("[Scanner]:\tNo java files in the specified directory.\r\n" + "", outContent.toString());
		}
	}

	
	/**
	 * Checks that when given a non-empty file, convertToString will return a non-empty String
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testConvertNonemptyFileToString() throws IOException {
		File file = new File(BASEDIR + "Documents\\SimpleProgram.java");
		Scanner sc = new Scanner();
		assertTrue("", sc.convertToString(file).length() > 0);
	}
	
	/**
	 * Checks that when given an empty, file, convertToString will return an empty String
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testConvertEmptyFileToString() throws IOException {
		File file = new File(BASEDIR + "Documents\\EmptyFile.java");
		Scanner sc = new Scanner();
		assertEquals(sc.convertToString(file).length(), 0);
	}


	/**
	 * Checks that when given a directory with more than one java file, declarations to a type
	 * are counted across all files
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testDecCountWithMultipleJavaFiles() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents\\DirWMultipleJavaFiles";
		String[] args = {pathname, "java.lang.String"};
		Scanner sc = new Scanner();
		
		sc.main(args);
		
		String s = outContent.toString();		// main should print "<type>. Declarations found: <count>; references found: <count>"
		String[] parts = s.split(" ");			// we want only the number of declarations
		parts[3] = parts[3].substring(0, parts[3].length()-1);			// Remove semi-colon
		int numberOfDeclarations = Integer.parseInt(parts[3]);
		
		assertEquals(numberOfDeclarations, 1);
	}

	
	/**
	 * Checks that when given a directory with more than one java file, declarations of a type
	 * are counted across all files
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testRefCountWithMultipleJavaFiles() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		
		String pathname = BASEDIR + "Documents\\DirWMultipleJavaFiles";
		String[] args = {pathname, "java.lang.String"};
		Scanner sc = new Scanner();
		
		sc.main(args);
		
		String s = outContent.toString();		// main should print "<type>. Declarations found: <count>; references found: <count>"
		String[] parts = s.split(" ");			// we want only the number of references
		int numberOfReferences = Integer.parseInt(parts[6]);
		
		assertEquals(numberOfReferences, 1);
	}


	/**
	 * Test that an ASTParser can be created for JLS8
	 */
	@Test
	public void testCreateParserForJLS8() {
		assertNotNull(ASTParser.newParser(AST.JLS8));
	}
	
	/**
	 * Checks that setting kind to K_COMPILATION_UNIT results in the creation of a CompilationUnit node
	 */
	@Test
	public void testSetKindCompilationUnit() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(new char[0]);
		assertTrue(parser.createAST(null) instanceof CompilationUnit);
	}
	
	/**
	 * Checks that we can successfully create an AST with a char array source
	 */
	@Test
	public void testSetSourceWithCharArray() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		String source = "abcdefg";
		parser.setSource(source.toCharArray());
		assertNotNull(parser.createAST(null));
	}

}



/**
 * Security manager to catch System.exit() calls
 */
class ExitDeniedSecurityManager extends SecurityManager {
	 
    @SuppressWarnings("serial")
	public static final class ExitSecurityException extends SecurityException {
        private final int status;
 
        public ExitSecurityException(final int status) {
            this.status = status;
        }
 
        public int getStatus() {
            return this.status;
        }
    }
 
    @Override
    public void checkExit(final int status) {
        throw new ExitSecurityException(status);
    }
 
    @Override
    public void checkPermission(final Permission perm) {}
}
