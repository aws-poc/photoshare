package com.aws.photosharing.security;

import org.junit.Test;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class JwtHelperTest {

    @Test
    public void test() {
        JwtHelper jwtHelper = new JwtHelper(() -> {
            try {
                return KeyPairGenerator.getInstance("RSA").generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        });

        String jwt = jwtHelper.create("ABC");

        String userName = jwtHelper.getUserName(jwt);

        assertEquals("ABC", userName);
    }
}
