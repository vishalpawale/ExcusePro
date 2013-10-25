package com.sudosaints.excusepro.exception;

import com.sudosaints.excusepro.util.ApiResponse.ApiError;


@SuppressWarnings("serial")
public class CommunicationException extends Exception {

	private ApiError apiError;
	
	public CommunicationException(Throwable e) {
		super(e);
		this.apiError = ApiError.COMMUNICATION_ERROR;
	}
	
	public CommunicationException(String message) {
		super (message);
		this.apiError = ApiError.COMMUNICATION_ERROR;
	}
	
	public CommunicationException(ApiError error) {
		setApiError(error);
	}

	public ApiError getApiError() {
		return apiError;
	}

	public void setApiError(ApiError apiError) {
		this.apiError = apiError;
	}

}
