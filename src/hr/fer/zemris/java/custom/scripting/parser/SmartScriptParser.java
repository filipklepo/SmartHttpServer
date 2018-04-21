package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptToken;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptTokenType;
import hr.fer.zemris.java.custom.scripting.nodes.*;

/**
 * SmartScriptParser is program which takes input text and creates document model matching
 * that input text. Besides doing that, it's task, in this form, is to check whether the generated tokens, 
 * generated by {@link SmartScriptLexer}, form a meaningful expression.
 * 
 * @author Filip Klepo
 *
 */
public class SmartScriptParser {

	/**
	 * Stack used for construction of document model.
	 */
	private ObjectStack stack;
	/**
	 * Document model representation of input text.
	 */
	private DocumentNode documentNode;
	
	/**
	 * Constructs instance of this class with given text. Resulting instance will have a built document
	 * model in DocumentNode.
	 * 
	 * @param text text from which document model is built
	 */
	public SmartScriptParser(String text) {
		SmartScriptLexer lexer = new SmartScriptLexer(text);
		
		stack = new ObjectStack();
		documentNode = new DocumentNode();
		
		stack.push(documentNode);
		
		try {
			parse(lexer);
		} catch (Exception e) {
			throw new SmartScriptParserException(e.getMessage());
		}
	}
	
	/**
	 * Generates TextNode from given text.
	 * 
	 * @param text text from which a TextNode will be generated
	 */
	private void generateTextNode(String text) {
		Node parent = (Node) stack.peek();
		parent.addChildNode(new TextNode(text));
	}
	
	/**
	 * Generates ForLoopNode from given components.
	 * 
	 * @param chunks parts of text, each of which represents one element of the for-loop
	 */
	private void generateForLoopNode(String[] chunks) {
		if(chunks.length != 4 && chunks.length != 5) {
			throw new IllegalArgumentException("For loop node contains " + (chunks.length - 1) +
					" parameters instead of 3 or 4!");
		}
		
		if(!isVariable(chunks[1])) {
			throw new IllegalArgumentException("First element of for loop must be a variable!");
		}
		
		for(int i = 2; i < chunks.length; ++i) {
			if(!isString(chunks[i]) && !isVariable(chunks[i]) && !(isInteger(chunks[i]) 
					|| isDouble(chunks[i]))) {
				throw new IllegalArgumentException("For loop node contains wrong tipe of parameters!");
			}
		}
		
		ElementVariable variable = (ElementVariable) generateElement(chunks[1]);
		Element startExpression = generateElement(chunks[2]);
		Element endExpression = generateElement(chunks[3]);
		Element stepExpression = null;
		
		if(chunks.length == 5) {
			stepExpression = generateElement(chunks[4]);
		}
		
		ForLoopNode forLoopNode = new ForLoopNode(variable, startExpression, endExpression, stepExpression);
		
		evaluateForLoopNode(forLoopNode);
	}
	
	/**
	 * Places ForLoopNode at appropriate position in DocumentNode.
	 * 
	 * @param forLoopNode newly created ForLoopNode
	 */
	private void evaluateForLoopNode(ForLoopNode forLoopNode) {
		Node parent = (Node) stack.peek();
		parent.addChildNode(forLoopNode);
		stack.push(forLoopNode);
	}
	
	/**
	 * Generates EchoNode from given components.
	 * 
	 * @param chunks parts of text, each of which represents element
	 */
	private void generateEchoNode(String[] chunks) {
		ArrayIndexedCollection col = new ArrayIndexedCollection();
		
		if(chunks.length == 1) {
			col.add(generateElement(chunks[0].substring(1)));
			evaluateEchoNode(col);
			return;
		}
		
		for(int i = 1; i < chunks.length; ++i) {
			col.add(generateElement(chunks[i]));
		}
		
		evaluateEchoNode(col);
	}
	
	/**
	 * Places EchoNode at appropriate place in DocumentNode. 
	 * 
	 * @param col collection which holds EchoNode Element-s
	 */
	private void evaluateEchoNode(ArrayIndexedCollection col) {
		Node parent = (Node) stack.peek();
		parent.addChildNode(new EchoNode(col.toArray()));
	}
	
	/**
	 * Evaluates END-tag.
	 */
	private void evaluateEndTag() {
		stack.pop();
		
		if(stack.size() < 1) {
			throw new RuntimeException("Document text contains more END-tags than forloop-tags!");
		}
	}
	
	/**
	 * Helping method which resolves creation of single tag.
	 * 
	 * @param tagContent textual content of tag
	 */
	private void resolveTag(String tagContent) {
		String[] chunks = getTagChunks(tagContent); //rightttt
		
		if(chunks[0].toLowerCase().equals("for")) {
			generateForLoopNode(chunks);
		} else if(chunks[0].equals("=") || (chunks[0].startsWith("=") && chunks.length == 1)) {
			generateEchoNode(chunks);
		} else if(chunks[0].toLowerCase().equals("end") && chunks.length == 1) {
			evaluateEndTag();
		} else {
			throw new IllegalArgumentException("Unknown tag name!");
		}
		
	}
	
	/**
	 * Gets elements stored in tag in form of {@link String} array.
	 * 
	 * @param tagContent tag content in form of {@link String}
	 * @return {@link String} array of elements
	 */
	private String[] getTagChunks(String tagContent) {
		ArrayIndexedCollection chunks = new ArrayIndexedCollection();
		boolean bufferIsString = false;
		StringBuilder buffer = new StringBuilder();
		
		while(tagContent.length() > 0) {
			char curChar = tagContent.charAt(0);
			
			if(Character.isWhitespace(curChar)) {
				if(!bufferIsString) {
					if(buffer.length() > 0) {
						chunks.add(buffer.toString());
						buffer.setLength(0);
						bufferIsString = false;
					}
				} else {
					buffer.append(curChar);
				}
			} else {
				buffer.append(curChar);
				if(curChar == '"') {
					if(bufferIsString) {
						chunks.add(buffer.toString());
						buffer.setLength(0);
						bufferIsString = false;
					} else if (buffer.length() == 1){
						bufferIsString = true;
					}
				}
			}

			tagContent = tagContent.length() == 1 ? "" : tagContent.substring(1);
		}
		if(buffer.length() > 0) {
			chunks.add(buffer.toString());
		}
		
		String[] strArray = new String[chunks.size()];
		for(int i = 0; i < strArray.length; ++i) {
			strArray[i] = (String)chunks.get(i);
		}
		
		return strArray;
	}
	
	/**
	 * Core method of this class. Its task is to produce Element instances from given SmartScriptLexer's 
	 * tokens and to create document model in DocumentNode.
	 * 
	 * @param lexer SmartScriptLexer from which tokens are read
	 */
	private void parse(SmartScriptLexer lexer) {
		SmartScriptToken token;
		
		while((token = lexer.nextToxen()).getType() != SmartScriptTokenType.EOF) {
			switch(token.getType()) {
				case TEXT:
					generateTextNode(token.getValue());
					break;
				case TAG:
					resolveTag(token.getValue());
					break;
				case EOF:
					break;
			}
		}
		
		if(stack.size() != 1) {
			throw new RuntimeException("There is no closing END-tag for every for loop tag!");
		}
	}
	
	/**
	 * Helping method which generates single Element from given input text.
	 * 
	 * @param input text from which Element is created
	 * @throws IllegalArgumentException if text is not valid Element representation
	 * @return Element whose value is input text
	 */
	private Element generateElement(String input) {
		if(isInteger(input)) {
			return new ElementConstantInteger(Integer.parseInt(input));
		} else if(isDouble(input)) {
			return new ElementConstantDouble(Integer.parseInt(input));
		} else if(isVariable(input)) {
			return new ElementVariable(input);
		} else if(isFunction(input)) {
			return new ElementFunction(input);
		} else if(isString(input)) {
			return new ElementString(input.replace("\\r\\n", System.lineSeparator()));
		} else if(isOperator(input)) {
			return new ElementOperator(input);
		} else {
			throw new IllegalArgumentException("Element " + input + " is not valid!");
		}
	}
	
	/**
	 * Checks if input text is valid representation of integer number.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid integer
	 */
	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if input text is valid representation of double number.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid double
	 */
	private boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if input text is valid representation of variable name.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid variable name
	 */
	private boolean isVariable(String input) {
		if(!Character.isLetter(input.charAt(0))) {
			return false;
		}
		
		for(char ch : input.toCharArray()) {
			if(!Character.isDigit(ch) && !Character.isLetter(ch) && !(ch == '_')) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if input text is valid representation of function name.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid function name
	 */
	private boolean isFunction(String input) {
		return input.startsWith("@") && isVariable(input.substring(1));
	}
	
	/**
	 * Checks if input text is valid representation of string.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid string
	 */
	private boolean isString(String input) {
		return input.startsWith("\"") && input.endsWith("\"");
	}
	
	/**
	 * Checks if input text is valid representation of operator.
	 * 
	 * @param input input text
	 * @return <b>'true'</b> if input text is valid operator
	 */
	private boolean isOperator(String input) {
		return input.equals("+") || input.equals("-") || input.equals("*") 
				|| input.equals("/") || input.equals("^");
	}
	
	/**
	 * Gets DocumentNode, document model representation generated from process of parsing.
	 * 
	 * @return document model representation generated by process of parsing
	 */
	public DocumentNode getDocumentNode() {
		return documentNode;
	}
	
}