package com.ta.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import com.ta.framework.FileUtil;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.nirvasoft.database.ConnMgr;

import password.DESedeEncryption;

public class ConnAdmin {

	public ConnAdmin() {
		super();
	}

	public static String servername = "";
	public static String port = "";
	public static String dbname = "";
	public static String dbUsr = "";
	public static String dbPwd = "";
	public static String connType = "";
	static String path = "";
	static String url="";

	public static Connection getConn(String path) throws FileNotFoundException, IOException {

		Connection conn = null;
		readConnectionString(path);
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setUser(dbUsr);
		ds.setPassword(dbPwd);
		ds.setServerName(servername);
		ds.setPortNumber(Integer.parseInt(port));
		ds.setDatabaseName(dbname);
		ds.setTrustServerCertificate(true);
		try {
			conn = ds.getConnection();
		} catch (SQLServerException e) {
			e.printStackTrace();
		}
		return conn;
	}


	private static void readConnectionString(String val) throws FileNotFoundException, IOException {
		
		String res = "";

			res = FileUtil.readDataSourceInfo(val);
		
		String[] l_split = res.split(",");
		if(l_split.length != 0) {
			servername = l_split[0];
			port = l_split[1];
			dbname = l_split[2];
			dbUsr = l_split[3];
			dbPwd = l_split[4];
			connType = l_split[5];
		}
	
	}
	
	public static String decryptPIN(String p) {
		String ret = "";
		try {
			DESedeEncryption myEncryptor = new DESedeEncryption();
			ret = myEncryptor.decrypt(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}






}
