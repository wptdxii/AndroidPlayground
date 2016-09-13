package com.wptdxii.ext.util.security;

import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要
 * Created by wptdxii on 2016/9/13 0013.
 */
public final class SHA1 extends Digest {

    public SHA1() throws NoSuchAlgorithmException {
        super("SHA-1");
    }
}
