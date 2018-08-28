package com.example.demo.controller;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/encryption")
public class EncryptionController {

    private final KmsMasterKeyProvider kmsMasterKeyProvider;

    public EncryptionController(final KmsMasterKeyProvider kmsMasterKeyProvider) {
        this.kmsMasterKeyProvider = kmsMasterKeyProvider;
    }

    @GetMapping
    @RequestMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestParam("data") String data) {
        final AwsCrypto crypto = new AwsCrypto();

        final Map<String, String> context = Collections.singletonMap("Example", "String");

        final String ciphertext = crypto.encryptString(kmsMasterKeyProvider, data, context).getResult();
        return ResponseEntity.ok(ciphertext);
    }

    @GetMapping
    @RequestMapping("/decrypt")
    public ResponseEntity<String> decrypt() {
        String data = "AYADeG11h416qC5Ay/Bx2L/sg9kAcAACAAdFeGFtcGxlAAZTdHJpbmcAFWF3cy1jcnlwdG8tcHVibGljLWtleQBEQWhWVmJYYUdLR052TjB2UHJySlBGSEpEMmZvNWFIZ3dEaUxWelNUeldUQlRMMEJYU3M2WU1PdTR6Mmg3SmpML3Z3PT0AAQAHYXdzLWttcwBOYXJuOmF3czprbXM6ZXUtY2VudHJhbC0xOjY0MTUyMjU2NTY2MDprZXkvMWE1NjQwZTItMGRkZS00MDExLWI1NDEtYzJiOTYyNzRlZmZmALgBAgEAeBluiI0q5a/jLJRDIVOCwqMa5i5TxEiJeu+bNgcbqZWQAZX9f8tVwNnC6Jja0ixD8qQAAAB+MHwGCSqGSIb3DQEHBqBvMG0CAQAwaAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAzxFZgehV114jvSxQkCARCAOzNW4rK4BOTFIgQKhFCTuBGpQCR3sVH5eeBZOJut2D/puNYyQpfZor9cjkqzqe2ZzlGwvJ7xRZt/OwVvAgAAAAAMAAAQAAAAAAAAAAAAAAAAAPj3eXFsTGd/yUYFw8wc2JT/////AAAAAQAAAAAAAAAAAAAAAQAAAANn9xKg0Owzgf05gr+UabcuFIBVAGcwZQIxALQcp1q1EKSg0z1hzMtXliesxz7B3FJBRRGR1T1MZnGt77UvH4exsXhpmbTtrABYsgIwA6oNzktYUUFlOS9RbJ9wS+rYkjlKXfIM8NEU4Z+1bzQKKktLtChKO6K3vGWmLNbl";
        final AwsCrypto crypto = new AwsCrypto();
        final CryptoResult<String, KmsMasterKey> decryptResult = crypto.decryptString(kmsMasterKeyProvider, data);

        final Map<String, String> context = Collections.singletonMap("Example", "String");

        for (final Map.Entry<String, String> e : context.entrySet()) {
            if (!e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey()))) {
                throw new IllegalStateException("Wrong Encryption Context!");
            }
        }

        return ResponseEntity.ok(decryptResult.getResult());
    }
}
