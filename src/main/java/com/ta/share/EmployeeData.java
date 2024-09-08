package com.ta.share;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class EmployeeData {
	
	private String syskey;
	private String empId;
	private String name;
	private String fatherName;
	private String address;
	private String email;
	private String mobile;
	private String gender;
	private String nrc;
	private String drivingLicense;
	private String image;
	private Date dob;
	private CityData city;
	private int recordStatus;
	private List<QualificationData> qualifications;

	public EmployeeData() {
		
		this.qualifications = new ArrayList<>();
	}
}
