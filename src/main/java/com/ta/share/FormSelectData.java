package com.ta.share;

import java.util.List;

import lombok.Data;

@Data
public class FormSelectData {

	private String message;
	private boolean state;
	private List<CityData> cities;
	private List<QualTypeData> qualTypes;
	
}
