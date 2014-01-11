package com.sudosaints.excusepro.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sudosaints.excusepro.Preferences;
import com.sudosaints.excusepro.R;
import com.sudosaints.excusepro.exception.CommunicationException;
import com.sudosaints.excusepro.test.MockServerResponseGenerator;
import com.sudosaints.excusepro.util.ApiRequest.RequestMethod;
import com.sudosaints.excusepro.util.ApiResponse.ApiError;



public class HttpHelper {
	
	private DefaultHttpClient client;
	private Logger logger;
	Context ctx;
	Preferences prefs;
	
	private boolean isMockEnabled() {
		return ctx.getResources().getBoolean(R.bool.isMockEnabled);
	}
	
	public static ServerResponse doSimpleHttpRequest (String uri) throws CommunicationException {

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);
			String contentType = response.getEntity().getContentType().getValue().trim();
			return new ServerResponse(response.getStatusLine().getStatusCode(), contentType, response.getEntity().getContent());				
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommunicationException(e);
		}
	}
	
	public HttpHelper(Context ctx) {
		this.client=getHttpClient();
		logger = new Logger(ctx);
		this.ctx =ctx;
		this.prefs = new Preferences(ctx);
	}
	
	private DefaultHttpClient getHttpClient() {

	    DefaultHttpClient lclient = new DefaultHttpClient();
	    ClientConnectionManager mgr = lclient.getConnectionManager();
	    HttpParams params = lclient.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 16000);
	    
	    lclient = new DefaultHttpClient(
	        new ThreadSafeClientConnManager(params,
	            mgr.getSchemeRegistry()), params);

	    return lclient;
	}
	
	private HttpResponse doHttpRequest(ApiRequest apiRequest, boolean useNewClient) throws CommunicationException {
		
		DefaultHttpClient httpClient;
		httpClient = useNewClient ? getHttpClient() : client;
		Properties reqParams = apiRequest.reqParams;
		
		/*final TelephonyManager tm = (TelephonyManager)  ctx.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		final String Device_IMEI = tm.getDeviceId();*/
		
		//HttpContext localContext = new BasicHttpContext();
		HttpUriRequest request = null;
		//String url = apiRequest.isUrlAbsolute() ? apiRequest.getUrl() : ctx.getResources().getString(R.string.server_name)+apiRequest.getUrl();
		String url = apiRequest.isUrlAbsolute() ? apiRequest.getUrl() : prefs.getServerUrl()+apiRequest.getUrl();

	    logger.debug("API HTTP REQUEST: "+url);
        logger.debug("Request Parameters: "+reqParams);

		List<NameValuePair> nameValuePairs = null;
		if (reqParams!=null && reqParams.size()>0) {
	        nameValuePairs = new ArrayList<NameValuePair>(reqParams.size());
	        for (Object pname : reqParams.keySet()) {
	        	nameValuePairs.add(new BasicNameValuePair((String)pname, reqParams.getProperty((String)pname)));
	        }
		}
		
		if (apiRequest.requestMethod==RequestMethod.POST) {
			request = new HttpPost(url);			
			if (nameValuePairs!=null) {
		        try {
					((HttpPost)request).setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new CommunicationException(e);
				}
			}
		} else if (apiRequest.requestMethod==RequestMethod.POST_RAW) {
			request = new HttpPost(url);			
			//InputStreamEntity entity = new InputStreamEntity(instream, length)
			FileEntity entity = new FileEntity(apiRequest.getPostFile(), "application/octet-stream");
			((HttpPost)request).setEntity(entity);
		} else if (apiRequest.requestMethod == RequestMethod.FILE_UPLOAD) {
			
			request = new HttpPost(url);
		} else { // GET
			if (nameValuePairs!=null && !apiRequest.isUrlAbsolute()) {
				try {
					//String serverName = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Preferences.serverName, Constants.SERVERNAME);
//					String serverName = CommonUtil.getServerNameFromURL(ctx.getResources().getString(R.string.server_url));
//					String nodeName = CommonUtil.getNodeNameFromURL(ctx.getResources().getString(R.string.server_url));
					String serverName = CommonUtil.getServerNameFromURL(prefs.getServerUrl());
					String nodeName = CommonUtil.getNodeNameFromURL(prefs.getServerUrl());

					logger.debug("Server Name - " + serverName);
					logger.debug("Node Name - " + nodeName);
					URI uri = URIUtils.createURI(Constants.URI_SCHEME,serverName, -1,nodeName+"/"+apiRequest.getUrl(), URLEncodedUtils.format(nameValuePairs, "UTF-8"), null);
					logger.debug("URI - " + uri.toString());
					request = new HttpGet(uri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					throw new CommunicationException(e);
				}
			}
			else {
				try{
					request = new HttpGet(url);
				}
				catch(IllegalArgumentException e){
					e.printStackTrace();
					throw new CommunicationException(e);
				}
			}			
		}
		
		boolean execOK = false;
		HttpResponse response = null;
		
		request.setHeader("Accept-Encoding", "gzip");	
		/*if(apiRequest.useBasicAuth()) {
			logger.debug("Using basic http authentication, UserName - " + prefs.getUserName() + " Password - " + prefs.getPassword());
			request.setHeader("Authorization", "Basic " + Base64.encodeToString((prefs.getUserName() + ":" + prefs.getPassword()).getBytes(), Base64.NO_WRAP));
		}*/
		/*request.setHeader("Device_IMEI", Device_IMEI);
		request.setHeader("Device_API_Version", ctx.getResources().getString(R.string.api_version));*/
				
		try {
			response = httpClient.execute(request);
			execOK = true;
		} catch (Exception e) {
		    logger.warn("HttpClient->execute() threw exception, creating new instance of HttpClient and retrying. Exception: "+e.getMessage());
			e.printStackTrace();
		}

		if (!execOK && !useNewClient) {
			httpClient = getHttpClient();
			try {
				response = httpClient.execute(request);
			} catch (Exception e) {
				// We still have a problem. Bail out.
			    logger.warn("HttpClient->execute() AGAIN threw exception. Bailing out.. Stack Trace follows:");			   
				e.printStackTrace();
			} 
		}
		return response;
	}
	
	public ServerResponse sendRequest (ApiRequest apiRequest) throws CommunicationException {
		return sendRequest(apiRequest, false);
	}
	
	public ServerResponse sendRequest (ApiRequest apiRequest, boolean useNewClient) throws CommunicationException {
		
		if (isMockEnabled()) {
			return new MockServerResponseGenerator(ctx).getMockResponse(apiRequest);
		}

		HttpEntity entity = null;
		HttpResponse response = null;
		
		response = doHttpRequest(apiRequest, useNewClient);			
		if (response==null) {
			throw new CommunicationException("Server Communication Error");
		}
		
		entity = response.getEntity();
		
		if (entity!=null) {
				//byte[] data = EntityUtils.toByteArray(entity);
			if (entity.getContentType()==null) {
				throw new CommunicationException(ApiError.MISSING_CONTENT_TYPE_HEADER);
			}
			String contentType = entity.getContentType().getValue().trim();
			try {
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if(contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					InputStream inputStream = new GZIPInputStream(entity.getContent());
					logger.debug("Content Encoding is Gzip");
					return new ServerResponse(response.getStatusLine().getStatusCode(), contentType, inputStream);
				} else {
					logger.debug("Content Encoding is NOT Gzip");
					return new ServerResponse(response.getStatusLine().getStatusCode(), contentType, entity.getContent());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CommunicationException(e);
			}
		}
		else {
			throw new CommunicationException("Communication Error");
		}
		
	}

	public Bitmap downloadImage(String url) {
		
		byte[] data = downloadRaw(url);
		if (null==data) return null;
		
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bm;					
	}

	/**
	 * Read response as raw bytes (typically for images)
	 * @param url
	 * @param useAuth
	 * @return
	 */
	public byte[] downloadRaw(String url) {
		byte[] data = null;
		ServerResponse response = null;
		//app.getLogger().debug("Fetching Image: "+url);
		try {
			response = sendRequest(new ApiRequest().setUrl(url).setUrlAbsolute(true).setRequestMethod(RequestMethod.GET), true);
			data = response.getByteArray();
		} catch (CommunicationException e) {
		    logger.warn("Image Download Failed for "+url);
			e.printStackTrace();
			return null;
		}
		if (null==data) return null;
		
		if (response.getContentType().startsWith("text")) {
			logger.warn("Received non-binary data when expecting binary!");
			//app.getLogger().debug(new String(data));
			return null;
		}
		else {
			return data;
		}

	}

}
