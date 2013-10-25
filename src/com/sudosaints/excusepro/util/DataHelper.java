package com.sudosaints.excusepro.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.os.Environment;

public class DataHelper {

	private Context context;

	private CommonUtil commonUtil;
	
	public DataHelper(Context context) {
		super();
		this.context = context;
		this.commonUtil = new CommonUtil(context);
	}
	
	public void createDataDirIfNotExists() {
		if (isExtStorageAvailable()) {
			File root = Environment.getExternalStorageDirectory();
			String path = root.getAbsolutePath()+"/icardwala/data/";
			File dataDir = new File(path);
			if (!(dataDir.exists() && dataDir.isDirectory())) {
				dataDir.mkdirs();
			}
		}
	}
	
	public String getDataDir() {
		File root = Environment.getExternalStorageDirectory();
		String path = root.getAbsolutePath()+"/icardwala/data";
		return path;
	}
	
	public static boolean isExtStorageAvailable() {
		String state = Environment.getExternalStorageState();
	    return Environment.MEDIA_MOUNTED.equals(state);
	}
	
	public boolean inputDataFilesExist() {
		if (isExtStorageAvailable()) {
			File root = Environment.getExternalStorageDirectory();
			String path = root.getAbsolutePath()+"/icardwala/input/";		
			File sessionFile = new File(path);		
			File userFile = new File(path);		
			File statusFile = new File(path);		
			if (sessionFile.exists() && userFile.exists() && statusFile.exists()) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getCsvFileContentsAsString (File file) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = br.readLine()) != null) {
				String[] vals = str.split(",");
				for (String val : vals) {
					sb.append(commonUtil.decodeString(val)).append(",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	public boolean checkFiles() {
		if (!isExtStorageAvailable()) {
			return false;
		}
		
		File root = Environment.getExternalStorageDirectory();
		String path = root.getAbsolutePath()+"/icardwala/output/";
		File outputDir = new File(path);
		if(outputDir.list().length == 0){
			return false;
		}
		return true;
	}
	
	public ResultStatus copyBackgroundFile(String fromPath) {
		ResultStatus resultStatus = new ResultStatus(true);
		String toPath = getDataDir() + "/background";
		File toFile = new File(toPath);
		File fromFile = new File(fromPath);
		try {
			FileUtils.copyFile(fromFile, toFile);
		} catch (IOException e) {
			e.printStackTrace();
			resultStatus.setSuccess(false);
			resultStatus.setStatusMessage("Unable to copy background file");
		}
		return resultStatus;
	}
}
