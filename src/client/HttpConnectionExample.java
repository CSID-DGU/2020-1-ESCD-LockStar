package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class HttpConnectionExample { 
	static Scanner sc = new Scanner(System.in);
	private final String USER_AGENT = "Mozilla/5.0";
	
    public static void main(String[] args) throws Exception {
        HttpConnectionExample http = new HttpConnectionExample(); 
        int ch = 1;
        String urlParameters="";
        while(true){
        	System.out.print("메뉴선택(1: 회원가입, 2: 퍼블릭키 등록, 3: 파일 업로드, 4: 파일 다운로드): ");
            ch = sc.nextInt();
            sc.nextLine();
            if(ch==0){
                break;
            }
            else if(ch==1){		// 회원 가입
            	String name, password;
            	System.out.print("ID입력: ");
                name = sc.nextLine();
                System.out.print("password입력: ");
                password = sc.nextLine();
                
                // 생성할 아이디와 패스워드 서버에 전송
                Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name)
						 .addParam("password", password)
						 .submit();
                //urlParameters = "username="+name+"&password="+password;
                //http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add",urlParameters);
            }
            else if(ch==2){		// 공개키 등록
            	String name, password;
            	System.out.print("ID입력: ");
                name = sc.nextLine();
                System.out.print("password입력: ");
                password = sc.nextLine();
                
                KeyPair key = RSA.keyMake();	// 키 쌍 생성
				File filePublicKey = new File("./public.key");

				// 사용자 공개키 서버에 등록
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name)
						 .addParam("password", password)
						 .addParam("key", filePublicKey)
						 .submit();
				
			}
            else if(ch==3){		// 파일 업로드
            	String name, password, toName;
            	System.out.print("ID입력: ");
                name = sc.nextLine();
                System.out.print("password입력: ");
                password = sc.nextLine();
                System.out.print("권한을 보낼 ID 입력: ");
                toName = sc.nextLine();
                
                String filePath = "./upload.txt";
                String encryptedFilePath = "./encryptedUpload.txt";
            	File file = new File(filePath);			// 파일 생성
            	upload(filePath, encryptedFilePath);	// 파일 암호화 및 파일에 대한 대칭키 생성
            	String keyPath = "./aeskey.txt";		// 파일대 대한 대칭키 위치
            	
            	urlParameters = "username=" + name + "&password=" + password;
            	http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key"+urlParameters);
            	
            	// 대칭키 암호화 : 현재 publicKey를 서버에서 서버 공개키를 받아서 넣어야 함 : 미구현@@@@@@@@@@@@@@@@@
            	//File encryptedFileKey = new File(RSA.encryption("./uploadkey.txt", "./encryptedUploadKey", publicKey));
            	
            	// 암호화된 파일을 서버에 전송
            	//urlParameters = "file="+file+"&file_key="+encryptedFileKey;
                //http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file",urlParameters);
                
                // 암호화된 파일에 대해 권한을 가질 사용자 ID 정보를 서버에 전송
                urlParameters = "username=" + name + "&password=" + password + "&usernames=" + toName;
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow",urlParameters);
            }
            else if(ch==4){		// 파일 다운로드
            	System.out.print("다운로드할 파일명 입력: ");
            	String FileID = sc.nextLine();
            	System.out.print("ID입력: ");
            	String name = sc.nextLine();
            	System.out.print("password입력: ");
                String password = sc.nextLine();
                
                urlParameters = "/{"+FileID+"}?name="+name+"&password="+password;
                http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file"+urlParameters);
                //http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key" + urlKeyParameters);
            }
            else if(ch==5) {
            	//filePath = "./stringText.txt";
            	//encryptedFilePath = "./encryptedText.txt";
            	//decryptedFilePath = "./decryptedText.txt";
            	
            	System.out.println("암호화 하고자 하는 파일의 위치를 입력해주세요 : ");
            	String filePath = sc.nextLine();
            	System.out.println("암호화된 파일을 저장할 위치를 입력해주세요 : ");
            	String encryptedFilePath = sc.nextLine();
            	System.out.println("복호화된 파일을 저장할 위치를 입력해주세요 : ");
            	String decryptedFilePath = sc.nextLine();
            	
                KeyPair keyPair = RSA.keyMake();
                RSA.encryption(filePath, encryptedFilePath, keyPair.getPublic());
                RSA.decryption(encryptedFilePath, decryptedFilePath, keyPair.getPrivate());
            }
        } 
    } 
    
    private void sendPost(String targetUrl, String parameters) throws Exception {
    	URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();    
        con.setRequestMethod("POST"); // HTTP POST 메소드 설정
        con.setRequestProperty("User-Agent", "test"); 
        con.setDoOutput(true); // POST 파라미터 전달을 위한 설정 // 
        DataOutputStream wr = new DataOutputStream(con.getOutputStream()); 
        wr.writeBytes(parameters); 
        wr.flush(); 
        wr.close();
        try {
        	int responseCode = con.getResponseCode(); 
        	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
        	String inputLine; 
        	StringBuffer response = new StringBuffer(); 
        	while ((inputLine = in.readLine()) != null) {
        		response.append(inputLine); } in.close();
        		System.out.println("HTTP 응답 코드 : " + responseCode); 
        		System.out.println("HTTP body : " + response.toString()
        	);
        } catch(IOException e) {
        	int responseCode = con.getResponseCode();
        	System.out.println(responseCode+" 오류");
        }
    }
    
    private void sendGet(String targetUrl) throws Exception {
		URL url = new URL(null, targetUrl, new sun.net.www.protocol.https.Handler());
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
		} catch(IOException e) {
        	int responseCode = con.getResponseCode();
        	System.out.println(responseCode+" 오류");
        }
	}
    
    
	private static File makeKey() throws IOException {
		String public_key = "asdasd";	// 퍼블릭키 생성
		File key = new File("./key.txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(key, false);
			writer.write(public_key);
			writer.flush();
		} catch(IOException e) {	
		}
		return key;
    }

	private static void upload(String filePath, String encryptedFilePath) throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
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
