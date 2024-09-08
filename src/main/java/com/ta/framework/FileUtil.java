package com.ta.framework;

import java.util.*;

import javax.servlet.ServletContext;

import java.text.*;
import java.io.*;


public class FileUtil{

    
    public static String readDataSourceInfo(String path) throws FileNotFoundException, IOException {
    	

    	String res = "";
    	try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
			res = reader.readLine();
		} 
    	return res;
    }
	
}