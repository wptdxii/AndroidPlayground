package com.wptdxii.ext.util.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest packaging with {@link MessageDigest}.
 *
 * Created by wptdxii on 2016/9/13 0013.
 */
public class Digest {

    private static final char[] digits = new char[]{
            '0', '1', '2', '3', '4',//
            '5', '6', '7', '8', '9',//
            'a', 'b', 'c', 'd', 'e',//
            'f'};

    private final MessageDigest mDigest;

    /**
     * Construct a Digest with corresponding algorithm.
     *
     * @param algorithm Digest algorithm.
     * @throws NoSuchAlgorithmException
     */
    public Digest(String algorithm) throws NoSuchAlgorithmException {
        mDigest = MessageDigest.getInstance(algorithm);
    }

    /**
     * Compute the digest for input byte array.
     *
     * @param source Input byte array.
     * @return Digest for input byte array.
     */
    public byte[] digest(byte[] source) {
        if (source == null) {
            return null;
        }
        try {
            return mDigest.digest(source);
        } finally {
            mDigest.reset();
        }
    }

    /**
     * Compute the digest for input string.
     *
     * @param source Input string.
     * @return Digest for input string.
     */
    public String digest(String source) {
        if (source == null) {
            return null;
        }
        return bytes2HexStr(digest(source.getBytes()));
    }

    /**
     * Compute the digest for input stream.
     *
     * @param is Input stream.
     * @return Digest for input stream.
     */
    public String digest(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        try {
            int count;
            byte[] buffer = new byte[1024];
            while ((count = is.read(buffer)) > 0) {
                mDigest.update(buffer, 0, count);
            }
            return bytes2HexStr(mDigest.digest());

        } finally {
            mDigest.reset();
        }
    }

    private static String bytes2HexStr(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        char[] buf = new char[2 * bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            buf[2 * i + 1] = digits[b & 0xF];
            b = (byte) (b >>> 4);
            buf[(2 * i)] = digits[b & 0xF];
        }
        return new String(buf);
    }
}
