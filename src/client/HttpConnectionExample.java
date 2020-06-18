package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
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
            else if(ch==1){
            	String name, password;
            	System.out.print("ID입력: ");
                name = sc.nextLine();
                System.out.print("password입력: ");
                password = sc.nextLine();
                urlParameters = "name="+name+"&password="+password;
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add",urlParameters);
            }
            else if(ch==2){
            	String name, password;
            	System.out.print("ID입력: ");
                name = sc.nextLine();
                System.out.print("password입력: ");
                password = sc.nextLine();
            	File key = makeKey();
                urlParameters = "name="+name+"&password="+password+"&key="+key;
                System.out.println(urlParameters);
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key",urlParameters);
            }
            else if(ch==3){
            	File file = new File("./upload.txt");; 
            	File file_key = upload();
                urlParameters = "file="+file+"&file_key="+file_key;
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file",urlParameters);
            }
            else if(ch==4){
            	System.out.print("다운로드할 파일명 입력: ");
            	String FileID = sc.nextLine();
            	System.out.print("ID입력: ");
            	String name = sc.nextLine();
            	 System.out.print("password입력: ");
                String password = sc.nextLine();
                urlParameters = "/{"+FileID+"}?name="+name+"&password="+password;
                http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file"+urlParameters);
            }
            else if(ch==5) {
            	String filePath = "";
            	String encryptedFilePath = "";
            	String decryptedFilePath = "";
            	//filePath = "./stringText.txt";
            	//encryptedFilePath = "./encryptedText.txt";
            	//decryptedFilePath = "./decryptedText.txt";
            	
            	System.out.println("암호화 하고자 하는 파일의 위치를 입력해주세요 : ");
            	filePath = sc.nextLine();
            	System.out.println("암호화된 파일을 저장할 위치를 입력해주세요 : ");
            	encryptedFilePath = sc.nextLine();
            	System.out.println("복호화된 파일을 저장할 위치를 입력해주세요 : ");
            	decryptedFilePath = sc.nextLine();
            	
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
	
	private static File upload() throws IOException {
		String upload_key = "qweqwe";	// 파일 업로드 시 키 생성
		File key = new File("./uploadkey.txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(key, false);
			writer.write(upload_key);
			writer.flush();
		} catch(IOException e) {	
		}
		return key;
    }
}
