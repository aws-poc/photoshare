package com.aws.photosharing.security;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface KeyPairReteriver {
    KeyPair getKeyPair();
}
