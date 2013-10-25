package com.sudosaints.excusepro.exception;


@SuppressWarnings("serial")
public class CryptographyException extends Exception {

	public CryptographyException(Throwable e) {
		super(e);
	}
	
	public CryptographyException(String message) {
		super (message);
	}

	public CryptographyException(String string, Exception e) {
		super(string,e);
	}
}
