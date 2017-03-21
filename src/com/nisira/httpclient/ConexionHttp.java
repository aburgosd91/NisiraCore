package com.nisira.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.nisira.utils.nisiracore.Constantes;

@SuppressWarnings({ "deprecation", "unused" })
public class ConexionHttp{
	static HttpClient httpclient;
	static HttpPost httppost;
	static InputStream is;
	static String line, result, variables = "", complemento_url = "";
	static ArrayList<NameValuePair> nameValuePairs;
	static HttpResponse response;
	static HttpEntity entity;
	static BufferedReader reader;
	static StringBuilder sb;
	
	public static  void addVariableTask(String parametro,String var){
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(parametro, var));
		variables = String.format("?"+parametro+"=%s", var);
	}
	
	public static void addVariables(String name, String var){
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(name, var));
		variables = String.format("%s&%s=%s", variables, name, var);
	}
	
	public static void pageConsulta(String page){
		if(!page.isEmpty()){
			complemento_url	= page;
		}else{
			Constantes.messageLog("ConexionHttp","pageConsulta() -> page is null");
		}
		
	}
	public static String ejecutar(){//http client
		try {
			Constantes.messageLog(complemento_url, String.format("%s%s%s",ParametrosHttp.URL_SERVER, complemento_url, variables));
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(String.format("%s%s%s",ParametrosHttp.URL_SERVER, complemento_url, variables));
			
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			is = entity.getContent();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			sb = new StringBuilder();
			while ((line = reader.readLine()) != null)
				sb.append(String.format("%s\n", line));
			result = sb.toString();
			is.close();
			httpclient.getConnectionManager().shutdown();
			Constantes.messageLog(complemento_url, result);
			return result;
		} catch(Exception e) {
			Constantes.messageLog(complemento_url, e.toString());
			return null;
		}
	}
	
	public static String Jsoup_Json(){
		String result = "";
		try {
			Constantes.messageLog("Url", String.format("%s%s%s",ParametrosHttp.URL_SERVER, ConexionHttp.complemento_url, ConexionHttp.variables));
			String url=ParametrosHttp.URL_SERVER+ConexionHttp.complemento_url+ConexionHttp.variables;
			Connection.Response res = Jsoup
					.connect(url).method(Method.GET).execute();
//			Map<String, String> cookie = res.cookies();
			result = res.parse().select("body").text(); //->JSON
			Constantes.messageLog("Resultado",result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}
	public static String Jsoup_Xml(){
		String result = "";
		try {
			Constantes.messageLog("Url", String.format("%s%s%s",ParametrosHttp.URL_SERVER, ConexionHttp.complemento_url, ConexionHttp.variables));
			String url=ParametrosHttp.URL_SERVER+ConexionHttp.complemento_url+ConexionHttp.variables;
			Connection.Response res = Jsoup
					.connect(url).timeout(0).method(Method.GET).execute();
//			Map<String, String> cookie = res.cookies();
			result = res.parse().select("body").text().replace("\n", ""); //->XML , eliminar saltos de linea y espacios en blanco
			Constantes.messageLog("Resultado","aa");
			return result;
		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}
	
}
