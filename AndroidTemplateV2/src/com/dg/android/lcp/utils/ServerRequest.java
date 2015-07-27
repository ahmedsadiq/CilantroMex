package com.dg.android.lcp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dg.android.lcp.objects.SessionManager;

public class ServerRequest {

	public static JSONObject sendRequest(String url, String parameter) {

		JSONObject responseJson = null;
		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
			}
			in.close();
			responseJson = new JSONObject(str.toString());
			Log.i("Get", str.toString());
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.logException(e);
		} catch (ClientProtocolException e) {
			ExceptionHandler.logException(e);
		} catch (IOException e) {
			ExceptionHandler.logException(e);
		} catch (JSONException e) {
			e.printStackTrace();

			responseJson = null;
		}
		Log.i("Json Exception:", "" + responseJson);
		return responseJson;

	}

	public static JSONObject sendPostRequest(String url, String parameter) {

		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		DefaultHttpClient client = new DefaultHttpClient();
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		if (url.contains("https")) {
			registry.register(new Scheme("https", socketFactory, 443));
		} else {
			registry.register(new Scheme("http", socketFactory, 443));
		}

		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient httpclient = new DefaultHttpClient(mgr, client.getParams());
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		JSONObject responseJson = null;
		// HttpClient httpclient = SessionManager.getHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			httppost.addHeader("content-type", "application/x-www-form-urlencoded");
			httppost.setEntity(new StringEntity(parameter));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
			}
			in.close();
			Log.i("Response Code", "=========" + str);
			responseJson = new JSONObject(str.toString());
			Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());

		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.logException(e);
		} catch (ClientProtocolException e) {
			ExceptionHandler.logException(e);
		} catch (IOException e) {
			ExceptionHandler.logException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("Json Exception:", "" + responseJson);
			responseJson = null;
		}
		Log.i("in post method", "the response String:" + responseJson);
		Log.i("in post method", "the response Json:" + responseJson);
		return responseJson;
	}

	public static JSONObject sendRequestWithoAuthToken(String url, String parameter) {

		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		DefaultHttpClient client = new DefaultHttpClient();
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		if (url.contains("https")) {
			registry.register(new Scheme("https", socketFactory, 443));
		} else {
			registry.register(new Scheme("http", socketFactory, 443));
		}

		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient clients = new DefaultHttpClient(mgr, client.getParams());
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		JSONObject responseJson = null;
		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = clients.execute(request);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
			}
			in.close();
			responseJson = new JSONObject(str.toString());

			Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() == 401) {
				Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());
				JSONObject error = new JSONObject();
				error.put("status", false);
				error.put("errorMsg", "401");
				return error;
			}

		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.logException(e);
		} catch (ClientProtocolException e) {
			ExceptionHandler.logException(e);
		} catch (IOException e) {
			ExceptionHandler.logException(e);
		} catch (JSONException e) {
			e.printStackTrace();

			responseJson = null;
		}
		Log.i("Json Exception:", "" + responseJson);
		return responseJson;

	}

	public static JSONObject sendPostRequestWithoAuthToken(String url, String parameter) {

		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		DefaultHttpClient client = new DefaultHttpClient();
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		if (url.contains("https")) {
			registry.register(new Scheme("https", socketFactory, 443));
		} else {
			registry.register(new Scheme("http", socketFactory, 443));
		}

		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient clients = new DefaultHttpClient(mgr, client.getParams());
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		JSONObject responseJson = null;
		// HttpClient httpclient = SessionManager.getHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			httppost.addHeader("content-type", "application/x-www-form-urlencoded");
			httppost.setEntity(new StringEntity(parameter));
			// Execute HTTP Post Request
			HttpResponse response = clients.execute(httppost);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
			}
			in.close();
			responseJson = new JSONObject(str.toString());
			Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() == 401) {
				Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());
				JSONObject error = new JSONObject();
				error.put("status", false);
				error.put("errorMsg", "401");
				return error;
			}
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.logException(e);
		} catch (ClientProtocolException e) {
			ExceptionHandler.logException(e);
		} catch (IOException e) {
			ExceptionHandler.logException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("Json Exception:", "" + responseJson);
			responseJson = null;
		}
		Log.i("in post method", "the response String:" + responseJson);
		Log.i("in post method", "the response Json:" + responseJson);
		return responseJson;
	}

	public static JSONObject sendDeleteRequest(String url, String parameter) throws UnknownHostException, SocketException, IOException, JSONException {

		JSONObject responseJson = null;
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(new HttpDelete(url));

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		in.close();
		Log.i("ResponseStringDELETE", str.toString());

		responseJson = new JSONObject(str.toString());

		if (response.getStatusLine().getStatusCode() == 401) {
			Log.i("Response Code", "=========" + response.getStatusLine().getStatusCode());
			JSONObject error = new JSONObject();
			error.put("status", false);
			error.put("errorMsg", "401");
			return error;
		} else {
			return responseJson;
		}
	}

	public static JSONObject sendDeleteRequestWithOAuthToken(String url, String parameter) throws UnknownHostException, SocketException, IOException, JSONException {

		JSONObject responseJson = null;
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(new HttpDelete(url));

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		in.close();
		Log.i("ResponseStringDELETE", str.toString());

		responseJson = new JSONObject(str.toString());

		return responseJson;
	}
}