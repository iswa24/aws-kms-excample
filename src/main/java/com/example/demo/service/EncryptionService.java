package com.example.demo.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoInputStream;
import com.amazonaws.encryptionsdk.jce.JceMasterKey;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;

@Service
public class EncryptionService {

    @Value("${keyArn}")
    private String keyId;

    private final AmazonS3 s3;

    private final AWSKMS client;

    public EncryptionService(AmazonS3 s3, AWSKMS client) {
        this.s3 = s3;
        this.client = client;
    }

    public void upload(String bucketName, String key) {
        try {
            String srcFile = "test.pdf";
            SecretKey cryptoKey = retrieveEncryptionKey();

            JceMasterKey masterKey = JceMasterKey.getInstance(cryptoKey, "Example", keyId, "AES/GCM/NoPadding");
            AwsCrypto crypto = new AwsCrypto();
            Map<String, String> context = Collections.singletonMap("Example", "FileStreaming");
            FileInputStream in = new FileInputStream(srcFile);

            CryptoInputStream<JceMasterKey> encryptingStream = crypto.createEncryptingStream(masterKey, in, context);

            ObjectMetadata metadata = new ObjectMetadata();

            s3.putObject(bucketName, key, encryptingStream, metadata);
        } catch (
                FileNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    private SecretKey retrieveEncryptionKey() {
        SecureRandom rnd = new SecureRandom();

        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(keyId);
        dataKeyRequest.setKeySpec("AES_256");

        GenerateDataKeyResult generateDataKeyResult = client.generateDataKey(dataKeyRequest);

        ByteBuffer plaintext = generateDataKeyResult.getPlaintext();
        byte[] rawKey = plaintext.array();
        rnd.nextBytes(rawKey);
        return new SecretKeySpec(rawKey, "AES");
    }
}
