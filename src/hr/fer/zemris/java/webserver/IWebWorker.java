package hr.fer.zemris.java.webserver;

/**
 * IWebWorker is an interface which models an abstract web worker used by 
 * {@link SmartHttpServer}. Every worker must be able to process the request 
 * defined by {@link RequestContext}.
 * 
 * @author Filip Klepo
 *
 */
public interface IWebWorker {

	/**
	 * Processes request modeled by given {@link RequestContext} instance.
	 * 
	 * @param context context of request
	 */
	public void processRequest(RequestContext context);
	
}