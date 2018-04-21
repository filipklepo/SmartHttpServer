package hr.fer.zemris.java.webserver;

import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.util.Random;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * SmartHttpServer models a simple web server which uses HyperText Transfer 
 * Protocol protocol to receive requests and generate responses. 
 * <p> This server's properties, such as address and port on which it listens,
 * paths to other configuration files and settings of {@link IWebWorker}s are
 * something which user must provide by constructor.</p>
 * 
 * @author Filip Klepo
 *
 */
public class SmartHttpServer {
	
	/**
	 * Class which models a single user's session with this server.
	 * 
	 * @author Filip Klepo
	 *
	 */
	private static class SessionMapEntry {
		/**
		 * Session ID.
		 */
		String sid;
		/**
		 * Dictates the time for which this session is valid.
		 */
		long validUntil;
		/**
		 * Map which holds this session's parameters.
		 */
		Map<String, String> map;
		
		/**
		 * The default constructor.
		 */
		public SessionMapEntry() {
			this.map = new ConcurrentHashMap<>();
		}
		
	}
	
	/**
	 * Address on which server listens to requests.
	 */
	private String address;
	/**
	 * Port on which server listens to requests.
	 */
	private int port;
	/**
	 * Number of worker threads in thread pool.
	 */
	private int workerThreads;
	/**
	 * Duration of user sessions in seconds.
	 */
	private int sessionTimeout;
	/**
	 * Map holding all supported mime types.
	 */
	private Map<String,String> mimeTypes = new HashMap<String, String>();
	/**
	 * Server's thread. Servers task in this thread is to receive requests and
	 * to generate responses.
	 */
	private ServerThread serverThread;
	/**
	 * Condition flag for server thread.
	 */
	private boolean serverThreadRunning;
	/**
	 * Pool of threads which are server's workers.
	 */
	private ExecutorService threadPool;
	/**
	 * The root file from which we serve responses.
	 */
	private Path documentRoot;
	/**
	 * Map which holds instances of {@link IWebWorker} mapped to their names.
	 */
	private Map<String,IWebWorker> workersMap;
	
	/**
	 * Map which holds user sessions mapped by their ID. 
	 */
	private Map<String, SessionMapEntry> sessions =
			new HashMap<String, SmartHttpServer.SessionMapEntry>();
	/**
	 * Generator of random numbers.
	 */
	private Random sessionRandom = new Random();

	/**
	 * Instantiates this server with given configuration file.
	 * 
	 * @param configFileName file which contains server's properties
	 */
	public SmartHttpServer(String configFileName) {
		Objects.requireNonNull(configFileName);
		Path configFileNamePath = Paths.get(configFileName).normalize();
		initSessionCleanerThread();
		
		if(!Files.isReadable(configFileNamePath)) {
			throw new IllegalArgumentException(
					"Expected path to readable .properties file!");
		}
		
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(configFileName));
			try {
				parseWorkers(props.getProperty("server.workers"));
			} catch (ClassNotFoundException 
					| InstantiationException 
					| IllegalAccessException e) {
				System.err.println("Can not determine the properties of this "
						+ "server since its properties can not be read.");
				return;
			}
			
			address = props.getProperty("server.address");
			port = Integer.parseInt(props.getProperty("server.port"));
			workerThreads = 
					Integer.parseInt(props.getProperty("server.workerThreads"));
			sessionTimeout = 
					Integer.parseInt(props.getProperty("session.timeout"));

			//load mimeTypes map with mimeConfig.properties file content
			Properties mimeProps = new Properties();
			mimeProps.load(
					new FileInputStream(props.getProperty("server.mimeConfig"))
					);
			for (final Entry<Object, Object> entry : mimeProps.entrySet()) {
				mimeTypes.put(
						(String) entry.getKey(), (String) entry.getValue());
			}

			documentRoot = Paths.get(
					props.getProperty("server.documentRoot")).normalize();
		} catch (NumberFormatException e1) {
			System.out.println("Number parsing error. "+e1.getMessage());
			return;
		} catch(IOException e2) {
			System.out.println("Error while reading from file. "
					+e2.getMessage());
			return;
		}
	}
	
	/**
	 * Parses workers from given path.
	 * 
	 * @param workersPropsPath path to workers properties file
	 * @throws IOException if I/O error of any kind has occurred
	 * @throws ClassNotFoundException if worker's class has not been found
	 * @throws InstantiationException if worker's class could not be 
	 * instantiated
	 * @throws IllegalAccessException if method does not have the privilege
	 * to access certain data
	 */
	private void parseWorkers(String workersPropsPath) 
			throws IOException, ClassNotFoundException, InstantiationException, 
			IllegalAccessException {
		if(workersMap == null) {
			workersMap = new HashMap<>();
			Properties props = new Properties();
			props.load(new FileReader(workersPropsPath));
			for(Entry<Object,Object> entry : props.entrySet()) {
				String path = (String)entry.getKey();
				String fqcn = (String)entry.getValue();
				if(workersMap.get(path) != null) {
					throw new RuntimeException("Duplicate key "+path);
				}
				
				Class<?> referenceToClass = 
						this.getClass().getClassLoader().loadClass(fqcn);
				Object newObject = referenceToClass.newInstance();
				IWebWorker iww = (IWebWorker)newObject;
				workersMap.put(path, iww);
			}

		}
	}
	
	/**
	 * Initializes the session cleaner thread. Session cleaner thread has the
	 * task to periodically go through current user sessions and delete them
	 * if their session time has expired.
	 */
	private void initSessionCleanerThread() {
		Thread cleaner = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					while(true) {
					try {
						//sleep for 5 minutes
						Thread.sleep(60*5*1000);
						break;
					} catch (InterruptedException e) {}
					}
					//clean expired sessions
					cleanExpiredSessions();
				}
			}
		});
		cleaner.setDaemon(true);
		cleaner.start();
	}
	
	/**
	 * Cleans the expired user sessions.
	 */
	private void cleanExpiredSessions() {
		Iterator<Entry<String, SessionMapEntry>> it = 
				sessions.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, SessionMapEntry> entry =
					(Entry<String,SessionMapEntry>)it.next();
			if(entry.getValue().validUntil < System.currentTimeMillis()/1000) {
				it.remove();
			}
		}
	}
	
	/**
	 * Starts the server.
	 */
	protected synchronized void start() {
		if(serverThread == null) {
			serverThread = new ServerThread();
			threadPool = Executors.newFixedThreadPool(workerThreads);
			serverThreadRunning = true;
			serverThread.start();
		}
	}
	
	/**
	 * Stops the server.
	 */
	protected synchronized void stop() {
		//signal to server's thread to stop working
		serverThreadRunning = false;
		threadPool.shutdown();
	}
	
	/**
	 * ServerThread is a class which models this server's thread.
	 * 
	 * <p>Task of a server thread is very simple and it can be described in
	 * two steps:
	 * <ul>
	 * <li>Read the request</li>
	 * <li>Generate the response</li>
	 * </ul>
	 * 
	 * @author Filip Klepo
	 *
	 */
	protected class ServerThread extends Thread {
		@Override
		public void run() {
			try (ServerSocket socket = new ServerSocket()){
				socket.bind(new InetSocketAddress(address, port));
				
				while(serverThreadRunning) {
					Socket client = socket.accept();
					ClientWorker cw = new ClientWorker(client);
					threadPool.submit(cw);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				return;
			}
		}
	}

	/**
	 * ClientWorker is a class which models the main worker used by this server.
	 * Every request represents a client, and every request given to server 
	 * will be constructed as instance of this class. 
	 * 
	 * @author Filip Klepo
	 *
	 */
	private class ClientWorker implements Runnable {
		/**
		 * Client's socket.
		 */
		private Socket csocket;
		/**
		 * Wrapped client's input stream.
		 */
		private PushbackInputStream istream;
		/**
		 * Client's output stream.
		 */
		private OutputStream ostream;
		/**
		 * Client's version.
		 */
		private String version;
		/**
		 * Client's method.
		 */
		private String method;
		/**
		 * Parameters of request.
		 */
		private Map<String,String> params = new HashMap<String, String>();
		/**
		 * Permanent parameters of request.
		 */
		private Map<String,String> permParams = new HashMap<String, String>();
		/**
		 * The output cookies.
		 */
		private List<RCCookie> outputCookies = new
				ArrayList<RequestContext.RCCookie>();

		/**
		 * Instantiates this class with given client socket.
		 * 
		 * @param csocket client socket
		 * @throws NullPointerException if given socket is null
		 */
		public ClientWorker(Socket csocket) {
			Objects.requireNonNull(csocket);

			this.csocket = csocket;
		}

		/**
		 * Runs this worker.
		 */
		public void run() {
			try {
				istream = new PushbackInputStream(csocket.getInputStream());
			} catch (IOException e) {
				System.err.println("Can not obtain socket's input stream.");
				return;
			}
			try {
				ostream = csocket.getOutputStream();
			} catch (IOException e) {
				System.err.println("Can not obtain socket's output stream.");
				return;
			}

			List<String> request = readRequest();
			if(request.size() < 1) {
				sendError(400, "Bad request");
				return;
			}
			
			checkSession(request);
			
			String firstLine = request.get(0);
			String[] firstLineParams = firstLine.split(" ");
			
			if(firstLineParams.length != 3) {
				sendError(400, "Bad request");
				return;
			}
			method = firstLineParams[0];
			String requestedPath = firstLineParams[1];
			version = firstLineParams[2];
			
			int indexOfQMark = requestedPath.indexOf('?');
			String path;
			String paramString = null;
			if(indexOfQMark != -1) {
				path = requestedPath.substring(0, indexOfQMark);
				paramString = requestedPath.substring(indexOfQMark+1);
			} else {
				path = requestedPath;
			}
			
			if(paramString!=null&&!parseParameters(paramString)) {
				sendError(400, "Bad request");
				return;
			}

			if(!method.equals("GET") 
					|| (!version.equals("HTTP/1.0")
							&&!version.equals("HTTP/1.1"))) {
				sendError(400, "Bad request");
				return;
			}
			
			RequestContext rc = new RequestContext(
					ostream, params, permParams, outputCookies);
			if(path.startsWith("/ext/")) {
				if(path.length() == 5) {
					sendError(404, "Unreadable");
				} else {
					String fqcn = "hr.fer.zemris.java.webserver.workers." 
							+ path.substring(5);
					try {
						Class<?> referenceToClass = 
								this.getClass().getClassLoader().loadClass(fqcn);
						Object newObject = referenceToClass.newInstance();
						IWebWorker iww = (IWebWorker)newObject;
						iww.processRequest(rc);
					} catch (ClassNotFoundException 
							| InstantiationException 
							| IllegalAccessException e) {
						sendError(404, "Unreadable");
					}
				}
				try {
					csocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			
			if(workersMap.get(path) != null) {
				workersMap.get(path).processRequest(rc);
				try {
					csocket.close();
				} catch (IOException e) {
				}
				return;
			}
			
			Path resolvedPath;
			try {
				resolvedPath = Paths.get(documentRoot.toString(), path);
			} catch (Exception e1) {
				sendError(404, "Unreadable");
				return;
			}
			if(!resolvedPath.startsWith(documentRoot)) {
				sendError(403, "Forbidden");
				return;
			}
			if(!Files.isReadable(resolvedPath)) {
				sendError(404, "Unreadable");
				return;
			}
			
			String extension = null;
			int extensionIndex = resolvedPath.toString().lastIndexOf('.');
			extension = resolvedPath.toString().substring(extensionIndex + 1);
			
			String mimeType = mimeTypes.get(extension);
			mimeType = mimeType == null ? "application/octet-stream" : mimeType;
			//brojPoziva.smscr will set the mime type by itself
			if(!resolvedPath.getFileName().toString().startsWith("brojPoziva")) {
				rc.setMimeType(mimeType);
			}
			
			if(extension.equals("smscr")) {
				try {
					new SmartScriptEngine(
							new SmartScriptParser(
									new String(Files.readAllBytes(resolvedPath), 
											StandardCharsets.UTF_8))
							.getDocumentNode(), rc).execute();
				} catch (IOException e) {
					sendError(404, "Unreadable");
				}
			} else {
				try {
					byte[] fileBytes = Files.readAllBytes(resolvedPath);
					rc.write(fileBytes);
				} catch (IOException e) {}
			}
			
			try {
				csocket.close();
			} catch (IOException e) {}
		}
		
		/**
		 * Checks given header lines for a eventual SID value of a cookie.
		 * 
		 * @param headerLines lines of request's header
		 */
		private void checkSession(List<String> headerLines) {
			for(String headerLine : headerLines) {
				if(headerLine.startsWith("Cookie:")) {
					String[] cookies = headerLine.substring(8).split("; ");
					String sidCandidate = null;
					SessionMapEntry session = null;
					for(String cookie : cookies) {
						if(cookie.startsWith("sid")) {
							sidCandidate = cookie.split("=")[1];
							break;
						}
					}
					session = sessions.get(sidCandidate);
					if(session != null) {
						if(session.validUntil < System.currentTimeMillis()/1000) {
							sessions.remove(sidCandidate);
							session = null;
						}
					}
					if(session == null) {
						session = new SessionMapEntry();
						session.sid = getNewSID();	
						sessions.put(session.sid, session);
					}
					session.validUntil = System.currentTimeMillis()/1000 
								+ sessionTimeout;
					
					permParams = session.map;
					outputCookies.add(
							new RCCookie(
									"sid", session.sid, null, address, "/"));
					return;
				}
			}
			//Header does not contain Cookie content
			SessionMapEntry session = new SessionMapEntry();
			session.sid = getNewSID();
			session.validUntil = System.currentTimeMillis()/1000 
					+ sessionTimeout;
			sessions.put(session.sid, session);
			permParams = session.map;
			outputCookies.add(
					new RCCookie(
							"sid", session.sid, null, address, "/"));
			
		}
		
		/**
		 * Generates new, 20-character long, random {@link String} which 
		 * represents a SID, the session ID.
		 * 
		 * @return new SID
		 */
		private String getNewSID() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < 20; ++i) {
				char c = (char)(65+sessionRandom.nextInt(26));
				sb.append(c);
			}
			return sb.toString();
		}

		/**
		 * Parses given {@link String} form  of request parameters.
		 * 
		 * @param parameters {@link String} form of parameters
		 * @return <b>true</b> if parameters were parsed successfully
		 */
		private boolean parseParameters(String parameters) {
			if(parameters == null || parameters.isEmpty()) {
				return false;
			}
			
			String[] paramsArr = parameters.split("&");
			for(String param : paramsArr) {
				String[] paramParts = param.split("=");
				if(paramParts.length != 2 || paramParts[0].isEmpty() 
						|| paramParts[1].isEmpty()) {
					return false;
				}
				params.put(paramParts[0], paramParts[1]);
			}
			return true;
		}
		
		/**
		 * Sends error response to client. Error response is sent if regular 
		 * response could not be generated due to unfixable problems.
		 * 
		 * @param statusCode status code of error response, indicating 
		 * the specific type of error
		 * @param statusText status text of error response
		 */
		private void sendError(int statusCode, String statusText) {
			String response = "<html><head><title>"+statusText+"</title></head>"
					+ "<body><b>"+statusCode+" "+statusText+"</b></body><html>";

			try {
				ostream.write(
						("HTTP/1.1 "+statusCode+" "+statusText+"\r\n"+
								"Server: Smart Http Server\r\n"+
								"Content-Type: text/html;charset=UTF-8\r\n"+
								"Content-Length: "+response.length()+"\r\n"+
								"Connection: close\r\n"+
								"\r\n"+response)
									.getBytes(StandardCharsets.ISO_8859_1)
				);
				ostream.flush();
			} catch (IOException e) {
			}
		}

		/**
		 * Reads request.
		 * 
		 * @return {@link List} of request lines
		 */
		private List<String> readRequest() {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;
l:			while(true) {
				int b;
				try {
					b = istream.read();
				} catch (IOException e) {
					return null;
				}
				if(b==-1) return null;
				if(b!=13) {
					bos.write(b);
				}
				// 13 = \r ; 10 = \n
				switch(state) {
				case 0:
					if(b==13) {state=1; } else if(b==10) state = 4;
					break;
				case 1:
					if(b==10) { state=2; } else state = 0;
					break;
				case 2:
					if(b==13) { state=3; } else state = 0;
					break;
				case 3 :
					if(b==10) { break l; } else state = 0;
					break;
				case 4:
					if(b==10) { break l; } else state = 0;
					break;

				}
			}

			String str = new String(
					bos.toByteArray(), StandardCharsets.ISO_8859_1);
			
			List<String> headers = new ArrayList<>();
			String currentLine = null;
			for(String s : str.split("\n")) {
				if(s.isEmpty()) break;
				char c = s.charAt(0);
				if(c==9 || c==32) {
					currentLine +=s;
				} else {
					if(currentLine != null) {
						headers.add(currentLine);
					}
					currentLine = s;
				}
			}
			if(!currentLine.isEmpty()) {
				headers.add(currentLine);
			}
			return headers;
		}

	}
	
	/**
	 * The main method. Starts {@link SmartHttpServer}.
	 * 
	 * @param args arguments from the command line, not used
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Path to server's .properties file expected!");
			return;
		}
		new SmartHttpServer(args[0]).start();
	}
	
}
