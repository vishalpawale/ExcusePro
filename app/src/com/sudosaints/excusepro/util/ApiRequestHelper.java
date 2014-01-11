package com.sudosaints.excusepro.util;

import android.content.Context;

import com.sudosaints.excusepro.R;
import com.sudosaints.excusepro.exception.CommunicationException;
import com.sudosaints.excusepro.util.ApiRequest.RequestName;

public class ApiRequestHelper {

	private Context context;
	HttpHelper httpHelper;
	ResponseHelper responseHelper;

	Logger logger;

	public ApiRequestHelper(Context context) {
		super();
		this.context = context;
		httpHelper = new HttpHelper(context);
		responseHelper = new ResponseHelper(context);
		logger = new Logger(context);
	}

	public ApiResponse getCategories() {
		
		ApiResponse apiResponse = new ApiResponse();
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setRequestName(RequestName.GET_CATEGORIES);
		apiRequest.setParam("apiKey", context.getResources().getString(R.string.api_key));
		ServerResponse serverResponse = null;
		try {
			serverResponse = httpHelper.sendRequest(apiRequest);
			apiResponse = responseHelper.getApiResponse(serverResponse);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return apiResponse.setError(e.getApiError());
		}
		return apiResponse;
	}
	
	public ApiResponse getExcuses(Long categoryId) {
		
		ApiResponse apiResponse = new ApiResponse();
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setRequestName(RequestName.GET_EXCUSES);
		apiRequest.setParam("apiKey", context.getResources().getString(R.string.api_key));
		apiRequest.setParam("categoryId", String.valueOf(categoryId));
		ServerResponse serverResponse = null;
		try {
			serverResponse = httpHelper.sendRequest(apiRequest);
			apiResponse = responseHelper.getApiResponse(serverResponse);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return apiResponse.setError(e.getApiError());
		}
		return apiResponse;
	}
	
	private ApiResponse performApiRequest(ApiRequest apiRequest) {
		ApiResponse apiResponse = new ApiResponse().setSuccess(false);
		ServerResponse response = null;
		try {
			response = httpHelper.sendRequest(apiRequest, true);
		} catch (CommunicationException e) {
			e.printStackTrace();
			return apiResponse.setError(e.getApiError());
		}
		apiResponse = responseHelper.getApiResponse(response);
		return apiResponse;
	}

}
