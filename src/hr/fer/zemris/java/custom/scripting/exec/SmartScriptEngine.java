package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * SmartScriptEngine is a class which models a engine which runs .smscr scripts.
 * 
 * 
 * @author Filip Klepo
 *
 */
public class SmartScriptEngine {

	/**
	 * Document node which engine uses to run the script.
	 */
	private DocumentNode documentNode;
	/**
	 * Request context which holds parameters used in scripts.
	 */
	private RequestContext requestContext;
	/**
	 * Stack used for generating the results.
	 */
	private ObjectMultistack multistack = new ObjectMultistack();
	/**
	 * The stack functions.
	 */
	private final static Map<String, Consumer<ObjectStack>> STACK_FUNS;
	/**
	 * The stack functions which use {@link RequestContext}.
	 */
	private final static 
	Map<String, BiConsumer<ObjectStack, RequestContext>> STACK_REQCON_FUNS;
	/**
	 * List which holds valid operations names.
	 */
	private final static List<String> VALID_OPERATIONS;
	
	static {
		STACK_FUNS = new HashMap<>();
		
		STACK_FUNS.put("+", new BinaryNumOperationStackConsumer((a,b) -> a + b));
		STACK_FUNS.put("-", new BinaryNumOperationStackConsumer((a,b) -> a - b));
		STACK_FUNS.put("*", new BinaryNumOperationStackConsumer((a,b) -> a * b));
		STACK_FUNS.put("/", new BinaryNumOperationStackConsumer((a,b) -> a / b));
		
		STACK_FUNS.put("sin", new Consumer<ObjectStack>() {
			@Override
			public void accept(ObjectStack t) {
				Double num = Double.parseDouble(t.pop().toString()) 
						* Math.PI/180;
				t.push(Math.sin(num));
			}
		});
		
		STACK_FUNS.put("decfmt", new Consumer<ObjectStack>() {
			@Override
			public void accept(ObjectStack t) {
				String format = t.pop().toString();
				Double num = Double.parseDouble(t.pop().toString());
				
				t.push(new DecimalFormat(format).format(num));
			}
		});
		
		STACK_FUNS.put("dup", new Consumer<ObjectStack>() {
			@Override
			public void accept(ObjectStack t) {
				t.push(t.peek());
			}
		});
		
		STACK_FUNS.put("swap", new Consumer<ObjectStack>() {
			@Override
			public void accept(ObjectStack t) {
				Object second = t.pop();
				Object first = t.pop();
				
				t.push(second);
				t.push(first);
			}
		});
		
		STACK_REQCON_FUNS = new HashMap<>();
		
		STACK_REQCON_FUNS.put("setMimeType", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				String mimeType = t.pop().toString();
				if(mimeType.startsWith("\"")&&mimeType.endsWith("\"")) {
					if(mimeType.length() > 2) {
						mimeType = mimeType.substring(1, mimeType.length()-1);
					} else {
						return;
					}
				}

				u.setMimeType(mimeType);
			}
		});
		
		STACK_REQCON_FUNS.put("paramGet", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				Object defValue = t.pop();
				String name = t.pop().toString();
				
				Object value = u.getParameter(name);
				t.push(value != null ? value : defValue);
			}
		});
		
		STACK_REQCON_FUNS.put("pparamGet", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				Object defValue = t.pop();
				String name = t.pop().toString();
				
				Object value = u.getPersistentParameter(name);
				t.push(value != null ? value : defValue);
			}
		});
		
		STACK_REQCON_FUNS.put("pparamSet", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				String name = t.pop().toString();
				String value = t.pop().toString();
				
				u.setPersistentParameter(name, value);
			}
		});
		
		STACK_REQCON_FUNS.put("pparamDel", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				String name = t.pop().toString();
				
				u.removePersistentParameter(name);
			}
		});
		
		STACK_REQCON_FUNS.put("tparamGet", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				Object defValue = t.pop();
				String name = t.pop().toString();
				
				Object value = u.getTemporaryParameter(name);
				t.push(value != null ? value : defValue);
			}
		});
		
		STACK_REQCON_FUNS.put("tparamSet", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				String name = t.pop().toString();
				String value = t.pop().toString();
				
				u.setTemporaryParameter(name, value);
			}
		});
		
		STACK_REQCON_FUNS.put("tparamDel", 
				new BiConsumer<ObjectStack, RequestContext>() {
			
			@Override
			public void accept(ObjectStack t, RequestContext u) {
				String name = t.pop().toString();
				
				u.removeTemporaryParameter(name);
			}
		});
		
		VALID_OPERATIONS = new ArrayList<>();
		VALID_OPERATIONS.add("+");
		VALID_OPERATIONS.add("-");
		VALID_OPERATIONS.add("*");
		VALID_OPERATIONS.add("/");
	}
	
	/**
	 * BinaryNumOperationStackConsumer is a {@link Consumer} which uses 
	 * {@link ObjectStack} to load parameters, perform operations on them and
	 * to store the result on it.
	 * 
	 * @author Filip Klepo
	 *
	 */
	private static class BinaryNumOperationStackConsumer 
		implements Consumer<ObjectStack> {
		
		/**
		 * Function used by this consumer.
		 */
		private BiFunction<Double, Double, Double> fun;
		
		/**
		 * Instantiates this class with given function.
		 * 
		 * @param fun function used by this consumer
		 */
		public BinaryNumOperationStackConsumer(
				BiFunction<Double, Double, Double> fun) {
			this.fun = fun;
		}

		@Override
		public void accept(ObjectStack t) {
			Double operand2 = Double.parseDouble(t.pop().toString());
			Double operand1 = Double.parseDouble(t.pop().toString());
			Object res = fun.apply(operand1, operand2);
			Object integerRes = transformToInteger(res);
			
			t.push(integerRes != null ? integerRes : res); 
		}
		
		/**
		 * Transforms given {@link Object} to {@link Integer} if possible.
		 * 
		 * @param value {@link Object}
		 * @return {@link Integer} if successfuly transformed, <b>null</b> if 
		 * not
		 */
		private Object transformToInteger(Object value) {
			Double doubleValue = (Double)value;
			if(doubleValue%1 != 0) {
				return null;
			}
			
			return doubleValue.intValue();
		}
	}
	
	/**
	 * Visitor which holds the main functionality of {@link SmartScriptEngine}.
	 * It's task is to go through each child node of given {@link DocumentNode}
	 * and to produce result of running of nodes.
	 */
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.toString());
			} catch (IOException e) {}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			String variable = node.getVariable().asText();
			Object startexpr = node.getStartExpression().asText();
			Object stepExpr = node.getStepExpression().asText();
			Object endExpr = node.getEndExpression().asText();
			
			multistack.push(variable, new ValueWrapper(startexpr));
			while(multistack.peek(variable).numCompare(endExpr) <= 0) {
				for(int i = 0; i < node.numberOfChildren(); ++i) {
					node.getChild(i).accept(this);
				}
				multistack.peek(variable).increment(stepExpr);
			}
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			ObjectStack stack = new ObjectStack();
			for(Element elem : node.getElements()) {
				if(elementIsConstant(elem)) {
					String val = elem.asText();
					stack.push((elem instanceof ElementString) 
							? val.substring(1, val.length() - 1) 
							: val);
				} else if(elementIsVariable(elem)){
					Object curValue = multistack.peek(elem.asText()).getValue();
					if(curValue == null) {
						throw new RuntimeException("Unknown variable " 
								+ elem.asText());
					}
					stack.push(curValue);
				} else if(elementIsFunction(elem)) {
					String fun = elem.asText().substring(1);
					if(functionUsesRequestContext(fun)) {
						STACK_REQCON_FUNS
						.get(fun)
						.accept(stack, requestContext);
					} else {
						STACK_FUNS.get(fun).accept(stack);
					}
				} else if(elementIsOperator(elem)) {
					String operator = elem.asText();
					if(!VALID_OPERATIONS.contains(operator)) {
						throw new RuntimeException(
								"Unsupported operator "+operator);
					}
					STACK_FUNS.get(operator).accept(stack);
				}
			}
			
			String[] elems = new String[stack.size()];
			for(int i = stack.size() - 1; i >= 0; i--) {
				elems[i] = stack.pop().toString();
			}
			
			for(String elem : elems) {
				try {
					requestContext.write(elem);
				} catch (IOException e) {}
			}
		}
		
		/**
		 * Checks if function uses {@link RequestContext}.
		 * 
		 * @param operation name of operation
 		 * @return <b>true</b> if function uses {@link RequestContext}
		 */
		private boolean functionUsesRequestContext(String operation) {
			return operation.startsWith("param")
					|| operation.startsWith("pparam")
					|| operation.startsWith("tparam")
					|| operation.contains("Mime");
		}
		
		/**
		 * Check if given {@link Element} is a constant.
		 * 
		 * @param elem {@link Element}
		 * @return <b>true</b> if {@link Element} is constant
		 */
		private boolean elementIsConstant(Element elem) {
			return elem instanceof ElementConstantInteger 
					|| elem instanceof ElementConstantDouble
					|| elem instanceof ElementString;
		}
		
		/**
		 * Check if given {@link Element} is a variable.
		 * 
		 * @param elem {@link Element}
		 * @return <b>true</b> if {@link Element} is variable
		 */
		private boolean elementIsVariable(Element elem) {
			return elem instanceof ElementVariable;
		}
		
		/**
		 * Check if given {@link Element} is a operator.
		 * 
		 * @param elem {@link Element}
		 * @return <b>true</b> if {@link Element} is operator
		 */
		private boolean elementIsOperator(Element elem) {
			return elem instanceof ElementOperator;
		}
		
		/**
		 * Check if given {@link Element} is a function.
		 * 
		 * @param elem {@link Element}
		 * @return <b>true</b> if {@link Element} is function
		 */
		private boolean elementIsFunction(Element elem) {
			return elem instanceof ElementFunction;
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for(int i = 0; i < node.numberOfChildren(); ++i) {
				node.getChild(i).accept(this);
			}
		}
		
	};

	/**
	 * Instantiates this class with given parameters.
	 * 
	 * @param documentNode document node used by engine
	 * @param requestContext request context which holds the script parameters
	 */
	public SmartScriptEngine(DocumentNode documentNode, RequestContext
			requestContext) {
		Objects.requireNonNull(documentNode);
		Objects.requireNonNull(requestContext);
		
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}

	/**
	 * Executes the loaded script.
	 */
	public void execute() {
		documentNode.accept(visitor);
	}

}