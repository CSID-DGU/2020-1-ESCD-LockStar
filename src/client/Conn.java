package client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Conn {
	public static void sendGet(String targetUrl) throws Exception {
		URL url = new URL(null, targetUrl, new sun.net.www.protocol.http.Handler());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "test");
		try {
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println("HTTP 응답 코드 : " + responseCode);
			System.out.println("HTTP body : " + response.toString());
		} catch (IOException e) {
			int responseCode = con.getResponseCode();
			System.out.println(responseCode + " 오류");
		}
	}

	public static void download(String url, String url1)
			throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();

		URL URL1 = new URL(null, url1, new sun.net.www.protocol.http.Handler());
		HttpURLConnection con = (HttpURLConnection) URL1.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "test");
		String result = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response1 = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response1.append(inputLine);
			}
			String str = response1.toString();
			String target = "\"name\":";
			int target_num = str.indexOf(target);
			result = str.substring(target_num + 8,
					(str.substring(target_num).indexOf("\"," + "\"created\":") + target_num));
			in.close();
		} catch (IOException e) {
		}
		String fileName = result;
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

	public static void downloadkey(String url, String keyPath) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();
		String fileName = keyPath;
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

	public static void symkey(String url, String keyPath) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();
		String fileName = keyPath;
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

}
