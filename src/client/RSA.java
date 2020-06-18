package client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
	 */
	public static KeyPair keyMake() throws NoSuchAlgorithmException {
		// RSA Ű���� �����մϴ�.
		KeyPair keyPair = CipherUtil.genRSAKeyPair();
		// PublicKey publicKey = keyPair.getPublic();
		// PrivateKey privateKey = keyPair.getPrivate();

		return keyPair;
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