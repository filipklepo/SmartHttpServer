package hr.fer.zemris.java.webserver;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

@SuppressWarnings("javadoc")
public class RequestContextTests {
	
	ByteArrayOutputStream os;
	
	@Before
	public void init() {
		os = new ByteArrayOutputStream();
	}

	@Test(expected=NullPointerException.class)
	public void testInitWithInvalidParameters() {
		new RequestContext(null, 
				new HashMap<String, String>(), new HashMap<String, String>(), 
				new ArrayList<RequestContext.RCCookie>());
	}
	
	@Test
	public void testInitWithValidParameters() {
		Map<String,String> map = new HashMap<>();
		map.put("Filip", "ja");
		RequestContext rq = new RequestContext(os, map, null, null);
		assertEquals("ja", rq.getParameter("Filip"));
		
		rq = new RequestContext(os, null, map, null);
		assertEquals("ja", rq.getPersistentParameter("Filip"));
	}
	
	@Test
	public void testParameterManipulation() {
		RequestContext rq = new RequestContext(
				os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
		
		rq.setPersistentParameter("Hajduk", "drugi");
		rq.setTemporaryParameter("Dinamo", "prvi");
		
		assertEquals("drugi", rq.getPersistentParameter("Hajduk"));
		assertEquals("prvi", rq.getTemporaryParameter("Dinamo"));
	}
	
	@Test
	public void testUnmodifiableElements() {
		RequestContext rq = new RequestContext(
				os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
		
		try {
			rq.getParameterNames().add("Something");
			fail("Parameter names must be a unmodifiable collection.");
		} catch (UnsupportedOperationException e) {}
		
		try {
			rq.getPersistentParameterNames().add("Something");
			fail("Persistent parameter names must be a unmodifiable collection.");
		} catch (UnsupportedOperationException e) {}
		
		try {
			rq.getTemporaryParameterNames().add("Something");
			fail("Temporary parameter names must be a unmodifiable collection.");
		} catch (UnsupportedOperationException e) {}
	}
	
	@Test
	public void testHeaderFormat() {
		RequestContext rq = new RequestContext(
				os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
		
		rq.setMimeType("text/plain");
		try {
			rq.write("Filip");
		} catch (IOException e) {
			fail("I/O error!");
		}
		
		String exp = "HTTP/1.1 200 OK\r\nContent-Type: text/plain; "
				+ "charset= UTF-8\r\n\r\nFilip";
		
		assertEquals(exp, new String(os.toByteArray()));
	}
	
	@Test
	public void testHeaderFormatWithCookies() {
		RequestContext rq = new RequestContext(
				os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
		rq.addRCCookie(new RCCookie("Filip", "ja", null, "localhost", "/"));
		
		rq.setMimeType("text/plain");
		rq.setContentLength(5);
		try {
			rq.write("Filip");
		} catch (IOException e) {
			fail("I/O error!");
		}
		
		String exp = "HTTP/1.1 200 OK\r\nContent-Type: text/plain; charset= "
				+ "UTF-8\r\nContent-Length: 5\r\nSet-Cookie: Filip=ja; "
				+ "Domain=localhost; Path=/\r\n\r\nFilip";
		
		assertEquals(exp, new String(os.toByteArray()));
	}
	
	@Test
	public void testOperationsAfterHeaderGeneration() {
		RequestContext rq = new RequestContext(
				os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
		rq.addRCCookie(new RCCookie("Filip", "ja", null, "localhost", "/"));
		
		rq.setMimeType("text/plain");
		rq.setContentLength(5);
		try {
			rq.write("Filip".getBytes());
		} catch (IOException e) {
			fail("I/O error!");
		}
		
		try {
			rq.setEncoding("UTF-16");
			fail("Change of enconding must result with exception after "
					+ "header is generated.");
		} catch (RuntimeException e) {}
		
		try {
			rq.setStatusCode(1950);
			fail("Change of status code must result with exception after "
					+ "header is generated.");
		} catch (RuntimeException e) {}
		
		try {
			rq.setStatusText("something");
			fail("Change of status text must result with exception after "
					+ "header is generated.");
		} catch (RuntimeException e) {}
		
		try {
			rq.setMimeType("text/html");
			fail("Change of mime type must result with exception after "
					+ "header is generated.");
		} catch (RuntimeException e) {}
	}
	
	
}
