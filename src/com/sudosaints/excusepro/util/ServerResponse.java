package com.sudosaints.excusepro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sudosaints.excusepro.exception.CommunicationException;

public class ServerResponse {

	private InputStream responseStream;

	int statusCode;
	String contentType;
	
	boolean alreadyRead = false;
	
	public ServerResponse(int statusCode, String contentType, InputStream responseStream) {
		super();
		this.responseStream = responseStream;
		this.contentType = contentType;
		this.statusCode = statusCode;
	}

	public InputStream getResponseStream() {
		return responseStream;
	}

	public void setResponseStream(InputStream responseStream) {
		this.responseStream = responseStream;
		alreadyRead = false;
	}

	public synchronized byte[] getByteArray() throws CommunicationException
	{
		if (!alreadyRead) {
			alreadyRead = true;
		    byte[] buffer = new byte[8192];
		    int bytesRead;
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    try {
				while ((bytesRead = responseStream.read(buffer)) != -1)
				{
				    output.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				//e.printStackTrace();
				throw new CommunicationException(e);
			}
		    return output.toByteArray();
		}
		else {
			throw new CommunicationException("Server Response has already been read!");
		}
	}

	public String getContentType() {
		return contentType;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
