package com.ta.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ExcelImportExceptionMapper implements ExceptionMapper<ExcelImportException> {

	@Override
	public Response toResponse(ExcelImportException arg0) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new com.ta.share.Response("Excel import failed!", false)).build();
	}

}
