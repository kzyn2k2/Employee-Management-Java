package com.ta.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ImageExceptionMapper implements ExceptionMapper<ImageException> {

	@Override
	public Response toResponse(ImageException arg0) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new com.ta.share.Response("Image cannot be processed!", false)).build();
	}

	
}
