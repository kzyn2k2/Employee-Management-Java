package com.ta.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SQLInjectExceptionMapper implements ExceptionMapper<SQLInjectException> {

	@Override
	public Response toResponse(SQLInjectException e) {
		return Response.status(Status.BAD_REQUEST).entity(new com.ta.share.Response("The entered input is not SQL safe", false)).build();
	}

}
