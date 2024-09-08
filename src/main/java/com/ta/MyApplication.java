package com.ta;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.ta.exceptions.ExcelExceptionMapper;
import com.ta.exceptions.ExcelImportException;
import com.ta.exceptions.ExcelImportExceptionMapper;
import com.ta.exceptions.ImageExceptionMapper;
import com.ta.exceptions.RestExceptionMapper;
import com.ta.exceptions.SQLInjectExceptionMapper;
import com.ta.service.RestService;

public class MyApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(RestExceptionMapper.class);
		classes.add(RestService.class);
		classes.add(ExcelExceptionMapper.class);
		classes.add(ExcelImportExceptionMapper.class);
		classes.add(ImageExceptionMapper.class);
		classes.add(SQLInjectExceptionMapper.class);
		return classes;
	}
	
}
