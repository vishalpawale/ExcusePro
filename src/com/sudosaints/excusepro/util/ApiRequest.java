package com.sudosaints.excusepro.util;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class ApiRequest implements Serializable {
	
	public enum RequestMethod {GET, POST, POST_RAW, FILE_UPLOAD};
	public enum RequestName {
		GET_CATEGORIES,
		GET_EXCUSES
	}
	
	RequestName requestName;
	RequestMethod requestMethod;
	String url;
	Properties reqParams = new Properties();
	
	//Used for FileName
	private String fileName;
	
	private boolean isUrlAbsolute = false; 
	private boolean useBasicAuth = false;
	
	private String[] pathVariables;
	
	private InputStream postDataStream;
	private File postFile;
	private int apiVersion;
	
	public ApiRequest() {
		apiVersion = 1;
		requestMethod = RequestMethod.GET;
	}
	
	public ApiRequest(RequestName reqName, String[] pathVariables) {	
		this();
		this.pathVariables = pathVariables;
		setRequestName(reqName);
	}
		
	public ApiRequest setParam (String name, String value) {
		if (null!=value) {
			reqParams.setProperty(name, value);
		}
		return this;
	}
	
	public Properties getParams() {
		return reqParams;
	}
	
	public ApiRequest setUrl (String url) {
		this.url = url;
		return this;
	}

	public ApiRequest setRequestMethod (RequestMethod method) {
		this.requestMethod = method;
		return this;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public String getUrl() {
		return url;
	}

	public RequestName getRequestName() {
		return requestName;
	}

	public ApiRequest setRequestName(RequestName requestName) {
		this.requestName = requestName;
		switch (requestName) {
		
		case GET_CATEGORIES:
			url = "/api/getCategories";
			break;
			
		case GET_EXCUSES:
			url = "/api/getExcuses";
			break;
        default:
			break;
		}

		return this;
	}

	public boolean isUrlAbsolute() {
		return isUrlAbsolute;
	}

	public ApiRequest setUrlAbsolute(boolean isUrlAbsolute) {
		this.isUrlAbsolute = isUrlAbsolute;
		return this;
	}

	public InputStream getPostDataStream() {
		return postDataStream;
	}

	public ApiRequest setPostDataStream(InputStream postDataStream) {
		this.postDataStream = postDataStream;
		return this;
	}

	public File getPostFile() {
		return postFile;
	}

	public ApiRequest setPostFile(File postFile) {
		this.postFile = postFile;
		return this;
	}
	
	public int getApiVersion() {
		return apiVersion;
	}

	public boolean useBasicAuth() {
		return useBasicAuth;
	}

	public void useBasicAuth(boolean useBasicAuth) {
		this.useBasicAuth = useBasicAuth;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
