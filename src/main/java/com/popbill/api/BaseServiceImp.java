/*
 * Copyright 2006-2014 innopost.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.popbill.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import kr.co.linkhub.auth.LinkhubException;
import kr.co.linkhub.auth.Token;
import kr.co.linkhub.auth.TokenBuilder;

import com.google.gson.Gson;

/**
 * Abstract class for Popbill Services.
 * 
 * @author KimSeongjun
 * @version 1.0.0
 */
public abstract class BaseServiceImp implements BaseService {

	private final String ServiceID_REAL = "POPBILL";
	private final String ServiceID_TEST = "POPBILL_TEST";
	private final String ServiceURL_REAL = "https://popbill.linkhub.co.kr";
	private final String ServiceURL_TEST = "https://popbill_test.linkhub.co.kr";
	private final String APIVersion = "1.0";

	private TokenBuilder tokenBuilder;

	private boolean isTest;
	private String linkID;
	private String secretKey;
	private Gson _gsonParser = new Gson();

	private Map<String, Token> tokenTable = new HashMap<String, Token>();

	/**
	 * 테스트모드 확인. 기본값은 false.
	 * 
	 * @return is Test or not.
	 */
	public boolean isTest() {
		return isTest;
	}

	/**
	 * 테스트 모드 설정.
	 * 
	 * @param isTest
	 *            Test or not.
	 */
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	protected String getLinkID() {
		return linkID;
	}

	/**
	 * 링크아이디 설정. (링크허브에서 발급)
	 * 
	 * @param linkID
	 */
	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	protected String getSecretKey() {
		return secretKey;
	}

	/**
	 * 비밀키 설정 (Issued by Linkhub)
	 * 
	 * @param secretKey
	 *            SecretKey.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	protected String getServiceID() {
		return isTest ? ServiceID_TEST : ServiceID_REAL;
	}

	protected String getServiceURL() {
		return isTest ? ServiceURL_TEST : ServiceURL_REAL;
	}

	private TokenBuilder getTokenbuilder() {
		if (this.tokenBuilder == null) {
			tokenBuilder = TokenBuilder
					.getInstance(getLinkID(), getSecretKey())
					.ServiceID(isTest ? ServiceID_TEST : ServiceID_REAL)
					.addScope("member");

			for (String scope : getScopes())
				tokenBuilder.addScope(scope);
		}

		return tokenBuilder;
	}
	
	private String getSessionToken(String CorpNum, String ForwardIP)
			throws PopbillException {

		if (CorpNum == null || CorpNum.isEmpty())
			throw new PopbillException(-99999999, "회원 사업자번호가 입력되지 않았습니다.");

		Token token = null;
		Date UTCTime = null;

		if (tokenTable.containsKey(CorpNum))
			token = tokenTable.get(CorpNum);

		boolean expired = true;

		if (token != null) {
			
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			SimpleDateFormat subFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			subFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			try {
				Date expiration = format.parse(token.getExpiration());
				UTCTime = subFormat.parse(getTokenbuilder().getTime());
				expired = expiration.before(UTCTime);
				
			} catch (LinkhubException le){
				throw new PopbillException(le);
			} catch (ParseException e){
			}
		}

		if (expired) {
			if (tokenTable.containsKey(CorpNum))
				tokenTable.remove(CorpNum);

			try {
				token = getTokenbuilder().build(CorpNum, ForwardIP);
				tokenTable.put(CorpNum, token);
			} catch (LinkhubException le) {
				throw new PopbillException(le);
			}
		}

		return token.getSession_token();
	}

	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#getBalance(java.lang.String)
	 */
	@Override
	public double getBalance(String CorpNum) throws PopbillException {
		try {
			return getTokenbuilder().getBalance(
					this.getSessionToken(CorpNum, null));
		} catch (LinkhubException le) {
			throw new PopbillException(le);
		}
	}

	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#getPartnerBalance(java.lang.String)
	 */
	@Override
	public double getPartnerBalance(String CorpNum) throws PopbillException {
		try {
			return getTokenbuilder().getPartnerBalance(
					this.getSessionToken(CorpNum, null));
		} catch (LinkhubException le) {
			throw new PopbillException(le);
		}
	}

	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#joinMember(com.popbill.api.JoinForm)
	 */
	@Override
	public Response joinMember(JoinForm joinInfo) throws PopbillException {
		String postData = toJsonString(joinInfo);

		return httppost("/Join", null, postData, null, Response.class);
	}

	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#getPopbillURL(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getPopbillURL(String CorpNum, String UserID, String TOGO)
			throws PopbillException {
		URLResponse response = httpget("/?TG=" + TOGO, CorpNum, UserID,
				URLResponse.class);

		return response.url;
	}

	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#checkIsMember(java.lang.String, java.lang.String)
	 */
	@Override
	public Response checkIsMember(String CorpNum, String LinkID)
			throws PopbillException {

		return httpget("/Join?CorpNum=" + CorpNum + "&LID=" + LinkID, null,
				null, Response.class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#listContact(java.lang.String, java.lang.String)
	 */
	@Override
	public ContactInfo[] listContact(String CorpNum, String UserID) 
			throws PopbillException {
			
		return httpget("/IDs", CorpNum, UserID, ContactInfo[].class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#updateContactMember(java.lang.String, com.popbill.api.ContactInfo, java.lang.String)
	 */
	@Override
	public Response updateContact(String CorpNum, ContactInfo contactInfo, String UserID) throws PopbillException {
		String postData = toJsonString(contactInfo);

		return httppost("/IDs", CorpNum, postData, UserID, Response.class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#registContactMember(java.lang.String, com.popbill.api.ContactInfo, java.lang.String)
	 */
	@Override
	public Response registContact(String CorpNum, ContactInfo contactInfo, String UserID) throws PopbillException {
		String postData = toJsonString(contactInfo);

		return httppost("/IDs/New", CorpNum, postData, UserID, Response.class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#checkID(java.lang.String)
	 */
	@Override
	public Response checkID(String CheckID) throws PopbillException {
		
		return httpget("/IDCheck?ID="+CheckID, null, null, Response.class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#getCorpInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public CorpInfo getCorpInfo(String CorpNum, String UserID) throws PopbillException {
		
		return httpget("/CorpInfo", CorpNum, UserID, CorpInfo.class);
	}
	
	/* (non-Javadoc)
	 * @see com.popbill.api.BaseService#updateCorpInfo(java.lang.String, com.popbill.api.CorpInfo, java.lang.String)
	 */
	@Override
	public Response updateCorpInfo(String CorpNum, CorpInfo corpInfo, String UserID) throws PopbillException {
		String postData = toJsonString(corpInfo);

		return httppost("/CorpInfo", CorpNum, postData, UserID, Response.class);
	}
	
	/**
	 * Convert Object to Json String.
	 * 
	 * @param Graph
	 * @return jsonString
	 */
	protected String toJsonString(Object Graph) {
		return _gsonParser.toJson(Graph);
	}

	/**
	 * Convert JsonString to Object of Clazz
	 * 
	 * @param json
	 * @param clazz
	 * @return Object of Clazz
	 */
	protected <T> T fromJsonString(String json, Class<T> clazz) {
		return _gsonParser.fromJson(json, clazz);
	}

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param PostData
	 * @param UserID
	 * @param clazz
	 * @return returned object
	 * @throws PopbillException
	 */
	protected <T> T httppost(String url, String CorpNum, String PostData,
			String UserID, Class<T> clazz) throws PopbillException {
		return httppost(url, CorpNum, PostData, UserID, null, clazz);
	}

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param PostData
	 * @param UserID
	 * @param Action
	 * @param clazz
	 * @return returned object
	 * @throws PopbillException
	 */
	protected <T> T httppost(String url, String CorpNum, String PostData,
			String UserID, String Action, Class<T> clazz)
			throws PopbillException {
		HttpURLConnection httpURLConnection;
		try {
			URL uri = new URL(getServiceURL() + url);
			httpURLConnection = (HttpURLConnection) uri.openConnection();
		} catch (Exception e) {
			throw new PopbillException(-99999999, "팝빌 API 서버 접속 실패", e);
		}

		if (CorpNum != null && CorpNum.isEmpty() == false) {
			httpURLConnection.setRequestProperty("Authorization", "Bearer "
					+ getSessionToken(CorpNum, null));
		}

		httpURLConnection.setRequestProperty("x-pb-version".toLowerCase(),
				APIVersion);

		if (Action != null && Action.isEmpty() == false) {
			httpURLConnection.setRequestProperty("X-HTTP-Method-Override",
					Action);
		}

		httpURLConnection.setRequestProperty("Content-Type",
				"application/json; charset=utf8");
		
		httpURLConnection.setRequestProperty("Accept-Encoding",
				"gzip");

		if (UserID != null && UserID.isEmpty() == false) {
			httpURLConnection.setRequestProperty("x-pb-userid", UserID);
		}

		try {
			httpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
		}

		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoOutput(true);

		if ((PostData == null || PostData.isEmpty()) == false) {

			byte[] btPostData = PostData.getBytes(Charset.forName("UTF-8"));

			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(btPostData.length));

			try {

				DataOutputStream output = new DataOutputStream(
						httpURLConnection.getOutputStream());
				output.write(btPostData);
				output.flush();
				output.close();
			} catch (Exception e) {
				throw new PopbillException(-99999999,
						"Fail to POST data to Server.", e);
			}

		}
		String Result = "";

		try {
			InputStream input = httpURLConnection.getInputStream();
			if (httpURLConnection.getContentEncoding().equals("gzip")) {
				Result = fromGzipStream(input);
			}else {
				Result = fromStream(input);
			}
			input.close();
		} catch (IOException e) {

			ErrorResponse error = null;

			try {
				InputStream input = httpURLConnection.getErrorStream();
				Result = fromStream(input);
				input.close();
				error = fromJsonString(Result, ErrorResponse.class);
			} catch (Exception E) {
			}

			if (error == null)
				throw new PopbillException(-99999999,
						"Fail to receive data from Server.", e);
			else
				throw new PopbillException(error.getCode(), error.getMessage());
		}

		return fromJsonString(Result, clazz);
	}

	private static final String boundary = "--u489jwe98j3498j394r23450--";
	private static final String CRLF = "\r\n";

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param form
	 * @param files
	 * @param UserID
	 * @param clazz
	 * @return returned object
	 * @throws PopbillException
	 */
	protected <T> T httppostFiles(String url, String CorpNum, String form,
			List<UploadFile> files, String UserID, Class<T> clazz)
			throws PopbillException {
		HttpURLConnection httpURLConnection;
		try {
			URL uri = new URL(getServiceURL() + url);
			httpURLConnection = (HttpURLConnection) uri.openConnection();
		} catch (Exception e) {
			throw new PopbillException(-99999999, "팝빌 API 서버 접속 실패", e);
		}

		if (CorpNum != null && CorpNum.isEmpty() == false) {
			httpURLConnection.setRequestProperty("Authorization", "Bearer "
					+ getSessionToken(CorpNum, null));
		}

		httpURLConnection.setRequestProperty("x-pb-version".toLowerCase(),
				APIVersion);
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Content-Type",
				"multipart/form-data;boundary=" + boundary);

		if (UserID != null && UserID.isEmpty() == false) {
			httpURLConnection.setRequestProperty("x-pb-userid", UserID);
		}
		
		httpURLConnection.setRequestProperty("Accept-Encoding",
				"gzip");

		try {
			httpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
		}

		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoOutput(true);

		try {

			DataOutputStream output = new DataOutputStream(
					httpURLConnection.getOutputStream());

			if ((form == null || form.isEmpty()) == false) {
				String formBody = "--" + boundary + CRLF;
				formBody += "content-disposition: form-data; name=\"form\""
						+ CRLF;
				formBody += "content-type: Application/json; charset=utf-8"
						+ CRLF + CRLF;
				formBody += form + "\r\n";
				byte[] btFormBody = formBody.getBytes(Charset.forName("UTF-8"));

				output.write(btFormBody);
			}

			for (UploadFile f : files) {
				String fileHeader = "--" + boundary + CRLF;
				fileHeader += "content-disposition: form-data; name=\""
						+ f.fieldName + "\"; filename=\"" + f.fileName + "\""
						+ CRLF;
				fileHeader += "content-type: Application/octet-stream" + CRLF
						+ CRLF;

				byte[] btFileHeader = fileHeader.getBytes(Charset
						.forName("UTF-8"));

				output.write(btFileHeader);

				byte[] buffer = new byte[32768];
				int read;
				while ((read = f.fileData.read(buffer, 0, buffer.length)) > 0) {
					output.write(buffer, 0, read);
				}
				
				output.write(CRLF.getBytes(Charset
						.forName("UTF-8")));
			}

			String boundaryFooter = "--" + boundary + "--" + CRLF;
			byte[] btboundaryFooter = boundaryFooter.getBytes(Charset
					.forName("UTF-8"));

			output.write(btboundaryFooter);

			output.flush();
			output.close();
		} catch (Exception e) {
			throw new PopbillException(-99999999,
					"Fail to POST data to Server.", e);
		}

		String Result = "";

		try {
			InputStream input = httpURLConnection.getInputStream();
			if (httpURLConnection.getContentEncoding().equals("gzip")) {
				Result = fromGzipStream(input);
			}else {
				Result = fromStream(input);
			}
			input.close();
		} catch (IOException e) {

			ErrorResponse error = null;

			try {
				InputStream input = httpURLConnection.getErrorStream();
				Result = fromStream(input);
				input.close();
				error = fromJsonString(Result, ErrorResponse.class);
			} catch (Exception E) {
			}

			if (error == null)
				throw new PopbillException(-99999999,
						"Fail to receive data from Server.", e);
			else
				throw new PopbillException(error.getCode(), error.getMessage());
		}

		return fromJsonString(Result, clazz);
	}

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param UserID
	 * @param clazz
	 * @return returned object
	 * @throws PopbillException
	 */
	protected <T> T httpget(String url, String CorpNum, String UserID,
			Class<T> clazz) throws PopbillException {
		HttpURLConnection httpURLConnection;
		try {
			URL uri = new URL(getServiceURL() + url);
			httpURLConnection = (HttpURLConnection) uri.openConnection();
		} catch (Exception e) {
			throw new PopbillException(-99999999, "팝빌 API 서버 접속 실패", e);
		}

		if (CorpNum != null && CorpNum.isEmpty() == false) {
			httpURLConnection.setRequestProperty("Authorization", "Bearer "
					+ getSessionToken(CorpNum, null));
		}

		httpURLConnection.setRequestProperty("x-pb-version".toLowerCase(),
				APIVersion);

		if (UserID != null && UserID.isEmpty() == false) {
			httpURLConnection.setRequestProperty("x-pb-userid", UserID);
		}
		
		httpURLConnection.setRequestProperty("Accept-Encoding",
				"gzip");

		String Result = "";

		try {
			InputStream input = httpURLConnection.getInputStream();
			if (httpURLConnection.getContentEncoding().equals("gzip")) {
				Result = fromGzipStream(input);
			}else {
				Result = fromStream(input);
			}
			input.close();
		} catch (IOException e) {

			ErrorResponse error = null;

			try {
				InputStream input = httpURLConnection.getErrorStream();
				Result = fromStream(input);
				input.close();
				error = fromJsonString(Result, ErrorResponse.class);
			} catch (Exception E) {
			}

			if (error == null)
				throw new PopbillException(-99999999,
						"Fail to receive data from Server.", e);
			else
				throw new PopbillException(error.getCode(), error.getMessage());
		}

		return fromJsonString(Result, clazz);
	}

	protected abstract List<String> getScopes();

	private class ErrorResponse {

		private long code;
		private String message;

		public long getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

	}

	protected class UnitCostResponse {

		public float unitCost;

	}

	protected class UploadFile {
		public UploadFile() {
		}

		public String fieldName;
		public String fileName;
		public InputStream fileData;
	}

	protected class URLResponse {
		public String url;
	}
	
	private static String fromStream(InputStream input) throws IOException {

		InputStreamReader is = new InputStreamReader(input,
				Charset.forName("UTF-8"));
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);

		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}

		return sb.toString();
	}
	
	private static String fromGzipStream(InputStream input) throws IOException {
		GZIPInputStream zipReader = new GZIPInputStream(input);
		
		InputStreamReader is = new InputStreamReader(zipReader, "UTF-8");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);

		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}

		return sb.toString();
	}

}
