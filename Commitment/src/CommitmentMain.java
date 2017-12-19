import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommitmentMain {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void main(String[] arguments) {
        System.out.println("Implementing commitment routine described in lecture 1");
        String message = "this is the message";
        try {
            Commit c = commit(message);
            if (verify(c, message + "r")) {
                System.out.println("Original message");
            }
            else {
                System.out.println("Not original message");
            }
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Commit commit(String msg) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        String key = bytesToHex(encoded);
        msg = key + msg;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(msg.getBytes(StandardCharsets.UTF_8));

        String com = bytesToHex(encodedhash);

        Commit c = new Commit(com, key);

        return c;
    }


    private static boolean verify(Commit c, String msg) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        msg = c.getKey() + msg;

        return c.getCom().equals(bytesToHex(digest.digest(msg.getBytes(StandardCharsets.UTF_8))));
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}

class Commit {
    private String com;
    private String key;

    public Commit(String comm, String key) {
        this.com = comm;
        this.key = key;
    }

    public String getCom() {
        return com;
    }

    public String getKey() {
        return key;
    }
}
