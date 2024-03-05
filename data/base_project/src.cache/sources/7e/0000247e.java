package java.security;

import java.util.Random;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecureRandom.class */
public class SecureRandom extends Random {
    public SecureRandom() {
        throw new RuntimeException("Stub!");
    }

    public SecureRandom(byte[] seed) {
        throw new RuntimeException("Stub!");
    }

    protected SecureRandom(SecureRandomSpi secureRandomSpi, Provider provider) {
        throw new RuntimeException("Stub!");
    }

    public static SecureRandom getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static SecureRandom getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static SecureRandom getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setSeed(byte[] seed) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Random
    public void setSeed(long seed) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Random
    public synchronized void nextBytes(byte[] bytes) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Random
    protected final int next(int numBits) {
        throw new RuntimeException("Stub!");
    }

    public static byte[] getSeed(int numBytes) {
        throw new RuntimeException("Stub!");
    }

    public byte[] generateSeed(int numBytes) {
        throw new RuntimeException("Stub!");
    }
}