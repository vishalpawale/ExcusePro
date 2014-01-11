package com.sudosaints.excusepro.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.sudosaints.excusepro.exception.CryptographyException;



public class CommonUtil {
	 
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	Context context;

	public CommonUtil(Context context) {
		super();
		this.context = context;
	}
	
	public boolean isDevBuild() {
		//return context.getResources().getString(R.string.build_type).startsWith("dev");
		return true;
	}
	
	private String encodeString(String message, String skey, String ivx)
			throws CryptographyException, UnsupportedEncodingException {
		if(message==null || message.equals("")) {
			return null;
		}
		SecretKeySpec keySpec = new SecretKeySpec(skey.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());
 
		Cipher cipher = getCypher(keySpec, ivSpec, Cipher.ENCRYPT_MODE);
 
		// Gets the raw bytes to encrypt, UTF8 is needed for
		// having a standard character set
		byte[] stringBytes;
 
		stringBytes = message.getBytes("UTF8");
 
		// encrypt using the cypher
		byte[] raw;
		try {
			raw = cipher.doFinal(stringBytes);
		} catch (IllegalBlockSizeException e) {
			throw new CryptographyException(e);
		} catch (BadPaddingException e) {
			throw new CryptographyException(e);
		}
 
		// converts to base64 for easier display.
/*			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(raw);*/
		
		String tp ="";
		try {
			tp= new String(Base64.encode(raw, Base64.NO_WRAP),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return tp; 
	}
 
	public String decodeString(String encrypted, String skey, String ivx) throws CryptographyException {
		if(encrypted==null || encrypted.equals("") || encrypted.equals("null")) {
			return null;
		}
		
		SecretKeySpec keySpec = new SecretKeySpec(skey.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());
 
		Cipher cipher = getCypher(keySpec, ivSpec, Cipher.DECRYPT_MODE);
 
		
		
		byte[] raw;
		
			raw = Base64.decode(encrypted.getBytes(),Base64.NO_WRAP);
		
 
		// decode the message
		
		byte[] stringBytes;
		try {
			stringBytes = cipher.doFinal(raw);
		} catch (IllegalBlockSizeException e) {
			throw new CryptographyException("Encrypted message was corrupted",
					e);
		} catch (BadPaddingException e) {
			throw new CryptographyException("Encrypted message was corrupted",
					e);
		}
 
		// converts the decoded message to a String
		String clear;
		try {
			clear = new String(stringBytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new CryptographyException(e);
		}
		return clear;
	}
 
	/**
	 * @param keySpec
	 * @param ivSpec
	 * @param mode
	 * @return
	 * @throws CryptographyException
	 */
	public static Cipher getCypher(SecretKeySpec keySpec,
			IvParameterSpec ivSpec, int mode) throws CryptographyException {
		// Get a cipher object.
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("invalid algorithm", e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("invalid padding", e);
		}
		try {
			cipher.init(mode, keySpec, ivSpec);
		} catch (InvalidKeyException e) {
			throw new CryptographyException("invalid key", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("invalid algorithm parameter.", e);
		}
		return cipher;
	}

	public String decodeString(String val) {
		if (isDevBuild()) {
			return val;
		}
		Log.i("TrackSense", "Val - " + val);
		try {
			return decodeString(val, Constants.key, Constants.ivx);
		} catch (CryptographyException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String encodeString(String val) {
		if (isDevBuild()) {
			return val;
		}
		
		try {
			return encodeString(val,Constants.key,Constants.ivx);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (CryptographyException e) {
			e.printStackTrace();
		}
		return null;
	}
	 
	public static String getServerNameFromURL (String url) {
		try {
			int from = url.indexOf("//") + 2;
			int to = url.indexOf("/", from);
			if (to < 0) {
				return url.substring(from);
			}
			return url.substring(from, to);
		} catch (Exception e) {
			e.printStackTrace();
			return url;
		}
		
	}

	public static String getNodeNameFromURL(String url) {
		try {
			int firstIndex = url.indexOf("//");
			int from = url.indexOf("/", firstIndex + 2);
			if (url.charAt(from-1)=='/') {
				return "";
			}
			int to = url.length();		
			return url.substring(from, to);
		} catch (Exception e) {
			e.printStackTrace();
			return url;
		}
	}
	
	public static String removeTrailingSlashes(String str) {
		while(str.endsWith("/") && str.length() > 1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	
	public static boolean validateEmail(String email) {
		Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}
	
}

	