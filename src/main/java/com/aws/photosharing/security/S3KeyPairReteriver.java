package com.aws.photosharing.security;

import com.aws.photosharing.service.S3Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
@Getter
public class S3KeyPairReteriver implements  KeyPairReteriver {

    private final KeyPair keyPair;

    public S3KeyPairReteriver(@Autowired S3Service s3Service) throws Exception {
        byte privateKeyBytes[] = s3Service.getObject("admin", "privateKey");
        byte publicKeyBytes[] = s3Service.getObject("admin", "publicKey");

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        this.keyPair = new KeyPair(publicKey, privateKey);
    }
}
