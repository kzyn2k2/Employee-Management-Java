package com.ta.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ta.framework.ConnAdmin;
import com.ta.framework.KeyGenerator;
import com.ta.share.CityData;
import com.ta.share.EmployeeData;
import com.ta.share.EmployeeDataSet;
import com.ta.share.ExcelEmployeeData;
import com.ta.share.Pager;
import com.ta.share.QualificationData;
import com.ta.share.Response;

public class EmployeeDao {

	public static String INSERT = "insert into Employee (syskey, employee_id, name, father_name,"+
										"address, email, mobile, gender, nrc, driving_license, dob, image,"+
										"record_status, city_key, created_on, modified_on) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	String WHERE = "WHERE e.Record_Status = 1";
		
	public static String TOTAL = "select count(syskey) as total from Employee where record_status = 1";
	
	
	public static final String QUAL = "insert into Qualification (syskey, name, receive_on, created_on, modified_on,"
									+ "record_status, qualtype_key, employee_key) values (?,?,?,?,?,?,?,?)";
	
	public static final String EMPLOYEE = "select  e.SYSKEY, e.EMPLOYEE_ID, e.NAME,e.FATHER_NAME, e.ADDRESS, e.EMAIL, e.MOBILE, e.GENDER, e.NRC, e.DRIVING_LICENSE, e.DOB, "
											+ "e.IMAGE, e.RECORD_STATUS, c.AUTOKEY as CITY_KEY, c.NAME as CITY_NAME, ROW_NUMBER() over (order by e.NAME) as RowNumber from EMPLOYEE e "
											+ "join CITY c on e.CITY_KEY = c.AUTOKEY WHERE e.SYSKEY = ? AND e.RECORD_STATUS = 1";
	
	public static final String QUALEMP = "select * from QUALIFICATION where EMPLOYEE_KEY = ? and RECORD_STATUS = 1";
	
	public static final String UPDATEEMP = "update employee set employee_id = ?, name = ?, father_name = ?, address = ?, email = ?, mobile = ?, gender = ?, nrc = ?,"
											+ "driving_license = ?, dob = ?, image = ?, record_status = ?, city_key = ?, modified_on = ? where syskey = ?";
	
	
	public static final String UPDATEQUAL = "update qualification set name = ?, receive_on = ?, modified_on = ?, record_status = ?, qualtype_key = ? where syskey = ?";
	
	public static final String DELEMP = "update employee set record_status = 4 where syskey = ?";
	
	public static final String ALLEMP = "select  e.SYSKEY, e.EMPLOYEE_ID, e.NAME,e.FATHER_NAME, e.ADDRESS, e.EMAIL, e.MOBILE, e.GENDER, e.NRC, e.DRIVING_LICENSE, e.DOB, e.RECORD_STATUS, e.IMAGE, "
										+ "c.AUTOKEY as CITY_KEY, c.NAME as CITY_NAME, ROW_NUMBER() over (order by e.NAME) as RowNumber from EMPLOYEE e join CITY c on e.CITY_KEY = c.AUTOKEY";
	
	
	public static String CHECK = "select count(syskey) as stat, e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE from employee e where e.EMPLOYEE_ID = ? or e.EMAIL = ? or e.MOBILE = ? or e.NRC = ?";
	
	
	public static Response addEmployee(EmployeeData data, Connection connection) {
		
		try {
			String where = "where e.EMPLOYEE_ID = ? or e.EMAIL = ? or e.MOBILE = ? or e.NRC = ?";
			if(data.getDrivingLicense() != "") {
				where += " or e.DRIVING_LICENSE = ?";
			}
			String ck = "select count(syskey) as stat, e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE from employee e "+where
						+" group by e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE";
			
			PreparedStatement check = connection.prepareStatement(ck);
			check.setString(1, data.getEmpId());
			check.setString(2, data.getEmail());
			check.setString(3, data.getMobile());
			check.setString(4, data.getNrc());
			if(data.getDrivingLicense() != "") {
				
				check.setString(5, data.getDrivingLicense());			
				
			}
			ResultSet status = check.executeQuery();
			while(status.next()) {
				
				if(status.getInt("stat") > 0) {
					
					if(status.getString("employee_id").equalsIgnoreCase(data.getEmpId())) {
						return new Response("Duplicate Empid", false);
					}
					if(status.getString("email").equalsIgnoreCase(data.getEmail())) {
						return new Response("Duplicate Email", false);
					}
					if(status.getString("mobile").equalsIgnoreCase(data.getMobile())) {
						return new Response("Duplicate Mobile", false);
					}
					if(status.getString("nrc").equalsIgnoreCase(data.getNrc())) {
						return new Response("Duplicate NRC", false);
					}
					if(status.getString("driving_license").equalsIgnoreCase(data.getDrivingLicense())) {
						return new Response("Duplicate DL", false);
					}
				}
				
			}
			
			PreparedStatement stmt = connection.prepareStatement(INSERT);
			data.setSyskey(KeyGenerator.getKey());
			stmt.setString(1, data.getSyskey());
			stmt.setString(2, data.getEmpId());
			stmt.setString(3, data.getName());
			stmt.setString(4, data.getFatherName());
			stmt.setString(5, data.getAddress());
			stmt.setString(6, data.getEmail());
			stmt.setString(7, data.getMobile());
			stmt.setString(8, data.getGender());
			stmt.setString(9, data.getNrc());
			stmt.setString(10, data.getDrivingLicense());
			stmt.setDate(11, new Date(data.getDob().getTime()));
			stmt.setString(12, data.getImage());
			stmt.setInt(13, 1);
			stmt.setInt(14, data.getCity().getAutokey());
			stmt.setDate(15, Date.valueOf(LocalDate.now()));
			stmt.setDate(16, Date.valueOf(LocalDate.now()));
			int res = stmt.executeUpdate();
			if(res == 1) {
				if(data.getQualifications().size() != 0) {
					for(QualificationData qual : data.getQualifications()) {
						PreparedStatement qualstmt = connection.prepareStatement(QUAL);
						qualstmt.setString(1, KeyGenerator.getKey());
						qualstmt.setString(2, qual.getName());
						qualstmt.setInt(3, qual.getYear());
						qualstmt.setDate(4, Date.valueOf(LocalDate.now()));
						qualstmt.setDate(5, Date.valueOf(LocalDate.now()));
						qualstmt.setInt(6, 1);
						qualstmt.setInt(7, qual.getQualType());
						qualstmt.setString(8, data.getSyskey());
						qualstmt.executeUpdate();
					}
				}
				return new Response(ck, true);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return new Response("Failure", false);
		}
			
		return null;
	}
	
	public static EmployeeDataSet getEmployees(Connection connection, Pager pager) {
		
		String WHERE = "WHERE e.Record_Status = 1";
		
		if(pager.getSearchVal().equalsIgnoreCase("") == false) {
			WHERE += "and e.name like '%"+pager.getSearchVal()+"%' or e.EMAIL = '"+pager.getSearchVal()+"' or e.EMPLOYEE_ID = '"+pager.getSearchVal()+"'";
		}
		
		String employees = "select * from (select  e.SYSKEY, e.EMPLOYEE_ID, e.NAME,e.FATHER_NAME, e.ADDRESS, e.EMAIL, "
				+ "e.MOBILE, e.GENDER, e.NRC, e.DRIVING_LICENSE, e.DOB, e.IMAGE, e.RECORD_STATUS, c.AUTOKEY as CITY_KEY, "
				+ "c.NAME as CITY_NAME, ROW_NUMBER() over (order by e.EMPLOYEE_ID) as RowNumber "
				+ "from EMPLOYEE e join CITY c on e.CITY_KEY = c.AUTOKEY "+WHERE+") AS NumberedRows where RowNumber between ? and ? order by RowNumber";
		
		EmployeeDataSet data = new EmployeeDataSet();
		List<EmployeeData> res = new ArrayList<>();
		try {
			PreparedStatement stmt = connection.prepareStatement(employees);
			int start = (pager.getCurrent() - 1) * pager.getSize() + 1;
			int end = pager.getCurrent() * pager.getSize();
			stmt.setInt(1, start);
			stmt.setInt(2, end);
			ResultSet result = stmt.executeQuery();
			while(result.next()) {
				EmployeeData e = new EmployeeData();
				CityData c = new CityData();
				e.setSyskey(result.getString("syskey"));
				e.setEmpId(result.getString("employee_id"));
				e.setName(result.getString("name"));
				e.setFatherName(result.getString("father_name"));
				e.setAddress(result.getString("address"));
				e.setEmail(result.getString("email"));
				e.setMobile(result.getString("mobile"));
				e.setGender(result.getString("gender"));
				e.setNrc(result.getString("nrc"));
				e.setDrivingLicense(result.getString("driving_license"));
				e.setDob(result.getDate("dob"));
				e.setRecordStatus(result.getInt("record_status"));
				e.setImage(result.getString("image"));
				c.setAutokey(result.getInt("city_key"));
				c.setName(result.getString("city_name"));
				e.setCity(c);
				res.add(e);
			}
			if(res.size() > 0) {
				data.setEmployees(res);
				data.setState(true);
				if(pager.getSearchVal().equalsIgnoreCase("") == false) {
					String TOTALSEARCH = "select count(syskey) as total from Employee where record_status = 1 and name like '%"+pager.getSearchVal()+"%' or EMAIL = '"+pager.getSearchVal()+"' or EMPLOYEE_ID = '"+pager.getSearchVal()+"'";
					PreparedStatement total = connection.prepareStatement(TOTALSEARCH);
					ResultSet totalRes = total.executeQuery();
					if(totalRes.next()) {
						data.setTotal(totalRes.getInt("total"));
					}	
				}else {
					PreparedStatement total = connection.prepareStatement(TOTAL);
					ResultSet totalRes = total.executeQuery();
					if(totalRes.next()) {
						data.setTotal(totalRes.getInt("total"));
					}	
				}
				return data;
			}else {
				data.setState(false);
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			data.setState(false);
			return data;
		}
}
	
	public static EmployeeData getEmployee(String syskey, Connection conn) throws SQLException {
		
			PreparedStatement stmt = conn.prepareStatement(EMPLOYEE);
			stmt.setString(1, syskey);
			ResultSet result = stmt.executeQuery();
			while(result.next()) {
				EmployeeData e = new EmployeeData();
				CityData c = new CityData();
				e.setSyskey(result.getString("syskey"));
				e.setEmpId(result.getString("employee_id"));
				e.setName(result.getString("name"));
				e.setFatherName(result.getString("father_name"));
				e.setAddress(result.getString("address"));
				e.setEmail(result.getString("email"));
				e.setMobile(result.getString("mobile"));
				e.setGender(result.getString("gender"));
				e.setNrc(result.getString("nrc"));
				e.setDrivingLicense(result.getString("driving_license"));
				e.setDob(result.getDate("dob"));
				e.setRecordStatus(result.getInt("record_status"));
				e.setImage(result.getString("image"));
				c.setAutokey(result.getInt("city_key"));
				c.setName(result.getString("city_name"));
				e.setCity(c);
				PreparedStatement qualstmt = conn.prepareStatement(QUALEMP);
				qualstmt.setString(1, syskey);
				ResultSet rs = qualstmt.executeQuery();
				while(rs.next()) {
					QualificationData q = new QualificationData();
					q.setName(rs.getString("name"));
					q.setSyskey(rs.getString("syskey"));
					q.setYear(rs.getInt("receive_on"));
					q.setQualType(rs.getInt("qualtype_key"));
					q.setStatus(rs.getInt("record_status"));
					e.getQualifications().add(q);
				}
				return e;
			}
		return null;
	}
	
	public static Response updateEmployee(Connection connection, EmployeeData data) throws SQLException {
		
			
			String where = "where e.SYSKEY != ? AND (e.EMPLOYEE_ID = ? or e.EMAIL = ? or e.MOBILE = ? or e.NRC = ?";
			if(data.getDrivingLicense() != "") {
				where += " or e.DRIVING_LICENSE = ?)";
			}else {
				where += ")";
			}
			String ck = "select count(syskey) as stat, e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE from employee e "+where
						+" group by e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE";
			
			PreparedStatement check = connection.prepareStatement(ck);
			check.setString(1, data.getSyskey());
			check.setString(2, data.getEmpId());
			check.setString(3, data.getEmail());
			check.setString(4, data.getMobile());
			check.setString(5, data.getNrc());
			if(data.getDrivingLicense() != "") {
				
				check.setString(6, data.getDrivingLicense());			
				
			}
			ResultSet status = check.executeQuery();
			while(status.next()) {
				
				if(status.getInt("stat") > 0) {
					
					if(status.getString("employee_id").equalsIgnoreCase(data.getEmpId())) {
						return new Response("Duplicate Empid", false);
					}
					if(status.getString("email").equalsIgnoreCase(data.getEmail())) {
						return new Response("Duplicate Email", false);
					}
					if(status.getString("mobile").equalsIgnoreCase(data.getMobile())) {
						return new Response("Duplicate Mobile", false);
					}
					if(status.getString("nrc").equalsIgnoreCase(data.getNrc())) {
						return new Response("Duplicate NRC", false);
					}
					if(status.getString("driving_license").equalsIgnoreCase(data.getDrivingLicense())) {
						return new Response("Duplicate DL", false);
					}
				}
				
			}
			
			PreparedStatement stmt = connection.prepareStatement(UPDATEEMP);
			
			stmt.setString(1, data.getEmpId());
			stmt.setString(2, data.getName());
			stmt.setString(3, data.getFatherName());
			stmt.setString(4, data.getAddress());
			stmt.setString(5, data.getEmail());
			stmt.setString(6, data.getMobile());
			stmt.setString(7, data.getGender());
			stmt.setString(8, data.getNrc());
			stmt.setString(9, data.getDrivingLicense());
			stmt.setDate(10, new Date(data.getDob().getTime()));
			stmt.setString(11, data.getImage());
			stmt.setInt(12, 1);
			stmt.setInt(13, data.getCity().getAutokey());
			stmt.setDate(14, Date.valueOf(LocalDate.now()));
			stmt.setString(15, data.getSyskey());
			int res = stmt.executeUpdate();
			if(res == 1) {
				for(QualificationData q : data.getQualifications()) {
					
					if(q.getSyskey() != null && q.getStatus() == 4) {
						PreparedStatement qstmt = connection.prepareStatement(UPDATEQUAL);
						qstmt.setString(1, q.getName());
						qstmt.setInt(2, q.getYear());
						qstmt.setDate(3, Date.valueOf(LocalDate.now()));
						qstmt.setInt(4, q.getStatus());
						qstmt.setInt(5, q.getQualType());
						qstmt.setString(6, q.getSyskey());
						qstmt.executeUpdate();
					}else if(q.getSyskey() == null && q.getStatus() == 1) {
						PreparedStatement qualstmt = connection.prepareStatement(QUAL);
						qualstmt.setString(1, KeyGenerator.getKey());
						qualstmt.setString(2, q.getName());
						qualstmt.setInt(3, q.getYear());
						qualstmt.setDate(4, Date.valueOf(LocalDate.now()));
						qualstmt.setDate(5, Date.valueOf(LocalDate.now()));
						qualstmt.setInt(6, 1);
						qualstmt.setInt(7, q.getQualType());
						qualstmt.setString(8, data.getSyskey());
						qualstmt.executeUpdate();
					}
					
				}
			}
		
		
		return new Response("Success", true);
	}
	
	
	public static Response deleteEmployee(Connection conn, String syskey) {
		try {
			PreparedStatement stmt = conn.prepareStatement(DELEMP);
			stmt.setString(1, syskey);
			int res = stmt.executeUpdate();
			if(res == 1) {
				return new Response("Success", true);
			}
			return new Response("failure", false);
		} catch (SQLException e) {
			e.printStackTrace();
			return new Response("failure", false);
		}
	}
	
	public static List<EmployeeData> getAllEmp(Connection connection) throws SQLException {
		
		List<EmployeeData> res = new ArrayList<>();
		
			PreparedStatement stmt = connection.prepareStatement(ALLEMP);
			ResultSet result = stmt.executeQuery();
			while(result.next()) {
				EmployeeData e = new EmployeeData();
				CityData c = new CityData();
				e.setSyskey(result.getString("syskey"));
				e.setEmpId(result.getString("employee_id"));
				e.setName(result.getString("name"));
				e.setFatherName(result.getString("father_name"));
				e.setAddress(result.getString("address"));
				e.setEmail(result.getString("email"));
				e.setMobile(result.getString("mobile"));
				e.setGender(result.getString("gender"));
				e.setNrc(result.getString("nrc"));
				e.setDrivingLicense(result.getString("driving_license"));
				e.setDob(result.getDate("dob"));
				e.setRecordStatus(result.getInt("record_status"));
				e.setImage(result.getString("image"));
				c.setAutokey(result.getInt("city_key"));
				c.setName(result.getString("city_name"));
				e.setCity(c);
				PreparedStatement qualstmt = connection.prepareStatement(QUALEMP);
				qualstmt.setString(1, e.getSyskey());
				ResultSet rs = qualstmt.executeQuery();
				while(rs.next()) {
					QualificationData q = new QualificationData();
					q.setName(rs.getString("name"));
					q.setSyskey(rs.getString("syskey"));
					q.setYear(rs.getInt("receive_on"));
					q.setQualType(rs.getInt("qualtype_key"));
					q.setStatus(rs.getInt("record_status"));
					e.getQualifications().add(q);
				}
				res.add(e);
			}
		return res;
	}
	
	private static int convertBool(boolean active) {
		if(active == true) {
			return 1;
		}else {
			return 0;
		}
	}
	
	public static Response massInsertEmployee(List<ExcelEmployeeData> employees, Connection connection) {
		
		for(ExcelEmployeeData data : employees) {
			
			try {
				
				String where = "where e.EMPLOYEE_ID = ? or e.EMAIL = ? or e.MOBILE = ? or e.NRC = ?";
				if(data.getDrivingLicense() != "") {
					where += " or e.DRIVING_LICENSE = ?";
				}
				String ck = "select count(syskey) as stat, e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE from employee e "+where
							+" group by e.EMPLOYEE_ID, e.EMAIL, e.MOBILE, e.NRC, e.DRIVING_LICENSE";
				
				PreparedStatement check = connection.prepareStatement(ck);
				check.setString(1, data.getEmpId());
				check.setString(2, data.getEmail());
				check.setString(3, data.getMobile());
				check.setString(4, data.getNrc());
				if(data.getDrivingLicense() != "") {
					
					check.setString(5, data.getDrivingLicense());			
					
				}
				ResultSet status = check.executeQuery();
				while(status.next()) {
					
					if(status.getInt("stat") > 0) {
						
						if(status.getString("employee_id").equalsIgnoreCase(data.getEmpId())) {
							return new Response("Duplicate Empid in "+data.getCellPair().get("empid"), false);
						}
						if(status.getString("email").equalsIgnoreCase(data.getEmail())) {
							return new Response("Duplicate Email in "+data.getCellPair().get("email"), false);
						}
						if(status.getString("mobile").equalsIgnoreCase(data.getMobile())) {
							return new Response("Duplicate Mobile in "+data.getCellPair().get("mobile"), false);
						}
						if(status.getString("nrc").equalsIgnoreCase(data.getNrc())) {
							return new Response("Duplicate NRC in "+data.getCellPair().get("nrc"), false);
						}
						if(status.getString("driving_license").equalsIgnoreCase(data.getDrivingLicense())) {
							return new Response("Duplicate DL in "+data.getCellPair().get("dl"), false);
						}
					}
					
				}
				
				PreparedStatement stmt = connection.prepareStatement(INSERT);
				data.setSyskey(KeyGenerator.getKey());
				stmt.setString(1, data.getSyskey());
				stmt.setString(2, data.getEmpId());
				stmt.setString(3, data.getName());
				stmt.setString(4, data.getFatherName());
				stmt.setString(5, data.getAddress());
				stmt.setString(6, data.getEmail());
				stmt.setString(7, data.getMobile());
				stmt.setString(8, data.getGender());
				stmt.setString(9, data.getNrc());
				stmt.setString(10, data.getDrivingLicense());
				stmt.setDate(11, new Date(data.getDob().getTime()));
				stmt.setString(12, "");
				stmt.setInt(13, 1);
				stmt.setInt(14, data.getCity().getAutokey());
				stmt.setDate(15, Date.valueOf(LocalDate.now()));
				stmt.setDate(16, Date.valueOf(LocalDate.now()));
				int res = stmt.executeUpdate();
				if(res == 1) {
					if(data.getQualifications().size() != 0) {
						for(QualificationData qual : data.getQualifications()) {
							PreparedStatement qualstmt = connection.prepareStatement(QUAL);
							qualstmt.setString(1, KeyGenerator.getKey());
							qualstmt.setString(2, qual.getName());
							qualstmt.setInt(3, qual.getYear());
							qualstmt.setDate(4, Date.valueOf(LocalDate.now()));
							qualstmt.setDate(5, Date.valueOf(LocalDate.now()));
							qualstmt.setInt(6, 1);
							qualstmt.setInt(7, qual.getQualType());
							qualstmt.setString(8, data.getSyskey());
							qualstmt.executeUpdate();
						}
					}
					
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				return new Response("Failure", false);
			}			
		}
		return new Response("Success", true);
	}
}
