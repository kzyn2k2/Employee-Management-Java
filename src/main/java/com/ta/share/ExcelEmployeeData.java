package com.ta.share;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ExcelEmployeeData extends EmployeeData {

	Map<String, String> cellPair;
	
	public ExcelEmployeeData() {
		
		this.cellPair = new HashMap<>();
	}
	
}
