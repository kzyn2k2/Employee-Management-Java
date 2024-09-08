package com.ta.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.ta.share.Response;

public class ExcelImportException extends WebApplicationException {

	public ExcelImportException() {
		super(javax.ws.rs.core.Response.status(Status.INTERNAL_SERVER_ERROR).build());
	}
}
