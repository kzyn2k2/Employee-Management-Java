package com.ta.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExcelExceptionMapper implements ExceptionMapper<ExcelExportException> {

	@Override
	public Response toResponse(ExcelExportException e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new com.ta.share.Response("Excel export failed!", false)).build();
	}

}