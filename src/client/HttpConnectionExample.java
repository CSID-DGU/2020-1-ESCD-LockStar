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
					"\n ================= 메뉴선택  =================\n"
					+ " 1: 회원가입 \t2: 로그인 \t3: 파일 업로드 \n"
					+ " 4: 파일 목록 \t5: 파일 다운로드 \t6: 권한 전송 \n"
					+ " 7: 파일 수정 \t8: 파일 암호키 다운 \t9: 복호화 모듈 \n"
					+ " 10: 로그아웃 \t0: 프로그램 종료 \n"
					+ "입력 > ");
			ch = sc.nextInt();
			sc.nextLine();
			if (ch == 0) {
				System.out.println("===== 이용해 주셔서 감사합니다 :) =====");
				break;
			} else if (ch == 1) { // 회원 가입 및 사용자 공개키 서버에 등록
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();

				// 생성할 아이디와 패스워드 서버에 전송
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name).addParam("password", password).submit();
				
				TimeUnit.SECONDS.sleep(2);	// 3초간 대기
				
				RSA.keyMake("./public_" + name + ".txt", "./private_" + name + ".txt"); // 키 쌍 생성
				File filePublicKey = new File("./public_" + name + ".txt");
				
				// 사용자 공개키 서버에 등록
				http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name).addParam("password", password).addParam("key", filePublicKey)
						.submit();

			} else if (ch == 2) { // 로그인
				System.out.print("ID입력: ");
				name = sc.nextLine();
				System.out.print("password입력: ");
				password = sc.nextLine();

				
			} else if (ch == 3) { // 파일 업로드
				System.out.print("업로드할 파일 경로 입력 : ");
				String filePath = sc.nextLine();
				
				// 암호화된 파일 경로
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_encrypted");
				String encryptedFilePath = sb.toString();
				
				// 파일에 대한 대칭키 경로
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_aeskey");
				String keyPath = sb.toString();
				
				// 파일에 대한 대칭키의 비대칭키 경로
				sb = new StringBuffer(filePath);
				sb.insert(filePath.lastIndexOf("."), "_rsakey");
				String encryptedKeyPath = sb.toString();

				// 서버 공개키 경로
				String serverkeyPath = "./server_public.txt";
				
				upload(filePath, encryptedFilePath, keyPath); // 파일 암호화 및 파일에 대한 대칭키 생성

				// 서버로부터 서버에 대한 공개키 다운로드
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.downloadkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key/"
						+ urlParameters, serverkeyPath);
				
				PublicKey server_publicKey = RSA.LoadPublicKeyPair(serverkeyPath);

				// 대칭키 암호화
				File encryptedFileKey = new File(RSA.encryption(keyPath, encryptedKeyPath, server_publicKey));
				File encryptedFile = new File(encryptedFilePath);

				// 암호화된 파일 및 암호화된 대칭키를 서버에 전송
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file");
				http_post.addParam("username", name).addParam("password", password)
						.addParam("file", encryptedFile)		// 암호화된 파일 전송
						.addParam("file_key", encryptedFileKey) // 암호화된 대칭키 전송
						.submit();

			} else if (ch == 4) { // 다운로드할 수 있는 파일 확인
				// 다운로드 가능한 파일 목록 출력
				urlParameters = "username=" + name + "&password=" + password;
				Conn.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters);

			} else if (ch == 5) { // 파일 다운로드
				System.out.print("다운로드할 파일ID 입력: ");
				String FileID = sc.nextLine();
				
				urlParameters = FileID + "?username=" + name + "&password=" + password;
				String urlParameters1 = "username=" + name + "&password=" + password;
				Conn.download("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + urlParameters,
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/list?" + urlParameters1);

			} else if (ch == 6) { // 권한 전송
				String toName;
				int fileId;
				System.out.print("권한을 보낼 ID 입력: ");
				toName = sc.nextLine();
				System.out.print("권한을 줄 파일 ID 입력: ");
				fileId = sc.nextInt();

				Http http_post = new Http(
						"http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow/" + fileId);
				http_post.addParam("username", name).addParam("password", password)
						.addParam("usernames", toName) // 권한을 가질 사용자 ID 정보
						.submit();

			} else if (ch == 7) { // 파일 수정 요청
				String fileRename;
				int fileId;
				System.out.print("이름 수정할 파일 ID 입력: ");
				fileId = sc.nextInt();
				sc.nextLine();
				System.out.print("새로운 파일 위치 입력: ");
				fileRename = sc.nextLine();

				String keyPath = "./aeskey.txt";
				
				// 파일키 다운로드
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + fileId + "/"
						+ urlParameters, keyPath);
				
				String privateKeyPath = "./private_" + name + ".txt";
				// 사용자 비밀키 불러오기
				PrivateKey user_privatekey = RSA.LoadPrivateKeyPair(privateKeyPath);
				// 사용자 비밀키로 파일 대칭키 복호화
				RSA.decryption(keyPath, keyPath, user_privatekey);
				
				// 암호화된 파일 경로
				sb = new StringBuffer(fileRename);
				sb.insert(fileRename.lastIndexOf("."), "_encrypted");
				String encryptedFilePath = sb.toString();
				
				// 주어진 대칭키로 암호화 실행
				upload(fileRename, encryptedFilePath, keyPath, keyPath); // 파일 암호화 및 파일에 대한 대칭키 생성
				
				// 암호화된 파일 전송
				File newFile = new File(encryptedFilePath);
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/" + fileId);

				http_post.addParam("username", name).addParam("password", password)
						.addParam("file", newFile) // 새로운 파일
						.submit();

			} else if (ch == 8) { // 파일키 다운로드
				System.out.print("다운로드할 파일ID 입력: ");
				String fileId = sc.nextLine();

				String keyPath = "./aeskey.txt";
				
				urlParameters = "?username=" + name + "&password=" + password;
				Conn.symkey("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key/" + fileId + "/"
						+ urlParameters, keyPath);

			} else if (ch == 9) { // 복호화 모듈
				System.out.print("복호화할 파일 위치 입력: ");
				String filePath = sc.nextLine();

				String keyPath = "./aeskey.txt";

				String privateKeyPath = "./private_" + name + ".txt";
				System.out.print("파일을 저장할 위치 입력: ");
				String savePath = sc.nextLine();
				
				
				// 사용자 비밀키 불러오기
				PrivateKey user_privatekey = RSA.LoadPrivateKeyPair(privateKeyPath);
				// 사용자 비밀키로 파일 대칭키 복호화
				RSA.decryption(keyPath, keyPath, user_privatekey);
				// 파일 대칭키로 파일 복호화
				fileDecryption(filePath, savePath, keyPath);
				

			} else if (ch == 10) { // 로그아웃
				name = "";
				password = "";
			}

		}
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
	         // 파일에 대한 대칭키 로컬에 저장
	         String aeskey = aes.getKey(); // 암복호 시 사용되는 key
	         File keyfile = new File(keyPath);
	         keyfile.createNewFile();   // 대칭키 파일 생성
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
            // 파일에 대한 대칭키 로컬에 저장
            String aeskey = aes.getKey(); // 암복호 시 사용되는 key
            File keyfile = new File(keyPath);
            keyfile.createNewFile();   // 대칭키 파일 생성
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
	            plain = aes.decrypt(line, RSA.fileToString(keyPath)); // 대칭key
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
