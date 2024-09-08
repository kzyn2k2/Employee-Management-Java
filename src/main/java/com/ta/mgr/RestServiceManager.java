package com.ta.mgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageReader;
import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;


import com.ta.dao.CityDao;
import com.ta.dao.EmployeeDao;
import com.ta.dao.QualTypeDao;
import com.ta.exceptions.ExcelExportException;
import com.ta.exceptions.ImageException;
import com.ta.exceptions.SQLConnectException;
import com.ta.framework.ConnAdmin;
import com.ta.framework.ExcelProcessor;
import com.ta.share.CityData;
import com.ta.share.EmployeeData;
import com.ta.share.EmployeeDataSet;
import com.ta.share.FormSelectData;
import com.ta.share.ImageResponse;
import com.ta.share.Pager;
import com.ta.share.QualTypeData;
import com.ta.share.Response;

public class RestServiceManager {
	
	
	public static List<CityData> getCities(String path) {
		try(Connection conn = ConnAdmin.getConn(path)) {
			if(conn != null) {
				List<CityData> cityDatas = CityDao.getCities(conn);
				return cityDatas;
			}
		} catch (Exception e) {
			throw new SQLConnectException();
		}
		return null;
	}
	
	public static Response addEmployee(EmployeeData data, String path) {
		
		Response res = new Response();
		try(Connection conn = ConnAdmin.getConn(path)) {
			if(conn != null) {
			 	res = EmployeeDao.addEmployee(data, conn);
			}
		} catch (Exception e) {
			res = new Response("Cannot get connection!", false);
			
		}
		return res;
		
	}
	
	public static EmployeeDataSet getEmployees(String path, Pager pager) {
		EmployeeDataSet data = new EmployeeDataSet();
		try(Connection conn = ConnAdmin.getConn(path)) {
			if(conn != null) {
				data = EmployeeDao.getEmployees(conn, pager);
			}
		} catch (Exception e) {
			throw new SQLConnectException();
		}
		return data;
	}
	
	public static FormSelectData getFormSelectData(String path) {
		
		FormSelectData f = new FormSelectData();
		try(Connection conn = ConnAdmin.getConn(path)){
			if(conn != null) {
				List<CityData> cities = CityDao.getCities(conn);
				List<QualTypeData> qualTypes = QualTypeDao.getQualTypes(conn);
				f.setState(true);
				if(cities.size() != 0) {
					f.setCities(cities);
				}else {
					f.setState(false);
					f.setMessage("Cannot retrive cities");
				}
				
				if(qualTypes.size() != 0) {
					f.setQualTypes(qualTypes);
				}else {
					f.setState(false);
					f.setMessage("Cannot retrive qualification types");
				}
	
			}
		} catch (Exception e) {
			throw new SQLConnectException();
		}
		return f;
	}
	
	public static Response deleteEmployee(String syskey, String path) {
		
		Response r = new Response();
		try(Connection connection = ConnAdmin.getConn(path)) {
			if(connection != null) {
				r = EmployeeDao.deleteEmployee(connection, syskey);
			}

		} catch (Exception e) {
			return new Response("Delete employee's record failed!", false);
		}
		return r;
	}
	
	public static Response saveImage(InputStream upload, String fileName, String path) {
		
		Path uploadPath = Paths.get(path,fileName);
		try {

			Files.copy(upload, uploadPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			return new Response("Saving image failed!", false);
		}
			
		
		return new Response("Success", true);
		
	}
	
	public static void exportExcel(String path, HttpServletResponse response) {
		
		try(Connection conn = ConnAdmin.getConn(path)){
			ExcelProcessor ep = new ExcelProcessor(conn);
			List<EmployeeData> eps = EmployeeDao.getAllEmp(conn);
			ep.exportExcel(response, eps);
		} catch (SQLException | IOException e) {
			throw new ExcelExportException();
		}
		
	}
	
	public static Response importExcel(String path, InputStream io) {
		
		try(Connection conn = ConnAdmin.getConn(path)){
			if(io != null) {
				ExcelProcessor ep = new ExcelProcessor(conn);
				return EmployeeDao.massInsertEmployee(ep.importFromExcel(io), conn);
			}else {
				return new Response("Empty file!", false);
			}
	
		} catch (Exception e) {
			return new Response("Excel import failed!", false);
		}
		
	}
	
	
	public static byte[] getImage(String name, String upload) throws IOException, URISyntaxException {
		 
		
		Path root = Paths.get(upload);
		Path res = null;
		byte[] ret = new byte[4096];
        try {
            if (root.toFile().exists()) {
                Iterator<Path> iterator = Files.newDirectoryStream(root).iterator();
 
                while(iterator.hasNext()) {
                	Path test = iterator.next();
                	if(test.getFileName().toString().equals(name)){
                		res = test;
                	}
                }
            }
        } catch (IOException ie) {
        	throw new ImageException();
        }
        
        if(res != null) {
        	ret = Files.readAllBytes(res);
        }
		return ret;
		
		
	}
	
	public static EmployeeData getEmployee(String syskey, String path) {
		
		EmployeeData e = new EmployeeData();
		try(Connection conn = ConnAdmin.getConn(path)){
			if(conn != null) {
				e = EmployeeDao.getEmployee(syskey, conn);
			}

		} catch (Exception ex) {
			throw new SQLConnectException();
		}
		return e;
	}
	
	public static Response updateEmployee(String path, EmployeeData data) {
		Response r = new Response();
		try(Connection conn = ConnAdmin.getConn(path)){
			if(conn != null) {
				r = EmployeeDao.updateEmployee(conn, data);
			}
		} catch (Exception e) {
			return new Response("Update failed!", false);
		}
		return r;
	}

}
