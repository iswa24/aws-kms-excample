package com.example.demo.controller;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/object")
public class ObjectController {

    private final AmazonS3Encryption amazonS3Encryption;

    private final String SOME_KEY = "someKey";

    @Value("${keyArn}")
    private String keyId;

    private final AWSKMS client;

    @Autowired
    public ObjectController(AmazonS3Encryption amazonS3Encryption, AWSKMS client) {
        this.amazonS3Encryption = amazonS3Encryption;
        this.client = client;
    }

    @GetMapping("/upload")
    public String upload(@RequestParam("bucketName") String bucketName) {
        try {
            String srcFile = "test.pdf";
//            SecureRandom rnd = new SecureRandom();
//            // Generate data key
//            GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
//            dataKeyRequest.setKeyId(keyId);
//            dataKeyRequest.setKeySpec("AES_256");
//
//            GenerateDataKeyResult generateDataKeyResult = client.generateDataKey(dataKeyRequest);
//
//            ByteBuffer plaintext = generateDataKeyResult.getPlaintext();
//            byte[] rawKey = plaintext.array();
//            rnd.nextBytes(rawKey);
//            SecretKey cryptoKey = new SecretKeySpec(rawKey, "AES");
//
//            JceMasterKey masterKey = JceMasterKey.getInstance(cryptoKey, "Example", keyId, "AES/GCM/NoPadding");
//            AwsCrypto crypto = new AwsCrypto();
//            Map<String, String> context = Collections.singletonMap("Example", "FileStreaming");
            FileInputStream in = new FileInputStream(srcFile);

//            CryptoInputStream<JceMasterKey> encryptingStream = crypto.createEncryptingStream(masterKey, in, context);
//
            ObjectMetadata metadata = new ObjectMetadata();

//            ByteBuffer ciphertextBlob = generateDataKeyResult.getCiphertextBlob();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, SOME_KEY, in, metadata);

            amazonS3Encryption.putObject(putObjectRequest);
            return "uploaded";
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    @GetMapping("/list")
    public List<String> listObjects(@RequestParam("bucketName") String bucketName) {
        ListObjectsV2Result listObjectsV2Result = amazonS3Encryption.listObjectsV2(bucketName);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            System.out.println("* " + objectSummary.getKey());
        }
        return objectSummaries.stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadObjects(@RequestParam("bucketName") String bucketName) {
        S3Object object = amazonS3Encryption.getObject(bucketName, SOME_KEY);
        S3ObjectInputStream objectContent = object.getObjectContent();

//        SecretKey cryptoKey = retrieveEncryptionKey();
//
//        AwsCrypto crypto = new AwsCrypto();
//
//        JceMasterKey masterKey = JceMasterKey.getInstance(cryptoKey, "Example", keyId, "AES/GCM/NoPadding");
//
//        CryptoInputStream<JceMasterKey> decryptingStream = crypto.createDecryptingStream(masterKey, objectContent);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf")
                .body(new InputStreamResource(objectContent));
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
