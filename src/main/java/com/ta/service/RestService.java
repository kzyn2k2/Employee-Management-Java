package com.ta.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.core.header.FormDataContentDisposition;

import com.ta.dao.EmployeeDao;
import com.ta.mgr.RestServiceManager;
import com.ta.share.CityData;
import com.ta.share.EmployeeData;
import com.ta.share.EmployeeDataSet;
import com.ta.share.FormSelectData;
import com.ta.share.ImageResponse;
import com.ta.share.Pager;
import com.ta.share.Response;
import com.ta.share.TestMessage;

@Path("service")
public class RestService {

	@Context
	HttpServletRequest request;

	@Context
	HttpServletResponse response;
	
	@Context
	ServletContext context;
	
	
	@POST
	@Path("post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestMessage postTest(TestMessage message) {
		
		TestMessage res = new TestMessage();
		res.setDate(message.getDate());
		res.messageDate();
		return res;
	}
	
	@GET
	@Path("formselect")
	@Produces(MediaType.APPLICATION_JSON)
	public FormSelectData getCities() {
		
		return RestServiceManager.getFormSelectData(getPath());
	}

	
	@POST
	@Path("addemp")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEmployee(EmployeeData data) {
		return RestServiceManager.addEmployee(data, getPath());
	}
	
	@POST
	@Path("employees")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EmployeeDataSet getEmployees(Pager pager) {
		
		return RestServiceManager.getEmployees(getPath(), pager);
	}
	
	private String getPath() {
		
		return context.getRealPath("/WEB-INF/data.txt");
	}
	
	private String getUploadPath() {
		
		return context.getRealPath("/WEB-INF/uploads");
	}
	
	@POST
	@Path("image")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postImage(@FormDataParam("file") InputStream upload, @FormDataParam("file") FormDataContentDisposition detail) {
		
		
		return RestServiceManager.saveImage(upload, detail.getFileName(), getUploadPath());
		
	}
	
	@POST
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response testFile(@FormDataParam("file") InputStream upload, @FormDataParam("file") FormDataContentDisposition detail) {
		System.out.println(detail.getType());
		System.out.println(detail.getFileName());
		return null;
	}
	
	@GET
	@Path("image/{name}")
	public byte[] getImage(@javax.ws.rs.PathParam("name") String name) throws IOException, URISyntaxException {
		return RestServiceManager.getImage(name, getUploadPath());
		
	}
	
	@GET
	@Path("employee/{syskey}")
	@Produces(MediaType.APPLICATION_JSON)
	public EmployeeData getEmployee(@javax.ws.rs.PathParam("syskey") String syskey) {
		
		return RestServiceManager.getEmployee(syskey, getPath());
				
	}
	
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEmployee(EmployeeData data) {
		
		return RestServiceManager.updateEmployee(getPath(), data);
	}
	
	@DELETE
	@Path("deleteemp")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEmployee() {
		
		String syskey = request.getParameter("syskey");
		return RestServiceManager.deleteEmployee(syskey, getPath());
	}
	
	@GET
	@Path("export")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void exportExcel() {
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=emp.xlsx");
		RestServiceManager.exportExcel(getPath(), response);
		
	}
	
	@POST
	@Path("import")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response importExcel(@FormDataParam("file") InputStream io) {
		return RestServiceManager.importExcel(getPath(), io);
	}
	
}
