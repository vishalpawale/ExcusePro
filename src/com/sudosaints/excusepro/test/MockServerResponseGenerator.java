package com.sudosaints.excusepro.test;

import android.content.Context;

import com.sudosaints.excusepro.R;
import com.sudosaints.excusepro.util.ApiRequest;
import com.sudosaints.excusepro.util.ServerResponse;

public class MockServerResponseGenerator {

	Context context;
	
	public MockServerResponseGenerator(Context context) {
		this.context = context;
	}
	
	private int getMockDataFileResourceId(ApiRequest request) {
		switch (request.getRequestName()) {
		default:
			break;
		}
		
		return R.raw.responsemock_generror;
	}
	
	public ServerResponse getMockResponse (ApiRequest request) {
		return new ServerResponse(200, "application/json", 
				context.getResources().openRawResource(getMockDataFileResourceId(request)));
	}
	
}
