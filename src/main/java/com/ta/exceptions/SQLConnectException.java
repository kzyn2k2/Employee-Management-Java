package com.ta.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class SQLConnectException extends WebApplicationException {

	public SQLConnectException() {
		super(Response.status(Status.BAD_REQUEST).build());
	}
}
