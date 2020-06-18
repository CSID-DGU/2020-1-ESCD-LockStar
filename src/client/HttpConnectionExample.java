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
        	System.out.print("�޴�����(1: ȸ������, 2: �ۺ�Ű ���, 3: ���� ���ε�, 4: ���� �ٿ�ε�): ");
            ch = sc.nextInt();
            sc.nextLine();
            if(ch==0){
                break;
            }
            else if(ch==1){
            	String name, password;
            	System.out.print("ID�Է�: ");
                name = sc.nextLine();
                System.out.print("password�Է�: ");
                password = sc.nextLine();
                urlParameters = "name="+name+"&password="+password;
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add",urlParameters);
            }
            else if(ch==2){
            	String name, password;
            	System.out.print("ID�Է�: ");
                name = sc.nextLine();
                System.out.print("password�Է�: ");
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
            	System.out.print("�ٿ�ε��� ���ϸ� �Է�: ");
            	String FileID = sc.nextLine();
            	System.out.print("ID�Է�: ");
            	String name = sc.nextLine();
            	 System.out.print("password�Է�: ");
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
            	
            	System.out.println("��ȣȭ �ϰ��� �ϴ� ������ ��ġ�� �Է����ּ��� : ");
            	filePath = sc.nextLine();
            	System.out.println("��ȣȭ�� ������ ������ ��ġ�� �Է����ּ��� : ");
            	encryptedFilePath = sc.nextLine();
            	System.out.println("��ȣȭ�� ������ ������ ��ġ�� �Է����ּ��� : ");
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
        con.setRequestMethod("POST"); // HTTP POST �޼ҵ� ����
        con.setRequestProperty("User-Agent", "test"); 
        con.setDoOutput(true); // POST �Ķ���� ������ ���� ���� // 
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
        		System.out.println("HTTP ���� �ڵ� : " + responseCode); 
        		System.out.println("HTTP body : " + response.toString()
        	);
        } catch(IOException e) {
        	int responseCode = con.getResponseCode();
        	System.out.println(responseCode+" ����");
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
			System.out.println("HTTP ���� �ڵ� : " + responseCode);
			System.out.println("HTTP body : " + response.toString());
		} catch(IOException e) {
        	int responseCode = con.getResponseCode();
        	System.out.println(responseCode+" ����");
        }
	}
    
    
	private static File makeKey() throws IOException {
		String public_key = "asdasd";	// �ۺ�Ű ����
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
		String upload_key = "qweqwe";	// ���� ���ε� �� Ű ����
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
