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
	 * RSA Ű���� �����մϴ�
	 * @return RSA Ű��
	 * @throws IOException 
	 */
	public static KeyPair keyMake() throws NoSuchAlgorithmException, IOException {
		// RSA Ű���� �����մϴ�.
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
	 * ���ÿ� ����� Ű�� load�մϴ�.
	 * @param algorithm �Է�
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
	 * �Է¹��� ��ο� �ִ� ������ ��ȣȭ�Ͽ� ��ȣȭ�� ������ ������ ��ġ�� �����մϴ�.
	 * @param ��ȣȭ�ϰ��� �ϴ� ������ ���
	 * @param ��ȣȭ�� ������ ������ ��ġ
	 * @param Public Key
	 * @return ��ȣȭ�� ������ ��ġ
	 */
	public static String encryption(String filePath, String savePath, PublicKey publicKey) throws Exception {
		
		String plainText = fileToString(filePath); // ��ȣȭ �� ���ڿ� �Դϴ�.

		// Base64 ���ڵ��� ��ȣȭ ���ڿ� �Դϴ�.
		String encrypted = CipherUtil.encryptRSA(plainText, publicKey);
		System.out.println("encrypted : ����");
		System.out.println(encrypted + "\n");

		// ����Ű�� Base64 ���ڵ��� �������� ����ϴ�.
//		byte[] bytePublicKey = publicKey.getEncoded();
//		String base64PublicKey = Base64.getEncoder().encodeToString(bytePublicKey);
//		System.out.println("Base64 Public Key : " + base64PublicKey);
		
		stringToFile(encrypted, savePath);

		return savePath;
	}

	/**
	 * �Է¹��� ��ο� �ִ� ��ȣȭ�� ������ ��ȣȭ�Ͽ� ��ȣȭ�� ������ ������ ��ġ�� �����մϴ�.
	 * @param ��ȣȭ�ϰ��� �ϴ� ������ ���
	 * @param ��ȣȭ�� ������ ������ ��ġ
	 * @param Private Key
	 * @return ��ȣȭ�� ������ ��ġ
	 */
	public static String decryption(String filePath, String savePath, PrivateKey privateKey)
			throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
			IllegalBlockSizeException, IOException {
		
		String encrypted = fileToString(filePath);
		
		// ��ȣȭ �մϴ�.
		String decrypted = CipherUtil.decryptRSA(encrypted, privateKey);
		System.out.println("decrypted : ����");
		System.out.println(decrypted + "\n");

		// ����Ű�� Base64 ���ڵ��� ���ڿ��� ����ϴ�.
//		byte[] bytePrivateKey = privateKey.getEncoded();
//		String base64PrivateKey = Base64.getEncoder().encodeToString(bytePrivateKey);
//		System.out.println("Base64 Private Key : " + base64PrivateKey);
		
		stringToFile(decrypted, savePath);

		return savePath;
	}

	/**
	 * File to String �޼ҵ�
	 * @param ������ ���
	 * @return String ��ȯ
	 */
	public static String fileToString(String filePath) throws IOException {
		String content = "";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {

            // Formatting like \r\n will be lost
            // String content = lines.collect(Collectors.joining());

            // UNIX \n, WIndows \r\n
            content = lines.collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Contents : ����");
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
	 * String to File �޼ҵ�
	 * @param String
	 * @param ������ ��ο� ���� ����
	 * @return ������ ������ ��� ��ȯ
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