package com.sudosaints.excusepro.util;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

public class ApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public static class ApiError implements Serializable {
		
		private static final long serialVersionUID = 1L;

		int code;
		String message;
		
		public ApiError() {			
		}

		public ApiError(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public static final ApiError GENERAL_ERROR = new ApiError(500, "Error");
		public static final ApiError COMMUNICATION_ERROR = new ApiError(501, "Unable to communicate with server");
		public static final ApiError RESPONSE_ERROR = new ApiError(502, "Bad Response Format");
		public static final ApiError API_VERSION_MISMATCH = new ApiError(503, "API Version Mismatch");
		public static final ApiError MISSING_CONTENT_TYPE_HEADER = new ApiError(504, "Missing Content Type Header");

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	boolean success;
	Object data;
	ApiError error;
	
	@JsonIgnore
	private Object resultDataObject;
	
	public ApiResponse() {		
	}
	
	public ApiResponse(boolean success, Map<String, Object> data, String message) {
		this.success = success;
		this.data = data;
		if (!success) {
			this.error = new ApiError(500, message);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public ApiResponse setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public Object getData() {
		return data;
	}

	public ApiResponse setData(Object data) {
		this.data = data;
		return this;
	}

	public ApiError getError() {
		return error;
	}

	public ApiResponse setError(ApiError error) {
		this.error = error;
		if (error!=null) {
			success = false;
		}
		return this;
	}

	public Object getResultDataObject() {
		return resultDataObject;
	}

	public ApiResponse setResultDataObject(Object resultDataObject) {
		this.resultDataObject = resultDataObject;
		return this;
	}
	
	
}
