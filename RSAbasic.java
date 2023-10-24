import java.math.BigInteger;

public class RSAbasic {

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        while (b.compareTo(BigInteger.ZERO) != 0) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }

    public static BigInteger[] extended_gcd(BigInteger a, BigInteger b) {
        BigInteger[] result = new BigInteger[3];
        BigInteger xa = BigInteger.ONE, xb = BigInteger.ZERO;
        BigInteger ya = BigInteger.ZERO, yb = BigInteger.ONE;
        while (b.compareTo(BigInteger.ZERO) != 0) { 
            BigInteger[] quotientAndRemainder = a.divideAndRemainder(b);
            BigInteger q = quotientAndRemainder[0];
            BigInteger r = quotientAndRemainder[1];
            a = b;
            b = r;
            BigInteger xr = xa.subtract(q.multiply(xb));
            BigInteger yr = ya.subtract(q.multiply(yb));
            xa = xb;
            ya = yb;
            xb = xr;
            yb = yr;
        }
        result[0] = a;
        result[1] = xa;
        result[2] = ya;
        return result;
    }

    public static BigInteger inverse_modulo(BigInteger a, BigInteger n) {
        BigInteger[] result = extended_gcd(a, n);
        if (!result[0].equals(BigInteger.ONE)) {
            return null;
        } else {
            BigInteger x = result[1];
            return x.mod(n);
        }
    }

    public static BigInteger[][] generate_key(BigInteger p, BigInteger q) {
        BigInteger[][] keys = new BigInteger[2][2];
        BigInteger modulus = p.multiply(q);
        BigInteger totient = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger public_exponent = BigInteger.valueOf(65537);
        while (gcd(public_exponent, totient).compareTo(BigInteger.ONE) != 0) {
            public_exponent = public_exponent.add(BigInteger.ONE);
        }
        BigInteger private_exponent = inverse_modulo(public_exponent, totient);
        BigInteger[] public_key = {modulus, public_exponent};
        BigInteger[] private_key = {modulus, private_exponent};
        keys[0] = public_key;
        keys[1] = private_key;
        return keys;
    }

    public static String encrypt(String msg, BigInteger[] public_key) {
        BigInteger modulus = public_key[0];
        BigInteger exponent = public_key[1];
        String cipher_text_string = "";
        for (int i = 0; i < msg.length(); i++) {
            char ch = msg.charAt(i);
            BigInteger ch_value = BigInteger.valueOf((int) ch);
            BigInteger cipher_value = ch_value.modPow(exponent, modulus);
            if (cipher_text_string.equals("")) {
                cipher_text_string = cipher_value.toString();
            } else {
                cipher_text_string += "-" + cipher_value.toString();
            }
        }
        return cipher_text_string;
    }

    public static String decrypt(String cipher_text, BigInteger[] private_key) {
        BigInteger modulus = private_key[0];
        BigInteger exponent = private_key[1];
        String[] cipher_text_parts = cipher_text.split("-");
        String decrypted_text_string = "";
        for (int i = 0; i < cipher_text_parts.length; i++) {
            BigInteger cipher_value = new BigInteger(cipher_text_parts[i]);
            BigInteger decrypted_value = cipher_value.modPow(exponent, modulus);
            char decrypted_char = (char) decrypted_value.intValue();
            decrypted_text_string += decrypted_char;
        }
        return decrypted_text_string;
    }

    public static void main(String[] args) {
        // Tạo hai số nguyên tố p và q
        BigInteger p = new BigInteger("61");
        BigInteger q = new BigInteger("53");

        // Tạo khóa công khai và khóa bí mật
        BigInteger[][] keys = generate_key(p, q);
        BigInteger[] public_key = keys[0];
        BigInteger[] private_key = keys[1];

        // In ra khóa công khai và khóa bí mật
        System.out.println("Public key: (" + public_key[0] + ", " + public_key[1] + ")");
        System.out.println("Private key: (" + private_key[0] + ", " + private_key[1] + ")");



        // Chuỗi cần mã hóa
        String message = "Hello, world!";

        String ciphertext = encrypt(message,public_key );
        System.out.println("Ciphertext: " + ciphertext);

        String plainTxt = decrypt( ciphertext, private_key );

        System.out.println("plain text: " + plainTxt);


    }
}