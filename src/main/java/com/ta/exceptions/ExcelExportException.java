package com.ta.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ExcelExportException extends WebApplicationException {

	public ExcelExportException() {
		
		super(Response.status(Status.INTERNAL_SERVER_ERROR).build());
		
	}
	
}
