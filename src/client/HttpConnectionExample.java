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
        	System.out.print("�޴�����(1: ȸ������, 2: �ۺ�Ű ���, 3: ���� ���ε�, 4: ���� �ٿ�ε�): ");
            ch = sc.nextInt();
            sc.nextLine();
            if(ch==0){
                break;
            }
            else if(ch==1){		// ȸ�� ����
            	String name, password;
            	System.out.print("ID�Է�: ");
                name = sc.nextLine();
                System.out.print("password�Է�: ");
                password = sc.nextLine();
                
                // ������ ���̵�� �н����� ������ ����
                Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add");
				http_post.addParam("username", name)
						 .addParam("password", password)
						 .submit();
                //urlParameters = "username="+name+"&password="+password;
                //http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/add",urlParameters);
            }
            else if(ch==2){		// ����Ű ���
            	String name, password;
            	System.out.print("ID�Է�: ");
                name = sc.nextLine();
                System.out.print("password�Է�: ");
                password = sc.nextLine();
                
                KeyPair key = RSA.keyMake();	// Ű �� ����
				File filePublicKey = new File("./public.key");

				// ����� ����Ű ������ ���
				Http http_post = new Http("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/key");
				http_post.addParam("username", name)
						 .addParam("password", password)
						 .addParam("key", filePublicKey)
						 .submit();
				
			}
            else if(ch==3){		// ���� ���ε�
            	String name, password, toName;
            	System.out.print("ID�Է�: ");
                name = sc.nextLine();
                System.out.print("password�Է�: ");
                password = sc.nextLine();
                System.out.print("������ ���� ID �Է�: ");
                toName = sc.nextLine();
                
                String filePath = "./upload.txt";
                String encryptedFilePath = "./encryptedUpload.txt";
            	File file = new File(filePath);			// ���� ����
            	upload(filePath, encryptedFilePath);	// ���� ��ȣȭ �� ���Ͽ� ���� ��ĪŰ ����
            	String keyPath = "./aeskey.txt";		// ���ϴ� ���� ��ĪŰ ��ġ
            	
            	urlParameters = "username=" + name + "&password=" + password;
            	http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/user/server_public_key"+urlParameters);
            	
            	// ��ĪŰ ��ȣȭ : ���� publicKey�� �������� ���� ����Ű�� �޾Ƽ� �־�� �� : �̱���@@@@@@@@@@@@@@@@@
            	//File encryptedFileKey = new File(RSA.encryption("./uploadkey.txt", "./encryptedUploadKey", publicKey));
            	
            	// ��ȣȭ�� ������ ������ ����
            	//urlParameters = "file="+file+"&file_key="+encryptedFileKey;
                //http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file",urlParameters);
                
                // ��ȣȭ�� ���Ͽ� ���� ������ ���� ����� ID ������ ������ ����
                urlParameters = "username=" + name + "&password=" + password + "&usernames=" + toName;
                http.sendPost("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/allow",urlParameters);
            }
            else if(ch==4){		// ���� �ٿ�ε�
            	System.out.print("�ٿ�ε��� ���ϸ� �Է�: ");
            	String FileID = sc.nextLine();
            	System.out.print("ID�Է�: ");
            	String name = sc.nextLine();
            	System.out.print("password�Է�: ");
                String password = sc.nextLine();
                
                urlParameters = "/{"+FileID+"}?name="+name+"&password="+password;
                http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file"+urlParameters);
                //http.sendGet("http://ec2-18-218-11-184.us-east-2.compute.amazonaws.com/file/key" + urlKeyParameters);
            }
            else if(ch==5) {
            	//filePath = "./stringText.txt";
            	//encryptedFilePath = "./encryptedText.txt";
            	//decryptedFilePath = "./decryptedText.txt";
            	
            	System.out.println("��ȣȭ �ϰ��� �ϴ� ������ ��ġ�� �Է����ּ��� : ");
            	String filePath = sc.nextLine();
            	System.out.println("��ȣȭ�� ������ ������ ��ġ�� �Է����ּ��� : ");
            	String encryptedFilePath = sc.nextLine();
            	System.out.println("��ȣȭ�� ������ ������ ��ġ�� �Է����ּ��� : ");
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
