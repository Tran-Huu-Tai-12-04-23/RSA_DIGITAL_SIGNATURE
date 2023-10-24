import java.math.BigInteger;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;

public class RSA {
    private static final int KEY_SIZE = 2048;
    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;
    private Cipher cipher;

    public RSA() throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE);
        keyPair = keyPairGenerator.generateKeyPair();
        cipher = Cipher.getInstance("RSA");
    }

    public RSA(int p, int q) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        BigInteger pBig = new BigInteger("3541061913");
        BigInteger qBig = new BigInteger("3252305993");
    
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512); // độ dài khóa là 512 bit
        keyPair = keyPairGenerator.genKeyPair();
    
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(pBig.multiply(qBig), BigInteger.valueOf(65537));
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(qBig.multiply(pBig), BigInteger.valueOf(65537));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        cipher = Cipher.getInstance("RSA");
    }
    
    public String getPublicKey() {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    public String getPrivateKey() {
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public String encrypt(byte[] input, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
        byte[] encryptedBytes = cipher.doFinal(input);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String base64EncodedInput, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] input = Base64.getDecoder().decode(base64EncodedInput); 
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        byte[] decryptedBytes = cipher.doFinal(input);
        return new String(decryptedBytes);
    }

    public static PublicKey getPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    
    private static PrivateKey getPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

}
