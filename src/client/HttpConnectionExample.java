package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpConnectionExample {
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		HttpConnectionExample http = new HttpConnectionExample();
		int ch = 1;
		String urlParameters = "";
		while (true) {
			System.out.print("메뉴선택(1: 회원가입, 2: 퍼블릭키 등록, 3: 파일 업로드, 4: 파일 다운로드, 5: 다운로드 가능한 파일 확인, 6: 권한 전송, 7: 파일 이름 수정, 8: 파일 암호키 다운로드, 9: 복호화 모듈): ");
			ch = sc.nextInt();
			sc.nextLine();
			if (ch == 0) {
				break;
			} else if (ch == 1) { // 회원 가입
				String name, password;
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();

				// 생성할 아이디와 패스워드 서버에 전송
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name).addParam("password", password).submit();

			} else if (ch == 2) { // 공개키 등록
				String name, password;
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();

				RSA.keyMake("./public.key", "./private.key"); // 키 쌍 생성
				File filePublicKey = new File("./public.key");

				// 사용자 공개키 서버에 등록
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name).addParam("password", password).addParam("key", filePublicKey)
						.submit();

			} else if (ch == 3) { // 파일 업로드
				String name, password;
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();

				String filePath = "./upload.txt";
				String encryptedFilePath = "./encryptedUpload.txt";
				upload(filePath, encryptedFilePath); // 파일 암호화 및 파일에 대한 대칭키 생성
				String keyPath = "./aeskey.txt"; // 파일에 대한 대칭키 위치

				urlParameters = "?username=" + name + "&password=" + password;
				downloadkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key/"
						+ urlParameters);
				PublicKey server_publicKey = RSA.LoadPublicKeyPair("./serverkey.txt");

				// 대칭키 암호화
				File encryptedFileKey = new File(RSA.encryption(keyPath, "./encryptedUploadKey.key", server_publicKey));
				File encryptedFile = new File(encryptedFilePath);

				// 암호화된 파일 및 암호화된 대칭키를 서버에 전송
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file");
				http_post.addParam("username", name).addParam("password", password).addParam("file", encryptedFile)
						.addParam("file_key", encryptedFileKey) // 암호화된 대칭키 전송
						.submit();

			} else if (ch == 4) { // 파일 다운로드
				System.out.print("ID입력: ");
				String name = sc.nextLine();
				System.out.print("password입력: ");
				String password = sc.nextLine();
				System.out.print("다운로드할 파일ID 입력: ");
				String FileID = sc.nextLine();

//              test
//              String name = "yoon";
//              String password = "1234";
//              String FileID = "49";

				urlParameters = FileID + "?username=" + name + "&password=" + password;
				download("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + urlParameters);

			} else if (ch == 5) { // 다운로드할 수 있는 파일 확인
				System.out.print("ID입력: ");
				String name = sc.nextLine();
				System.out.print("password입력: ");
				String password = sc.nextLine();
				urlParameters = "username=" + name + "&password=" + password;
				http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters);

			} else if (ch == 6) { // 권한 전송
				String name, password, toName;
				int fileId;
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();
				System.out.print("권한을 보낼 ID 입력: ");
				toName = sc.nextLine();
				System.out.print("권한을 줄 파일 ID 입력: ");
				fileId = sc.nextInt();

				Http http_post = new Http(
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow/" + fileId);
				http_post.addParam("username", name) // 사용자 아이디
						.addParam("password", password) // 사용자 비밀번호
						.addParam("usernames", toName) // 권한을 가질 사용자 ID 정보
						.submit();

			} else if (ch == 7) { // 파일 이름 수정 요청
				String name, password, fileRename;
				int fileId;
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();
				System.out.print("이름 수정할 파일 ID 입력: ");
				fileId = sc.nextInt();
				sc.nextLine();
				System.out.print("새로운 파일 이름 입력: ");
				fileRename = sc.nextLine();

				File newFile = new File(fileRename);
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + fileId);
				
				http_post.addParam("username", name) // 사용자 아이디
						.addParam("password", password) // 사용자 비밀번호
						.addParam("file", newFile) // 새로운 파일 이름
						.submit();

				
			} else if (ch == 8) { // 파일키 다운로드
				System.out.print("다운로드할 파일ID 입력: ");
				String FileID = sc.nextLine();
				System.out.print("ID입력: ");
				String name = sc.nextLine();
				System.out.print("password입력: ");
				String password = sc.nextLine();
				urlParameters = "?username=" + name + "&password=" + password;
				symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + FileID + "/"
						+ urlParameters);

			} else if (ch == 9) { // 복호화 모듈

				PrivateKey user_privatekey = RSA.LoadPrivateKeyPair("./private.key");

				// KeyPair keyPair = RSA.keyMake("./public.key", "./private.key");
				// RSA.encryption(filePath, encryptedFilePath, keyPair.getPublic());
				RSA.decryption("./symkey.txt", "./decryptedSymkey.txt", user_privatekey);

				AES256Util aes = new AES256Util();
				try {
					File infile = new File("./download.txt");
					FileReader filereader = new FileReader(infile);
					File outfile = new File("./plaindownload.txt");
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outfile));
					BufferedReader bufReader = new BufferedReader(filereader);
					String line = "";
					String plain;
					while ((line = bufReader.readLine()) != null) {
						plain = aes.decrypt(line, RSA.fileToString("./decryptedSymkey.txt")); // 대칭key
						if (outfile.isFile() && outfile.canWrite()) {
							bufferedWriter.write(plain);
							bufferedWriter.newLine();
							bufferedWriter.close();
						}
					}

					bufReader.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					System.out.println(e);
				}

			}

		}
	}

	private void sendGet(String targetUrl) throws Exception {
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

	private static void download(String url)
			throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();
		String fileName = "./download.txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

	private static void downloadkey(String url) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();
		String fileName = "./serverkey.txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

	private static void symkey(String url) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		int responseCode = response.getStatusLine().getStatusCode();
		InputStream inputStream = entity.getContent();
		String fileName = "./symkey.txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		int asd;
		while ((asd = inputStream.read()) != -1) {
			fos.write(asd);
		}
		System.out.println("HTTP 응답 코드 : " + responseCode);
	}

	private static File makeKey() throws IOException {
		String public_key = "asdasd"; // 퍼블릭키 생성
		File key = new File("./key.txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(key, false);
			writer.write(public_key);
			writer.flush();
		} catch (IOException e) {
		}
		return key;
	}

	private static void upload(String filePath, String encryptedFilePath)
			throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
		AES256Util aes = new AES256Util();
		try {
			File infile = new File(filePath);
			FileReader filereader = new FileReader(infile);
			File outfile = new File(encryptedFilePath);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outfile));
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";
			String crypto;
			while ((line = bufReader.readLine()) != null) {
				crypto = aes.encrypt(line);
				if (outfile.isFile() && outfile.canWrite()) {
					bufferedWriter.write(crypto);
					bufferedWriter.newLine();
					bufferedWriter.close();
				}
			}

			// 파일에 대한 대칭키 로컬에 저장
			String aeskey = aes.getKey(); // 암복호 시 사용되는 key
			File keyfile = new File("./aeskey.txt");
			bufferedWriter = new BufferedWriter(new FileWriter(keyfile));
			bufferedWriter.write(aeskey);
			bufferedWriter.newLine();
			bufferedWriter.close();

			bufReader.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
