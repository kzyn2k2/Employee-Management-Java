package com.ta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ta.share.CityData;
import com.ta.share.EmployeeData;

public class CityDao {

	
	public static final String CITIES = "select * from city";
	
	public static final String CITY = "select autokey, name from city where name = ?";
	
	
	public static List<CityData> getCities(Connection connection) throws SQLException {
		
		PreparedStatement stmt = connection.prepareStatement(CITIES);
		ResultSet rs = stmt.executeQuery();
		List<CityData> cityDatas = new ArrayList<>();
		while(rs.next()) {
			CityData c = new CityData();
			c.setAutokey(rs.getInt("autokey"));
			c.setName(rs.getString("name"));
			cityDatas.add(c);
		}
		return cityDatas;
	}
	
	public static CityData getCityByName(Connection connection, String name) {
	
		CityData c = new CityData();
		try {
			PreparedStatement stmt = connection.prepareStatement(CITY);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				
				c.setAutokey(rs.getInt("autokey"));
				c.setName(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	
	
	
}
