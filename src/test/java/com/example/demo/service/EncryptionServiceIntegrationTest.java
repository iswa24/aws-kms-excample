package com.example.demo.service;

import com.example.demo.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "development")
@ContextConfiguration(classes = { DemoApplication.class })
public class EncryptionServiceIntegrationTest {

    @Autowired
    private EncryptionService encryptionService;

    private final String BUCKET_NAME = "test-baumeister-document-upload-78";

    private final String KEY = "someKey";

    public EncryptionServiceIntegrationTest() {
    }

    @Test
    public void uploadTest() {
        encryptionService.upload(BUCKET_NAME, KEY);
    }
}