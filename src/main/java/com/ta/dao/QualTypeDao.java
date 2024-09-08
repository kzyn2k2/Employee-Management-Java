package com.ta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ta.share.QualTypeData;

public class QualTypeDao {

	public static final String QUALTYPES = "select autokey, name from qualification_type";
	
	public static final String QUALKEY = "select autokey, name from qualification_type where autokey = ?";
	
	public static final String QUALNAME = "select autokey, name from qualification_type where name = ?";
	
	public static List<QualTypeData> getQualTypes(Connection connection) throws SQLException {
		
		List<QualTypeData> qualTypes = new ArrayList<>();
		PreparedStatement stmt = connection.prepareStatement(QUALTYPES);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			QualTypeData q = new QualTypeData();
			q.setAutokey(rs.getInt("autokey"));
			q.setName(rs.getString("name"));
			qualTypes.add(q);
		}
		
		return qualTypes;
	}
	
	
	public static QualTypeData getQualTypeByKey(int key, Connection connection) {
		
		QualTypeData q = new QualTypeData();
		try {
			PreparedStatement stmt = connection.prepareStatement(QUALKEY);
			stmt.setInt(1, key);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				q.setAutokey(rs.getInt("autokey"));
				q.setName(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return q;
	}
	
	public static QualTypeData getQualTypeByName(String name, Connection connection) {
		
		QualTypeData q = new QualTypeData();
		try {
			PreparedStatement stmt = connection.prepareStatement(QUALNAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				q.setAutokey(rs.getInt("autokey"));
				q.setName(rs.getString("name"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return q;
	}
}
