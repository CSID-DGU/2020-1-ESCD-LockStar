package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

	/**
	 * RSA 키쌍을 생성합니다
	 * @return RSA 키쌍
	 * @throws IOException 
	 */
	public static KeyPair keyMake() throws NoSuchAlgorithmException, IOException {
		// RSA 키쌍을 생성합니다.
		KeyPair keyPair = CipherUtil.genRSAKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream("./public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream("./private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		
		return keyPair;
	}
	
	/**
	 * 로컬에 저장된 키를 load합니다.
	 * @param algorithm 입력
	 * @return
	 */
	public KeyPair LoadKeyPair(String algorithm)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File("./public.key");
		FileInputStream fis = new FileInputStream("./public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File("./private.key");
		fis = new FileInputStream("./private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}

	/**
	 * 입력받은 경로에 있는 파일을 암호화하여 암호화된 파일을 지정한 위치에 저장합니다.
	 * @param 암호화하고자 하는 파일의 경로
	 * @param 암호화된 파일을 저장할 위치
	 * @param Public Key
	 * @return 암호화된 파일의 위치
	 */
	public static String encryption(String filePath, String savePath, PublicKey publicKey) throws Exception {
		
		String plainText = fileToString(filePath); // 암호화 할 문자열 입니다.

		// Base64 인코딩된 암호화 문자열 입니다.
		String encrypted = CipherUtil.encryptRSA(plainText, publicKey);
		System.out.println("encrypted : ↓↓↓");
		System.out.println(encrypted + "\n");

		// 공개키를 Base64 인코딩한 문자일을 만듭니다.
//		byte[] bytePublicKey = publicKey.getEncoded();
//		String base64PublicKey = Base64.getEncoder().encodeToString(bytePublicKey);
//		System.out.println("Base64 Public Key : " + base64PublicKey);
		
		stringToFile(encrypted, savePath);

		return savePath;
	}

	/**
	 * 입력받은 경로에 있는 암호화된 파일을 복호화하여 복호화된 파일을 지정한 위치에 저장합니다.
	 * @param 복호화하고자 하는 파일의 경로
	 * @param 복호화된 파일을 저장할 위치
	 * @param Private Key
	 * @return 복호화된 파일의 위치
	 */
	public static String decryption(String filePath, String savePath, PrivateKey privateKey)
			throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
			IllegalBlockSizeException, IOException {
		
		String encrypted = fileToString(filePath);
		
		// 복호화 합니다.
		String decrypted = CipherUtil.decryptRSA(encrypted, privateKey);
		System.out.println("decrypted : ↓↓↓");
		System.out.println(decrypted + "\n");

		// 개인키를 Base64 인코딩한 문자열을 만듭니다.
//		byte[] bytePrivateKey = privateKey.getEncoded();
//		String base64PrivateKey = Base64.getEncoder().encodeToString(bytePrivateKey);
//		System.out.println("Base64 Private Key : " + base64PrivateKey);
		
		stringToFile(decrypted, savePath);

		return savePath;
	}

	/**
	 * File to String 메소드
	 * @param 파일의 경로
	 * @return String 반환
	 */
	public static String fileToString(String filePath) throws IOException {
		String content = "";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {

            // Formatting like \r\n will be lost
            // String content = lines.collect(Collectors.joining());

            // UNIX \n, WIndows \r\n
            content = lines.collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Contents : ↓↓↓");
            System.out.println(content + "\n");

			// File to List
            //List<String> list = lines.collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error...!!!");
        }
        return content;
	}
	
	/**
	 * String to File 메소드
	 * @param String
	 * @param 지정한 경로에 파일 저장
	 * @return 파일을 저장한 경로 반환
	 */
	public static String stringToFile(String str, String filePath) {

		try {
            Files.write(Paths.get(filePath), str.getBytes());
            // encoding
            // Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));

            // extra options
            // Files.write(Paths.get(path), content.getBytes(),
			//		StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return filePath;
	}
}