package testcode.sparrow;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class UnsaltedOneWayHash {
    public UnsaltedOneWayHash() {
    }

    public void bad() throws Throwable {
        MessageDigest hash = MessageDigest.getInstance("SHA-512");
        byte[] hashValue = hash.digest("hash me".getBytes("UTF-8"));
        System.out.println(hashValue);
    }

    public void good() throws Throwable {
        this.good1();
    }

    private void good1() throws Throwable {
        MessageDigest hash = MessageDigest.getInstance("SHA-512");
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        hash.update(prng.generateSeed(32));
        byte[] hashValue = hash.digest("hash me".getBytes("UTF-8"));
        System.out.println(hashValue);
    }
}
