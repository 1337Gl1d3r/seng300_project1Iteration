package seng_project_iteration_1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException
import java.io.IOException

class mainTest {

//tests usage print msg
	@Test
	void testUsageValid() {
		String BASEDIR = 
		String type = 
		String actual = Scanner.usage(filePath, type);
		String expected = 
		
		assertEquals(expected, actual);
	}
	
//tests usage print msg
	@Test(expected = FileNotFoundException.class)
	void testUsageInvalid() {
		String BASEDIR = 
		String type = 
		String actual = Scanner.usage(filePath, type);

		assertEquals(expected, actual);
	}

//convertToString
	@Test
	void testconvertToStringValid() {
		///////////
	}

//convertToString
	@Test
	void testconvertToStringInvalid() {
		///////////
	}

//convertToString
	@Test
	void testconvertToStringEmptyFile() {
		///////////
	}

//Main
	@Test
	void testMainValidFileArray() {
		/////////////
	}

//Main
	@Test
	void testMainInvalidFileArray() {
		/////////////
	}

//Main
	@Test
	void testMainjFilesArray() {
		/////////////
	}

//Main
	@Test
	void testMainjFilesArrayEmpty() {
		/////////////
	}

//Main
	@Test
	void testMainCounterDeclarations() {
		/////////////
	}

//Main
	@Test
	void testMainCounterReferences() {
		/////////////
	}

}
