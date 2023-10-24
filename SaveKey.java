import java.io.FileOutputStream;
import java.io.IOException;

public class SaveKey {
 
    public static void saveKey(String privateKey, String publicKey) {
        try (FileOutputStream fos = new FileOutputStream("key/private_key.pem")) {
            fos.write(privateKey.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream("key/public_key.pem")) {
            fos.write(publicKey.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        saveKey("hello","holalasdas");
    }
}
