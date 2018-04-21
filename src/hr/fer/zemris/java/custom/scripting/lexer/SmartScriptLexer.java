package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * SmartScriptLexer is a engine which performs lexical analysis of textual data. It's task is to 
 * generate meaningful parts of input textual data, also known as tokens.
 * 
 * @author Filip Klepo
 *
 */
public class SmartScriptLexer {

	/**
	 * Input text in form of char array.
	 */
	private char[] data;
	/**
	 * Current index from which input data is read.
	 */
	private int currentIndex;
	/**
	 * Current state of this lexer.
	 */
	private LexerState currentState;
	/**
	 * Buffer in which parts of input data are stored in order to generate tokens.
	 */
	private StringBuilder buffer;
	
	/**
	 * Constructs instance of this class with given input text for purpose of lexical analysis.
	 * 
	 * @param text input text which will be used in process of lexical analysis
	 */
	public SmartScriptLexer(String text) {
		data = text.replaceAll("\\{[\n\r\t ]+$", "{$")
				.replaceAll("$[\n\r\t ]+}", "$}").toCharArray();
		
		currentState = LexerState.INIT;
		buffer = new StringBuilder();
	}
	
	/**
	 * Generates single SmartScriptToken from current content of Lexer's buffer.
	 * 
	 * @return token which represents data that was previously stored in Lexer's buffer
	 */
	private SmartScriptToken generateToken() {
		String data = buffer.toString();
		buffer.setLength(0);
		
		switch(currentState) {
		
		case INIT:
			throw new IllegalArgumentException("Can not generate Token in INIT state!");
			
		case TEXT:
			return new SmartScriptToken(data, SmartScriptTokenType.TEXT);
			
		case TAG:
			return new SmartScriptToken(data.trim(), SmartScriptTokenType.TAG);
		
		}
		
		//to suppress Eclipse's warning!
		return null;
	}
	
	/**
	 * Generates end-of-file token which indicates that input text is read completely.
	 * It's value is null.
	 * 
	 * @return end-of-file token
	 */
	private SmartScriptToken generateEOFToken() {
		return new SmartScriptToken(null, SmartScriptTokenType.EOF);
	}
	
	/**
	 * Checks if character combination is tag entry. Specified tag entry for this lexer is '{' followed
	 * by '$'. 
	 * 
	 * @param currentCharacter current character of input text
	 * @param nextCharacter following character of input text
	 * @return <b>'true'</b> if combination is valid tag entry
	 */
	private boolean combinationIsTagEntry(char currentCharacter, char nextCharacter) {
		return currentCharacter == '{' && nextCharacter == '$';
	}
	
	/**
	 * Checks if character combination is such that following tag is interpreted as text.
	 * If '\' comes before '{' and '$' tag will be read as text.
	 * 
	 * @param currentCharacter current character of input text
	 * @param nextCharacter following character of input text
	 * @return <b>'true'</b> if next tag will be interpreted as text
	 */
	private boolean combinationIsIgnoreTag(char currentCharacter, char nextCharacter) {
		return currentCharacter == '\\' && nextCharacter == '{';
	}
	
	/**
	 * Checks if character combination represents end of current tag.
	 * 
	 * @param currentCharacter current character of input text
	 * @param nextCharacter following character of input text
	 * @return <b>'true'</b> if combination is tag exit
	 */
	private boolean combinationIsTagExit(char currentCharacter, char nextCharacter) {
		return currentCharacter == '$' && nextCharacter == '}';
	}
	
	/**
	 * Checks if character combination represents valid escape sequence, '\' followed by '\', 
	 * which will be interpreted as a single '\' in text token.
	 * 
	 * @param currentCharacter current character of input text
	 * @param nextCharacter following character of input text
	 * @return <b>'true'</b> if combination is valid escape sequence
	 */
	private boolean combinationIsDoubleBackslash(char currentCharacter, char nextCharacter) {
		return currentCharacter == '\\' && nextCharacter == '\\';
	}
	
	/**
	 * Checks if char combination is not supported escape sequence.
	 * 
	 * @param currentCharacter current character in input text
	 * @param nextCharacter following character in input text
	 * @return <b>'true'</b> if combination is unsupported escape sequence
	 */
	private boolean combinationIsUnsupportedSequence(char currentCharacter, char nextCharacter) {
		return currentCharacter == '\\' && !(nextCharacter == '{') && !(nextCharacter == '\\');
	}
	
	/**
	 * Changes lexer's state.
	 * 
	 * @param newState new state of this lexer
	 */
	private void changeLexerState(LexerState newState) {
		currentState = newState;
	}
	
	/**
	 * Generates next token from input data.
	 * 
	 * @return token which represents a part of input data
	 */
	public SmartScriptToken nextToxen() {

		while(currentIndex < data.length) {
			char currentChar = data[currentIndex];
			char nextChar = '\0';
			
			try {
				nextChar = data[currentIndex + 1];
			} catch (Exception ex) {
				//this will execute when currentChar is the last char of string
			}
			
			switch(currentState) {
			
			case INIT :
				if(combinationIsTagEntry(currentChar, nextChar)) {
					changeLexerState(LexerState.TAG);
					++currentIndex;
					break;
				} else {
					changeLexerState(LexerState.TEXT);
					--currentIndex;
					break;
				}
				
			case TEXT:
				if(combinationIsTagEntry(currentChar, nextChar)) {
					if(buffer.length() != 0) {
						return generateToken();
					} else {
						changeLexerState(LexerState.TAG);
						++currentIndex;
					}
					
				} else if(combinationIsIgnoreTag(currentChar, nextChar) 
						|| combinationIsDoubleBackslash(currentChar, nextChar)) {
					buffer.append(nextChar);
					++currentIndex;
					
				} else if(combinationIsUnsupportedSequence(currentChar, nextChar)) {
					throw new RuntimeException("Unsupported escape sequence!");
				
				} else {
					buffer.append(currentChar);
				}
				
				break;
				
			case TAG:
				if(combinationIsTagExit(currentChar, nextChar)) {
					if(buffer.length() != 0) {
						return generateToken();
					} else {
						changeLexerState(LexerState.TEXT);
						++currentIndex;
					}
				} else {
					buffer.append(currentChar);
				}
				
			default:
				break;
				
			}
			
			++currentIndex;
		}
		
		if(currentIndex == data.length) {
			if(buffer.length() != 0) {
				return generateToken();
			} else {
				return generateEOFToken();
			}
		}

		 //to suppress Eclipse's warning!
		return null;
	}
	
}
