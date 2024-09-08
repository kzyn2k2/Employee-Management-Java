package com.ta.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.spi.container.ContainerRequest;


/**
 * SQL injection filter
 * 
 */

public class SqlInjectFilter implements Filter {

	public FilterConfig config;
	public String sqlbody = "";

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.config = null;
		this.sqlbody = "";
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		
		String sql = "";
		sqlbody = "";
		MultiReadHttpServletRequest httprequest = new MultiReadHttpServletRequest((HttpServletRequest) request);
		HttpServletResponse httpresponse = (HttpServletResponse) response;
		// Get all request parameter names
		Enumeration<?> params = httprequest.getParameterNames();
		
		while (params.hasMoreElements()) {
			// Get the parameter name
			String name = params.nextElement().toString();
			// Get the corresponding value of the parameter
			String[] value = null;
			if(!name.equalsIgnoreCase("token")) {
				value = httprequest.getParameterValues(name);
			for (int i = 0; i < value.length; i++) {
				sql = sql + value[i];
				if (!value[i].equals("")) {
					sql += " ";
				}
			}
			}
		}


		String reqbody = getBody(httprequest);
		
		if(!reqbody.equals("") && !reqbody.contains("Content-Disposition: form-data;")){
			processJson(reqbody);
		}
		
		sql += sqlbody;
		sql = sql.replace('\n', ' ');
		if(sql.equals("")){
			chain.doFilter(httprequest, response);
		}else{
			//if(!httprequest.getRequestURI().contains("/serviceLoginW/signin") && !httprequest.getRequestURI().contains("/serviceLoginW/updateConfig")&& !httprequest.getRequestURI().contains("/serviceVideo/deleteDocument")) {
			if(!SqlSafeUtil.isSqlInjectionSafe(sql)){
				abortWithUnauthorized(httpresponse);
			}else{
				chain.doFilter(httprequest, response);
			}

		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		config = filterConfig;
		this.sqlbody = "";
	}
	private void abortWithUnauthorized(HttpServletResponse response) {
		
		ObjectMapper ob = new ObjectMapper();
		com.ta.share.Response res = new com.ta.share.Response("Please use SQL injection safe characters", false);
		
		
		
		try {
			PrintWriter output = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.addHeader("Access-Control-Allow-Origin", "*");
			output.print(ob.writeValueAsString(res));
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}
	public static String getBody(MultiReadHttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}
	public void processJson(String jsonStr) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        JsonNode node = objectMapper.readTree(jsonStr);
	        processNode(node);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

	private void processNode(JsonNode n){
	    if (n.isContainerNode()) {
	        processJsonContainer(n.iterator());
	    } else if (n.isNumber()) {
	    	sqlbody += String.valueOf(n.asDouble())+" ";
	    } else if (n.isBoolean()) {
	    	sqlbody += String.valueOf(n.asBoolean())+" ";
	    } else if (n.isTextual()) {
	    	byte ptext[] = n.asText().getBytes();
	    	String value;
			value = new String(ptext, StandardCharsets.US_ASCII);
			//System.out.println("data: "+ n.asText().toString());
			sqlbody += value+" ";
	    }
	}

	private void processJsonContainer(Iterator<JsonNode> iterator) {
	   while (iterator.hasNext()) {
	       processNode(iterator.next());
	   }
	}

}
