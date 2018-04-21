package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * RequestContext is a class which models the context in which user's request
 * to the server is. Context for user's request includes: 
 * <ul>
 * <li> {@link OutputStream} on which response is written </li>
 * <li> Response's {@link Charset} </li>
 * <li> Response's status code </li>
 * <li> Response's status text </li>
 * <li> Response's mime type </li>
 * <li> Request's parameters and persistent parameters </li>
 * <li> Request's cookies modeled by {@link RCCookie} </li>
 * </ul>
 * 
 * @author Filip Klepo
 *
 */
public class RequestContext {

	/**
	 * RCCookie is a class which models a HTTP cookie. HTTP cookie is a small
	 * piece of data sent from a server and stored in user's web browser while
	 * the user is browsing. Cookies are a reliable mechanism for a server to
	 * remember stateful information (such as items added in the shopping cart
	 * in an online store) or to record the user's browsing activity.
	 * 
	 * @author Filip Klepo
	 *
	 */
	public static class RCCookie {
		
		/**
		 * Name of cookie.
		 */
		private final String name;
		/**
		 * Value of cookie.
		 */
		private final String value;
		/**
		 * Domain for which cookie is valid.
		 */
		private final String domain;
		/**
		 * Path for which cookie is valid.
		 */
		private final String path;
		/**
		 * Time, in seconds, for which this cookie is valid. Time is measured
		 * as difference between the cookie's maximal age (in seconds) 
		 * and midnight, January 1, 1970 UTC.
		 */
		private final Integer maxAge;
		
		/**
		 * Instantiates this class with given parameters.
		 * 
		 * @param name name of cookie
		 * @param value value of cookie
		 * @param maxAge max age of cookie
		 * @param domain domain for which cookie is valid
		 * @param path path for which cookie is valid
		 */
		public RCCookie(String name, String value, Integer maxAge, String domain, 
				String path) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		/**
		 * Gets cookie's name.
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets cookie's value.
		 * 
		 * @return value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Gets domain for which cookie is valid.
		 * 
		 * @return domain
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Gets path for which cookie is valid.
		 * 
		 * @return path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Gets time, in seconds, for which cookie is valid.
		 * 
		 * @return time
		 */
		public Integer getMaxAge() {
			return maxAge;
		}
		
	}
	
	/**
	 * OutputStream on which response is written.
	 */
	private OutputStream outputStream;
	/**
	 * Response's charset.
	 */
	private Charset charset;
	/**
	 * Header's charset.
	 */
	private final static Charset HEADER_CHARSET = StandardCharsets.ISO_8859_1;
	
	/**
	 * Response's body encoding.
	 */
	private String encoding = "UTF-8";
	/**
	 * Response's status code.
	 */
	private int statusCode = 200;
	/**
	 * Response's content length. If content length is -1, user did not set 
	 * this parameter and it will not be visible in response's header.
	 */
	private int contentLength = -1;
	/**
	 * Response's status text.
	 */
	private String statusText = "OK";
	/**
	 * Response's mime type.
	 */
	private String mimeType = "text/html";
	
	/**
	 * Request's parameters.
	 */
	private Map<String,String> parameters;
	/**
	 * Request's temporary parameters.
	 */
	private Map<String,String> temporaryParameters;
	/**
	 * Request's persistent parameters.
	 */
	private Map<String,String> persistentParameters;
	/**
	 * Request's cookies.
	 */
	private List<RCCookie> outputCookies;
	/**
	 * Flag which indicates if header is generated. If header is generated, 
	 * user will not be able to change some parameters of {@link RequestContext}.
	 */
	private boolean headerGenerated;
	
	/**
	 * Instantiates this class with given parameters.
	 * 
	 * @param outputStream output stream on which response is written
	 * @param parameters request's parameters
	 * @param persistentParameters request's persistent parameters
	 * @param outputCookies request's cookies
	 * @throws NullPointerException if output stream is null-reference
	 */
	public RequestContext(
			OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, 
			List<RCCookie> outputCookies) {
		Objects.requireNonNull(outputStream);
		
		this.outputStream = outputStream;
		this.parameters = parameters;
		this.persistentParameters = persistentParameters;
		this.outputCookies = outputCookies;
		
		temporaryParameters = new HashMap<>();
	}
	
	/**
	 * Sets new encoding. If header is already generated, using this method will
	 * result in {@link RuntimeException} since the encoding is already written
	 * in response's header.
	 * 
	 * @param encoding new encoding
	 * @throws RuntimeException if header is already generated
	 */
	public void setEncoding(String encoding) {
		if(headerGenerated) {
			throw new RuntimeException("Header generated");
		}
		
		this.encoding = encoding;
	}

	/**
	 * Sets new status code. If header is already generated, using this
	 * method will result in {@link RuntimeException} since the status code 
	 * is already written in response's header.
	 * 
	 * @param statusCode new status code
	 * @throws RuntimeException if header is already generated
	 */
	public void setStatusCode(int statusCode) {
		if(headerGenerated) {
			throw new RuntimeException("Header generated");
		}
		
		this.statusCode = statusCode;
	}

	/**
	 * Sets new status text. If header is already generated, using this
	 * method will result in {@link RuntimeException} since the status text 
	 * is already written in response's header.
	 * 
	 * @param statusText new status text
	 * @throws RuntimeException if header is already generated
	 */
	public void setStatusText(String statusText) {
		if(headerGenerated) {
			throw new RuntimeException("Header generated");
		}
		
		this.statusText = statusText;
	}

	/**
	 * Sets new mime type. If header is already generated, using this
	 * method will result in {@link RuntimeException} since the mime type
	 * is already written in response's header.
	 * 
	 * @param mimeType new mime type
	 * @throws RuntimeException if header is already generated
	 */
	public void setMimeType(String mimeType) {
		if(headerGenerated) {
			throw new RuntimeException("Header generated");
		}
		
		this.mimeType = mimeType;
	}
	
	/**
	 * Sets content length. If given length is lesser than 0, 
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param contentLength new content length
	 * @throws IllegalArgumentException if given length is lesser than 0
	 */
	public void setContentLength(int contentLength) {
		if(contentLength < 0) {
			throw new IllegalArgumentException(
					"Length of content can not be nageative.");
		}
		
		this.contentLength = contentLength;
	}
	
	/**
	 * Gets parameter.
	 * 
	 * @param name parameter's name
	 * @return parameter's value
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * Gets unmodifiable set of parameters names.
	 * 
	 * @return parameters names
	 */
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}
	
	/**
	 * Gets persistent parameter.
	 * 
	 * @param name parameter's name
	 * @return parameter's value
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}
	
	/**
	 * Gets unmodifiable set of persistent parameters names.
	 * 
	 * @return parameters names
	 */
	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}
	
	/**
	 * Sets persistent parameter.
	 * 
	 * @param name parameter's name
	 * @param value parameter's value
	 */
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}
	
	/**
	 * Removes persistent parameter.
	 * 
	 * @param name parameter's name
	 */
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}
	
	/**
	 * Gets temporary parameter.
	 * 
	 * @param name parameter's name
	 * @return parameter's value
	 */
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}
	
	/**
	 * Gets unmodifiable set of temporary parameters names.
	 * 
	 * @return parameters names
	 */
	public Set<String> getTemporaryParameterNames() {
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}
	
	/**
	 * Sets temporary parameter.
	 * 
	 * @param name parameter's name
	 * @param value parameter's value
	 */
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}
	
	/**
	 * Removes temporary parameter.
	 * 
	 * @param name parameter's name
	 */
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}
	
	/**
	 * Adds cookie.
	 * 
	 * @param cookie new cookie
	 * @throws NullPointerException if given cookie is null-reference
	 */
	public void addRCCookie(RCCookie cookie) {
		Objects.requireNonNull(cookie);
		
		outputCookies.add(cookie);
	}
	
	/**
	 * Writes given array of bytes on output stream.
	 * 
	 * @param data array of bytes
	 * @return reference to this {@link RequestContext} 
	 * @throws IOException if I/O exception of any kind has occurred
	 */
	public RequestContext write(byte[] data) throws IOException {
		Objects.requireNonNull(data);
		if(!headerGenerated) {
			generateHeader();
		}
		
		outputStream.write(data);
		outputStream.flush();
		return this;
	}
	
	
	/**
	 * Writes given {@link String} on output stream.
	 * 
	 * @param text text
	 * @return reference to this {@link RequestContext} 
	 * @throws IOException if I/O exception of any kind has occurred
	 */
	public RequestContext write(String text) throws IOException {
		Objects.requireNonNull(text);
		if(!headerGenerated) {
			generateHeader();
		}

		outputStream.write(text.getBytes(charset));
		outputStream.flush();
		return this;
	}

	/**
	 * Generates the response's header.
	 */
	private void generateHeader() {
		charset = Charset.forName(encoding);
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 ").append(statusCode).append(" ")
			.append(statusText).append("\r\n");
		sb.append("Content-Type: ").append(mimeType)
			.append(mimeType.startsWith("text/") ? ("; charset= " +encoding) 
												 : "")
			.append("\r\n");
		if(contentLength > -1) {
			sb.append("Content-Length: ").append(contentLength).append("\r\n");
		}
		if(outputCookies != null && outputCookies.size() > 0) {
			for(RCCookie outputCookie : outputCookies) {
				sb.append("Set-Cookie: ");
				sb.append(outputCookie.getName()).append("=")
				.append(outputCookie.getValue());
				if(outputCookie.getDomain() != null) {
					sb.append("; Domain=").append(outputCookie.getDomain());
				}
				if(outputCookie.getPath() != null) {
					sb.append("; Path=").append(outputCookie.getPath());
				}
				if(outputCookie.getMaxAge() != null) {
					sb.append("; Max-Age=").append(outputCookie.getMaxAge());
				}
				sb.append("\r\n");
			}
		}
		sb.append("\r\n");
		
		try {
			outputStream.write(sb.toString().getBytes(HEADER_CHARSET));
			outputStream.flush();
		} catch (IOException e) {
			return;
		}
		headerGenerated = true;
	}
	
}
