package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * EchoParams is an {@link IWebWorker} which, whenever called, writes table of
 * given parameters, in HTML, on {@link RequestContext}'s output stream.
 * 
 * @author Filip Klepo
 *
 */
public class EchoParams implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) {
		try {
			context.write(
					"<html> <head> <title>Parameters</title> </head> <body>");
			context.write("<table border=\"1\">");
			context.write("<tr> <td><b>Name</b></td> <td><b>Value</b></td></tr>");
			for(String paramName : context.getParameterNames()) {
				context.write("<tr> <td>"+ paramName +"</td> ");
				context.write("<td>"+ context.getParameter(paramName) 
					+ "</td> </tr>");
			}
			context.write("</table> </body>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
