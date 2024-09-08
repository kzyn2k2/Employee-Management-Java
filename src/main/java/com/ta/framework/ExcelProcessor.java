package com.ta.framework;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ta.dao.CityDao;
import com.ta.dao.QualTypeDao;
import com.ta.share.EmployeeData;
import com.ta.share.ExcelEmployeeData;
import com.ta.share.ExcelQualificationData;
import com.ta.share.QualificationData;



public class ExcelProcessor {

	private Connection connection;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private XSSFSheet sheet2;
	
	public ExcelProcessor(Connection connection) {
		this.workbook = new XSSFWorkbook();
		this.connection = connection;

	}
	
	public void exportExcel(HttpServletResponse response, List<EmployeeData> employees) throws IOException {
	
		writeHeader();
		writeData(employees);
		
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
		
	}
	
	private void writeHeader() {
		
		sheet = workbook.createSheet("Employees");
		sheet.setColumnWidth(0, 16 * 256);
		sheet.setColumnWidth(1, 35 * 256);
		sheet.setColumnWidth(2, 35 * 256);
		sheet.setColumnWidth(3, 55 * 256);
		sheet.setColumnWidth(4, 19 * 256);
		sheet.setColumnWidth(5, 30 * 256);
		sheet.setColumnWidth(6, 20 * 256);
		sheet.setColumnWidth(7, 10 * 256);
		sheet.setColumnWidth(8, 14 * 256);
		sheet.setColumnWidth(9, 20 * 256);
		sheet.setColumnWidth(10, 16 * 256);
		sheet.setColumnWidth(11, 16 * 256);
		sheet2 = workbook.createSheet("Qualifications");
		sheet2.setColumnWidth(0, 35 * 256);
		sheet2.setColumnWidth(1, 22 * 256);
		sheet2.setColumnWidth(2, 40 * 256);
		sheet2.setColumnWidth(3, 15 * 256);
		sheet2.setColumnWidth(4, 16 * 256);
		
		Row row = sheet.createRow(0);
		Row row2 = sheet2.createRow(0);
		
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(15);
		style.setFont(font);
	
		createCell(row, 0, style, "Employee Id");
		createCell(row, 1, style, "Name");
		createCell(row, 2, style, "Father Name");
		createCell(row, 3, style, "Address");
		createCell(row, 4, style, "Driving License");
		createCell(row, 5, style, "Email");
		createCell(row, 6, style, "City");
		createCell(row, 7, style, "Gender");
		createCell(row, 8, style, "Mobile");
		createCell(row, 9, style, "NRC");
		createCell(row, 10, style, "Date of birth");
		
		createCell(row2, 0, style, "Employee ID");
		createCell(row2, 1, style, "Employee");
		createCell(row2, 2, style, "Qualification Type");
		createCell(row2, 3, style, "Qualification Name");
		createCell(row2, 4, style, "Receive On");
		
	}
	
	private void createCell(Row row, int columnNumber, CellStyle style, String val) {
		

		Cell cell = row.createCell(columnNumber);
		cell.setCellValue(val);
		cell.setCellStyle(style);
		
	}

	private void writeData(List<EmployeeData> employees) {
		
		int rowNum = 1;
		int row2Num = 1;
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(12);
		style.setFont(font);
		
		for(EmployeeData e : employees) {
			
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;

			createCell(row, colNum++, style, e.getEmpId());
			createCell(row, colNum++, style, e.getName());
			createCell(row, colNum++, style, e.getFatherName());
			createCell(row, colNum++, style, e.getAddress());
			createCell(row, colNum++, style, e.getDrivingLicense());
			createCell(row, colNum++, style, e.getEmail());
			createCell(row, colNum++, style, e.getCity().getName());
			createCell(row, colNum++, style, e.getGender());
			createCell(row, colNum++, style, e.getMobile());
			createCell(row, colNum++, style, e.getNrc());
			createCell(row, colNum++, style, e.getDob().toString().split("\\s+")[0]);
			
			if(!e.getQualifications().isEmpty()) {
				
				for(QualificationData q : e.getQualifications()) {
					
					Row row2 = sheet2.createRow(row2Num++);
					int col2Num = 0;
					createCell(row2, col2Num++, style, e.getEmpId());
					createCell(row2, col2Num++, style, e.getName());
					createCell(row2, col2Num++, style, QualTypeDao.getQualTypeByKey(q.getQualType(), connection).getName());
					createCell(row2, col2Num++, style, q.getName());
					createCell(row2, col2Num++, style, Integer.toString(q.getYear()));
				
				}
			}
		}
	}
	
	
	
	public String getCell(int row, int cell) {
		
		char c = convertCell(cell);
		String rowText = Integer.toString(row);
		
		return c+rowText;
	}
	
	public List<ExcelEmployeeData> importFromExcel(InputStream io) throws IOException, ParseException {
		
		workbook = new XSSFWorkbook(io);
		sheet = workbook.getSheet("Employees");
		List<ExcelQualificationData> quals = convertToQuals();
		
		List<ExcelEmployeeData> res = new ArrayList<>();

		
		Iterator<Row> rows = sheet.iterator();
		
		int row = 0;
		
		Map<String, List<QualificationData>> map = getSpecificQualifications(quals);
		
		while(rows.hasNext()) {
			
			Row current = rows.next();
			
			if(row == 0) {
				row++;
				continue;	
			}
			
			
			Iterator<Cell> cells = current.cellIterator();
			ExcelEmployeeData e = new ExcelEmployeeData();
			int cell = 0;
			
			while(cells.hasNext()) {
				Cell currentCell = cells.next();
				if(currentCell.getStringCellValue() == "" && cell != 4) {
				}
				switch (cell) {
				case 0: 
					e.getCellPair().put("empid", getCell(row+1, cell));
					e.setEmpId(currentCell.getStringCellValue());
					break;
				case 1:
					e.getCellPair().put("name", getCell(row+1, cell));
					e.setName(currentCell.getStringCellValue());
					break;
				case 2:
					e.getCellPair().put("fname", getCell(row+1, cell));
					e.setFatherName(currentCell.getStringCellValue());
					break;
				case 3:
					e.getCellPair().put("address", getCell(row+1, cell));
					e.setAddress(currentCell.getStringCellValue());
					break;
				case 4:
					e.getCellPair().put("dl", getCell(row+1, cell));
					e.setDrivingLicense(currentCell.getStringCellValue());
					break;
				case 5:
					e.getCellPair().put("email", getCell(row+1, cell));
					e.setEmail(currentCell.getStringCellValue());
					break;
				case 6:
					e.getCellPair().put("cid", getCell(row+1, cell));
					e.setCity(CityDao.getCityByName(connection, currentCell.getStringCellValue()));;
					break;
				case 7:
					e.getCellPair().put("gender", getCell(row+1, cell));
					e.setGender(currentCell.getStringCellValue());
					break;
				case 8:
					e.getCellPair().put("mobile", getCell(row+1, cell));
					e.setMobile(currentCell.getStringCellValue());
					break;
				case 9:
					e.getCellPair().put("nrc", getCell(row+1, cell));
					e.setNrc(currentCell.getStringCellValue());
					break;
				case 10:
					e.getCellPair().put("dob", getCell(row+1, cell));
					String date = currentCell.getStringCellValue().split("\\s+")[0];
					e.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(date));
					break;
					
			}
				
				cell++;
		}
			if(map.containsKey(e.getEmpId())) {
				e.setQualifications(map.get(e.getEmpId()));
			}
			res.add(e);
			row++;
		}
		
		
		return res;
	}
	
	private List<ExcelQualificationData> convertToQuals() throws IOException {
		sheet2 = workbook.getSheet("Qualifications");
		List<ExcelQualificationData> quals = new ArrayList<>();
		Iterator<Row> rows2 = sheet2.iterator();
		int row2 = 0;
		while(rows2.hasNext()) {
			Row current = rows2.next();
			if(row2 == 0) {
				row2++;
				continue;
			}
			Iterator<Cell> cells = current.cellIterator();
			ExcelQualificationData qualform = new ExcelQualificationData();
			int cell = 0;
			while(cells.hasNext()) {
				Cell currentCell = cells.next();
				if(cell == 0) {
					cell++;
					continue;
				}
				switch (cell) {
				case 1:
					qualform.setQualType(QualTypeDao.getQualTypeByName(currentCell.getStringCellValue(), connection).getAutokey());
					break;
				case 2:
					qualform.setName(currentCell.getStringCellValue());
					break;
				case 3:
					qualform.setYear((int) currentCell.getNumericCellValue());
					break;
				case 4:
					qualform.setEmpid(currentCell.getStringCellValue());
					break;
				}
				cell++;
			}
			qualform.setStatus(1);
			quals.add(qualform);
		}
		return quals;	
	}
	
	private Map<String, List<QualificationData>> getSpecificQualifications(List<ExcelQualificationData> forms) {
		
		Map<String, List<QualificationData>> res = new HashMap<>();
	
		for(ExcelQualificationData f : forms) {
			
			if(!res.containsKey(f.getEmpid())) {
				res.put(f.getEmpid(), new ArrayList<QualificationData>());
			}
			QualificationData q = new QualificationData();
			q.setName(f.getName());
			q.setQualType(f.getQualType());
			q.setStatus(1);
			q.setSyskey(null);
			q.setYear(f.getYear());
			res.get(f.getEmpid()).add(q);
			
		}
	return res;
	}
	
	
	public char convertCell(int cell) {
		
		switch (cell) {
        case 0: return 'A';
        case 1: return 'B';
        case 2: return 'C';
        case 3: return 'D';
        case 4: return 'E';
        case 5: return 'F';
        case 6: return 'G';
        case 7: return 'H';
        case 8: return 'I';
        case 9: return 'J';
        case 10: return 'K';
        case 11: return 'L';
        case 12: return 'M';
        case 13: return 'N';
        case 14: return 'O';
        case 15: return 'P';
        case 16: return 'Q';
        case 17: return 'R';
        case 18: return 'S';
        case 19: return 'T';
        case 20: return 'U';
        case 21: return 'V';
        case 22: return 'W';
        case 23: return 'X';
        case 24: return 'Y';
        case 25: return 'Z';
    }
		return 0;
	}
	
}
