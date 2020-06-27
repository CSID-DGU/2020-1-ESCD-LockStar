package client;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class AES256Util {
    private String iv;
    private Key keySpec;
    public String key;
    public AES256Util() throws UnsupportedEncodingException {
       StringBuffer temp = new StringBuffer();
       Random rnd = new Random();
       for (int i = 0; i < 16; i++) {
           int rIndex = rnd.nextInt(3);
           switch (rIndex) {
           case 0:
               temp.append((char) ((int) (rnd.nextInt(26)) + 97));
               break;
           case 1:
               temp.append((char) ((int) (rnd.nextInt(26)) + 65));
               break;
           case 2:
               temp.append((rnd.nextInt(10)));
               break;
           }
       }
       key = temp.toString();
       this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len > keyBytes.length){
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        this.keySpec = keySpec;
   }

   /**
     * AES256 으로 암호화 한다.
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encode(encrypted));
        return enStr;
    }

    public String encrypt(String str, String key) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
        this.iv = key.substring(0, 16);
         byte[] keyBytes = new byte[16];
         byte[] b = key.getBytes("UTF-8");
         int len = b.length;
         if(len > keyBytes.length){
             len = keyBytes.length;
         }
         System.arraycopy(b, 0, keyBytes, 0, len);
         SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
         this.keySpec = keySpec;
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
         c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
         byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
         String enStr = new String(Base64.encode(encrypted));
         return enStr;
     }
    
    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String str, String key) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
        this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len > keyBytes.length){
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        this.keySpec = keySpec;
       Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] byteStr = Base64.decode(str);
        return new String(c.doFinal(byteStr), "UTF-8");
    }
    
    public String getKey() {
       return key;
    }
}