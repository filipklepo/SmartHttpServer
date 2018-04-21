package hr.fer.zemris.java.custom.scripting.exec.tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;
import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * SSETest is a class which demonstrates the functionality of 
 * {@link SmartScriptEngine}.
 * 
 * @author Filip Klepo
 *
 */
public class SSETest {

	/**
	 * The main method. Runs when program is started.
	 * 
	 * @param args arguments from the command line, not used
	 */
	public static void main(String[] args) {
		//change this variable from 1 to 4 to test various scripts
		int test = 4;
		switch(test) {
		case 1:
			testScriptOne();
			break;
		case 2:
			testScriptTwo();
			break;
		case 3 :
			testScriptThree();
			break;
		case 4 :
			testScriptFour();
			break;
		default :
		}
	}
	
	/**
	 * Test script one.
	 */
	private static void testScriptOne() {
		String documentBody = readFromDisk("osnovni.smscr");
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<>();
		// create engine and execute it
		new SmartScriptEngine(
		new SmartScriptParser(documentBody).getDocumentNode(),
		new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
	
	/**
	 * Tests script two.
	 */
	private static void testScriptTwo() {
		String documentBody = readFromDisk("zbrajanje.smscr");
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		parameters.put("a", "4");
		parameters.put("b", "2");
		// create engine and execute it
		new SmartScriptEngine(
		new SmartScriptParser(documentBody).getDocumentNode(),
		new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
	
	/**
	 * Tests script three.
	 */
	private static void testScriptThree() {
		String documentBody = readFromDisk("brojPoziva.smscr");
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		persistentParameters.put("brojPoziva", "3");
		RequestContext rc = new RequestContext(System.out, parameters, persistentParameters,
		cookies);
		new SmartScriptEngine(
		new SmartScriptParser(documentBody).getDocumentNode(), rc
		).execute();
		System.out.println("\r\nVrijednost u mapi: "+rc.getPersistentParameter("brojPoziva"));
	}
	
	/**
	 * Tests script four.
	 */
	private static void testScriptFour() {
		String documentBody = readFromDisk("fibonacci.smscr");
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		// create engine and execute it
		new SmartScriptEngine(
		new SmartScriptParser(documentBody).getDocumentNode(),
		new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();

	}
	
	/**
	 * Reads file with given path from disk. File is read as {@link String} 
	 * object. If file can not be read, <b>null</b> is returned.
	 * 
	 * @param path path to file
	 * @return file's content, or <b>null<7b> if file can not be read
	 */
	private static String readFromDisk(String path) {
			String lines;
			try {
				lines = new String(Files.readAllBytes(Paths.get(path)), 
						StandardCharsets.UTF_8);
			} catch (InvalidPathException | IOException e) {
				return null;
			}
			
			return lines;
	}
	
}
