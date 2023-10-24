import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignature {
    private static final String ALGORITHM = "SHA256withRSA";
    private static final int BUFFER_SIZE = 8192;

    public static String signFile(File file, String privateKeyStr) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        while ((len = bis.read(buffer)) >= 0) {
            md.update(buffer, 0, len);
        }
        bis.close();
        byte[] hash = md.digest();
        Signature sig = Signature.getInstance(ALGORITHM);
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        sig.initSign(privateKey);
        sig.update(hash);
        byte[] signature = sig.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verifySignedFile(File file, String publicKey, File signatureFile) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, InvalidKeySpecException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        while ((len = bis.read(buffer)) >= 0) {
            md.update(buffer, 0, len);
        }
        bis.close();
        byte[] hash = md.digest();
        byte[] signatureBytes = new byte[(int) signatureFile.length()];
        BufferedInputStream signatureBis = new BufferedInputStream(new FileInputStream(signatureFile));
        signatureBis.read(signatureBytes);
        signatureBis.close();
        byte[] signature = Base64.getDecoder().decode(signatureBytes);
        System.out.println(publicKey);
        PublicKey publicKeyObj = getPublicKey(publicKey.trim());
        Signature sig = Signature.getInstance(ALGORITHM);
        sig.initVerify(publicKeyObj);
        sig.update(hash);
        return sig.verify(signature);
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

    public static boolean verifySignature(byte[] data, byte[] signature, String publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature sig = Signature.getInstance(ALGORITHM);
        sig.initVerify(pubKey);
        sig.update(data);
        return sig.verify(signature);
    }

    
}
