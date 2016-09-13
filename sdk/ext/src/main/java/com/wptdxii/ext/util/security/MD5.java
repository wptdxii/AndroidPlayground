package com.wptdxii.ext.util.security;

import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要
 * Created by wptdxii on 2016/9/13 0013..
 */
public final class MD5 extends Digest {

    public MD5() throws NoSuchAlgorithmException {
        super("MD5");
    }
}
