package com.ta.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<SQLConnectException> {


	@Override
	public Response toResponse(SQLConnectException arg0) {
		return Response.status(Status.BAD_REQUEST).entity(new com.ta.share.Response("Cannot get connection. Something went wrong!", false)).build();
	}

}
