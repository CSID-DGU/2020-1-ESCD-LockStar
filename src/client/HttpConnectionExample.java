package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class HttpConnectionExample {
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		HttpConnectionExample http = new HttpConnectionExample();
		int ch = 1;
		String urlParameters = "";
		String name = "";
		String password = "";
		StringBuffer sb;
		
		System.out.print("*** Welcome to Secure File transfer System ***");
		while (true) {
			System.out.print(
					"\n ================= �޴�����  =================\n"
					+ " 1: ȸ������ \t2: �α��� \t3: ���� ���ε� \n"
					+ " 4: ���� ��� \t5: ���� �ٿ�ε� \t6: ���� ���� \n"
					+ " 7: ���� ���� \t8: ���� ��ȣŰ �ٿ� \t9: ��ȣȭ ��� \n"
					+ " 10: �α׾ƿ� \t0: ���α׷� ���� \n"
					+ "�Է� > ");
			ch = sc.nextInt();
			sc.nextLine();
			if (ch == 0) {
				System.out.println("===== �̿��� �ּż� �����մϴ� :) =====");
				break;
			} else if (ch == 1) { // ȸ�� ���� �� ����� ����Ű ������ ���
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();

				// ������ ���̵�� �н����� ������ ����
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name).addParam("password", password).submit();
				
				TimeUnit.SECONDS.sleep(2);	// 3�ʰ� ���
				
				RSA.keyMake("./public_" + name + ".txt", "./private_" + name + ".txt"); // Ű �� ����
				File filePublicKey = new File("./public_" + name + ".txt");
				
				// ����� ����Ű ������ ���
				http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name).addParam("password", password).addParam("key", filePublicKey)
						.submit();

			} else if (ch == 2) { // �α���
				System.out.print("ID�Է�: ");
				name = sc.nextLine();
				System.out.print("password�Է�: ");
				password = sc.nextLine();

				
			} else if (ch == 3) { // ���� ���ε�
				System.out.print("���ε��� ���� ��� �Է� : ");
				String filePath = sc.nextLine();
				
				// ��ȣȭ�� ���� ���
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_encrypted");
				String encryptedFilePath = sb.toString();
				
				// ���Ͽ� ���� ��ĪŰ ���
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_aeskey");
				String keyPath = sb.toString();
				
				// ���Ͽ� ���� ��ĪŰ�� ���ĪŰ ���
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_rsakey");
				String encryptedKeyPath = sb.toString();

				// ���� ����Ű ���
				String serverkeyPath = "./server_public.txt";
				
				upload(filePath, encryptedFilePath, keyPath); // ���� ��ȣȭ �� ���Ͽ� ���� ��ĪŰ ����

				// �����κ��� ������ ���� ����Ű �ٿ�ε�
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.downloadkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key/"
						+ urlParameters, serverkeyPath);
				
				PublicKey server_publicKey = RSA.LoadPublicKeyPair(serverkeyPath);

				// ��ĪŰ ��ȣȭ
				File encryptedFileKey = new File(RSA.encryption(keyPath, encryptedKeyPath, server_publicKey));
				File encryptedFile = new File(encryptedFilePath);

				// ��ȣȭ�� ���� �� ��ȣȭ�� ��ĪŰ�� ������ ����
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file");
				http_post.addParam("username", name).addParam("password", password)
						.addParam("file", encryptedFile)		// ��ȣȭ�� ���� ����
						.addParam("file_key", encryptedFileKey) // ��ȣȭ�� ��ĪŰ ����
						.submit();

			} else if (ch == 4) { // �ٿ�ε��� �� �ִ� ���� Ȯ��
				// �ٿ�ε� ������ ���� ��� ���
				urlParameters = "username=" + name + "&password=" + password;
				Conn.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters);

			} else if (ch == 5) { // ���� �ٿ�ε�
				System.out.print("�ٿ�ε��� ����ID �Է�: ");
				String FileID = sc.nextLine();
				
				urlParameters = FileID + "?username=" + name + "&password=" + password;
				String urlParameters1 = "username=" + name + "&password=" + password;
				Conn.download("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + urlParameters,
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters1);

			} else if (ch == 6) { // ���� ����
				String toName;
				int fileId;
				System.out.print("������ ���� ID �Է�: ");
				toName = sc.nextLine();
				System.out.print("������ �� ���� ID �Է�: ");
				fileId = sc.nextInt();

				Http http_post = new Http(
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow/" + fileId);
				http_post.addParam("username", name).addParam("password", password)
						.addParam("usernames", toName) // ������ ���� ����� ID ����
						.submit();

			} else if (ch == 7) { // ���� ���� ��û
				String fileRename;
				int fileId;
				System.out.print("�̸� ������ ���� ID �Է�: ");
				fileId = sc.nextInt();
				sc.nextLine();
				System.out.print("���ο� ���� ��ġ �Է�: ");
				fileRename = sc.nextLine();

				String keyPath = "./aeskey.txt";
				
				// ����Ű �ٿ�ε�
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + fileId + "/"
						+ urlParameters, keyPath);
				
				String privateKeyPath = "./private_" + name + ".txt";
				// ����� ���Ű �ҷ�����
				PrivateKey user_privatekey = RSA.LoadPrivateKeyPair(privateKeyPath);
				// ����� ���Ű�� ���� ��ĪŰ ��ȣȭ
				RSA.decryption(keyPath, keyPath, user_privatekey);
				
				// ��ȣȭ�� ���� ���
				sb = new StringBuffer(fileRename);
				sb.insert(fileRename.lastIndexOf("."), "_encrypted");
				String encryptedFilePath = sb.toString();
				
				// �־��� ��ĪŰ�� ��ȣȭ ����
				upload(fileRename, encryptedFilePath, keyPath, keyPath); // ���� ��ȣȭ �� ���Ͽ� ���� ��ĪŰ ����
				
				// ��ȣȭ�� ���� ����
				File newFile = new File(encryptedFilePath);
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + fileId);

				http_post.addParam("username", name).addParam("password", password)
						.addParam("file", newFile) // ���ο� ����
						.submit();

			} else if (ch == 8) { // ����Ű �ٿ�ε�
				System.out.print("�ٿ�ε��� ����ID �Է�: ");
				String fileId = sc.nextLine();

				String keyPath = "./aeskey.txt";
				
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + fileId + "/"
						+ urlParameters, keyPath);

			} else if (ch == 9) { // ��ȣȭ ���
				System.out.print("��ȣȭ�� ���� ��ġ �Է�: ");
				String filePath = sc.nextLine();

				String keyPath = "./aeskey.txt";

				String privateKeyPath = "./private_" + name + ".txt";
				System.out.print("������ ������ ��ġ �Է�: ");
				String savePath = sc.nextLine();
				
				
				// ����� ���Ű �ҷ�����
				PrivateKey user_privatekey = RSA.LoadPrivateKeyPair(privateKeyPath);
				// ����� ���Ű�� ���� ��ĪŰ ��ȣȭ
				RSA.decryption(keyPath, keyPath, user_privatekey);
				// ���� ��ĪŰ�� ���� ��ȣȭ
				fileDecryption(filePath, savePath, keyPath);
				

			} else if (ch == 10) { // �α׾ƿ�
				name = "";
				password = "";
			}

		}
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

	private static void upload(String filePath, String encryptedFilePath, String keyPath)
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
	            }
	         }
	         
	         bufferedWriter.close();
	         // ���Ͽ� ���� ��ĪŰ ���ÿ� ����
	         String aeskey = aes.getKey(); // �Ϻ�ȣ �� ���Ǵ� key
	         File keyfile = new File(keyPath);
	         keyfile.createNewFile();   // ��ĪŰ ���� ����
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

	private static void upload(String filePath, String encryptedFilePath, String keyPath, String key)
            throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
         AES256Util aes = new AES256Util(key);
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
               }
            }
            
            bufferedWriter.close();
            // ���Ͽ� ���� ��ĪŰ ���ÿ� ����
            String aeskey = aes.getKey(); // �Ϻ�ȣ �� ���Ǵ� key
            File keyfile = new File(keyPath);
            keyfile.createNewFile();   // ��ĪŰ ���� ����
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
	
	private static void fileDecryption(String filePath, String savePath, String keyPath) 
	         throws UnsupportedEncodingException, NoSuchAlgorithmException, GeneralSecurityException {
	      AES256Util aes = new AES256Util();
	      try {
	         File infile = new File(filePath);
	         FileReader filereader = new FileReader(infile);
	         File outfile = new File(savePath);
	         BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outfile));
	         BufferedReader bufReader = new BufferedReader(filereader);
	         String line = "";
	         String plain;
	         while ((line = bufReader.readLine()) != null) {
	            plain = aes.decrypt(line, RSA.fileToString(keyPath)); // ��Īkey
	            if (outfile.isFile() && outfile.canWrite()) {
	               bufferedWriter.write(plain);
	               bufferedWriter.newLine();
	            }
	         }
	         bufferedWriter.close();
	         bufReader.close();
	      } catch (FileNotFoundException e) {
	      } catch (IOException e) {
	         System.out.println(e);
	      }
	   }
}
