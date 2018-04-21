package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * TreeWriter is class which demonstrates how {@link INodeVisitor} can be 
 * implemented to be able to produce exact original form of text from given 
 * document tree. 
 * <p> Program expects a file name as a single argument from command line.</p>
 * 
 * @author Filip Klepo
 *
 */
public class TreeWriter {

	/**
	 * WriteVisitor is a {@link INodeVisitor} which reproduces original textual
	 * form on {@link System}'s output stream of a {@link DocumentNode} 
	 * given via method {@link #visitDocumentNode(DocumentNode)}.
	 * 
	 * @author Filip Klepo
	 *
	 */
	private static class WriterVisitor implements INodeVisitor {

		@Override
		public void visitTextNode(TextNode node) {
			System.out.print(node);
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.print(node);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.print(node);
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for(int i = 0; i < node.numberOfChildren(); ++i) {
				System.out.print(node.getChild(i));
			}
		}
		
	}
	
	/**
	 * The main method. Runs when program is started.
	 * 
	 * @param args arguments from the command line, see {@link TreeWriter}
	 * documentation for more info
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Single file name was expected.");
			return;
		}
		
		String docBody = null;
		try {
			docBody = new String(Files.readAllBytes(Paths.get(args[0])));
		} catch (IOException e) {
			System.out.println("Given file can not be read.");
			return;
		}
		
		SmartScriptParser p = new SmartScriptParser(docBody);
		WriterVisitor visitor = new WriterVisitor();
		p.getDocumentNode().accept(visitor);
	}
	
}
