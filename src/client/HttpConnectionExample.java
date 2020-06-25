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
			System.out.print("�޴�����(1: ȸ������, 2: �ۺ�Ű ���, 3: ���� ���ε�, 4: ���� �ٿ�ε�, 5: �ٿ�ε� ������ ���� Ȯ��, 6: ���� ����, 7: ���� �̸� ����, 8: ���� ��ȣŰ �ٿ�ε�, 9: ��ȣȭ ���): ");
			ch = sc.nextInt();
			sc.nextLine();
			if (ch == 0) {
				break;
			} else if (ch == 1) { // ȸ�� ����
				String name, password;
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();

				// ������ ���̵�� �н����� ������ ����
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name).addParam("password", password).submit();

			} else if (ch == 2) { // ����Ű ���
				String name, password;
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();

				RSA.keyMake("./public.key", "./private.key"); // Ű �� ����
				File filePublicKey = new File("./public.key");

				// ����� ����Ű ������ ���
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name).addParam("password", password).addParam("key", filePublicKey)
						.submit();

			} else if (ch == 3) { // ���� ���ε�
				String name, password;
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();

				String filePath = "./upload.txt";
				String encryptedFilePath = "./encryptedUpload.txt";
				upload(filePath, encryptedFilePath); // ���� ��ȣȭ �� ���Ͽ� ���� ��ĪŰ ����
				String keyPath = "./aeskey.txt"; // ���Ͽ� ���� ��ĪŰ ��ġ

				urlParameters = "?username=" + name + "&password=" + password;
				downloadkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key/"
						+ urlParameters);
				PublicKey server_publicKey = RSA.LoadPublicKeyPair("./serverkey.txt");

				// ��ĪŰ ��ȣȭ
				File encryptedFileKey = new File(RSA.encryption(keyPath, "./encryptedUploadKey.key", server_publicKey));
				File encryptedFile = new File(encryptedFilePath);

				// ��ȣȭ�� ���� �� ��ȣȭ�� ��ĪŰ�� ������ ����
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file");
				http_post.addParam("username", name).addParam("password", password).addParam("file", encryptedFile)
						.addParam("file_key", encryptedFileKey) // ��ȣȭ�� ��ĪŰ ����
						.submit();

			} else if (ch == 4) { // ���� �ٿ�ε�
				System.out.print("ID�Է�: ");
				String name = sc.nextLine();
				System.out.print("password�Է�: ");
				String password = sc.nextLine();
				System.out.print("�ٿ�ε��� ����ID �Է�: ");
				String FileID = sc.nextLine();

//              test
//              String name = "yoon";
//              String password = "1234";
//              String FileID = "49";

				urlParameters = FileID + "?username=" + name + "&password=" + password;
				download("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + urlParameters);

			} else if (ch == 5) { // �ٿ�ε��� �� �ִ� ���� Ȯ��
				System.out.print("ID�Է�: ");
				String name = sc.nextLine();
				System.out.print("password�Է�: ");
				String password = sc.nextLine();
				urlParameters = "username=" + name + "&password=" + password;
				http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters);

			} else if (ch == 6) { // ���� ����
				String name, password, toName;
				int fileId;
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();
				System.out.print("������ ���� ID �Է�: ");
				toName = sc.nextLine();
				System.out.print("������ �� ���� ID �Է�: ");
				fileId = sc.nextInt();

				Http http_post = new Http(
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow/" + fileId);
				http_post.addParam("username", name) // ����� ���̵�
						.addParam("password", password) // ����� ��й�ȣ
						.addParam("usernames", toName) // ������ ���� ����� ID ����
						.submit();

			} else if (ch == 7) { // ���� �̸� ���� ��û
				String name, password, fileRename;
				int fileId;
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();
				System.out.print("�̸� ������ ���� ID �Է�: ");
				fileId = sc.nextInt();
				sc.nextLine();
				System.out.print("���ο� ���� �̸� �Է�: ");
				fileRename = sc.nextLine();

				File newFile = new File(fileRename);
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + fileId);
				
				http_post.addParam("username", name) // ����� ���̵�
						.addParam("password", password) // ����� ��й�ȣ
						.addParam("file", newFile) // ���ο� ���� �̸�
						.submit();

				
			} else if (ch == 8) { // ����Ű �ٿ�ε�
				System.out.print("�ٿ�ε��� ����ID �Է�: ");
				String FileID = sc.nextLine();
				System.out.print("ID�Է�: ");
				String name = sc.nextLine();
				System.out.print("password�Է�: ");
				String password = sc.nextLine();
				urlParameters = "?username=" + name + "&password=" + password;
				symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + FileID + "/"
						+ urlParameters);

			} else if (ch == 9) { // ��ȣȭ ���

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
						plain = aes.decrypt(line, RSA.fileToString("./decryptedSymkey.txt")); // ��Īkey
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
			System.out.println("HTTP ���� �ڵ� : " + responseCode);
			System.out.println("HTTP body : " + response.toString());
		} catch (IOException e) {
			int responseCode = con.getResponseCode();
			System.out.println(responseCode + " ����");
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
		System.out.println("HTTP ���� �ڵ� : " + responseCode);
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
		System.out.println("HTTP ���� �ڵ� : " + responseCode);
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
		System.out.println("HTTP ���� �ڵ� : " + responseCode);
	}

	private static File makeKey() throws IOException {
		String public_key = "asdasd"; // �ۺ�Ű ����
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

			// ���Ͽ� ���� ��ĪŰ ���ÿ� ����
			String aeskey = aes.getKey(); // �Ϻ�ȣ �� ���Ǵ� key
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
