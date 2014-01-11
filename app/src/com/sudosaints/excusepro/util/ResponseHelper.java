package com.sudosaints.excusepro.util;

import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;

import com.sudosaints.excusepro.exception.CommunicationException;
import com.sudosaints.excusepro.exception.ResponseFormatException;
import com.sudosaints.excusepro.util.ApiResponse.ApiError;

public class ResponseHelper {

	ObjectMapper mapper = new ObjectMapper();
	Context context;
	Logger logger;
	
	public ResponseHelper(Context ctx) {
		this.context = ctx;
		this.logger = new Logger(ctx);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	private void checkResponse(ServerResponse response)
			throws CommunicationException, ResponseFormatException {
		if (null == response) {
			throw new CommunicationException("Error Communicating with Server");
		}
        		
		if (logger.debugEnabled) {
			dumpResponse(response);
		}
		
        if (!response.getContentType().contains("application/json")) {
			throw new ResponseFormatException(
					"Server Response not in JSON format");
		}
	}
	
	private void dumpResponse(ServerResponse response) {
		
		try {
			String resp = new String(response.getByteArray());
			logger.debug("Response Dump: "+resp);
			response.setResponseStream(new StringBufferInputStream(resp));
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}
	
	public ApiResponse getApiResponse(ServerResponse response) {
		try {
			checkResponse(response);
		} catch (CommunicationException e1) {
			e1.printStackTrace();
			return new ApiResponse().setSuccess(false).setError(ApiError.COMMUNICATION_ERROR);
		} catch (ResponseFormatException e1) {
			e1.printStackTrace();
			return new ApiResponse().setSuccess(false).setError(ApiError.RESPONSE_ERROR);
		}

		ApiResponse apiResponse = null;
		try {
			Map<String, Object> responseMap = mapper.readValue(response.getResponseStream(),Map.class);
			apiResponse = new ApiResponse().setError(ApiError.RESPONSE_ERROR);
			if (responseMap.containsKey("success") && responseMap.get("success") != null) {
				apiResponse.setSuccess(((Boolean)responseMap.get("success")).booleanValue());
			}
			if (responseMap.containsKey("error") && responseMap.get("error") != null && !((Map<String,Object>) responseMap.get("error")).isEmpty()) {
				apiResponse.setSuccess(false);
				Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
				apiResponse.setError(new ApiError((Integer)error.get("code"), (String) error.get("message")));
			}
			if (responseMap.containsKey("data") && responseMap.get("data") != null) {
				apiResponse.setSuccess(true);
				Object data = responseMap.get("data");
				apiResponse.setData(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiResponse().setSuccess(false).setError(ApiError.GENERAL_ERROR);
		}
		return apiResponse;
	}
	
	public ResultStatus parseLoginResponse(ServerResponse response) {
		
		ResultStatus resultStatus = new ResultStatus(true);
		try {
			checkResponse(response);
		} catch (CommunicationException e1) {
			e1.printStackTrace();
			return ResultStatus.errorResult(ApiError.COMMUNICATION_ERROR.getMessage());
		} catch (ResponseFormatException e1) {
			e1.printStackTrace();
			return ResultStatus.errorResult(ApiError.RESPONSE_ERROR.getMessage());
		}
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			responseMap = mapper.readValue(response.getResponseStream(), Map.class);
			if(!Boolean.parseBoolean(responseMap.get("succeed")+"")) {
				resultStatus.setSuccess(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultStatus.errorResult(ApiError.GENERAL_ERROR.getMessage());
		}
		resultStatus.setResultObject(responseMap);
		return resultStatus;
	}
		
}
