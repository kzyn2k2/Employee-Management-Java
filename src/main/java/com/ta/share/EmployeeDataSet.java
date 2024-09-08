package com.ta.share;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EmployeeDataSet {

	private String msg;
	private int total;
	private boolean state;
	List<EmployeeData> employees = new ArrayList<>();	
	
}
