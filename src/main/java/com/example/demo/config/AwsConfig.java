package com.example.demo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

@Configuration
public class AwsConfig {

    @Bean
    public KmsMasterKeyProvider kmsMasterKeyProvider(AWSCredentialsProvider awsCredentialsProvider, @Value("${keyArn}") String keyArn) {
        return new KmsMasterKeyProvider(awsCredentialsProvider, keyArn);
    }

    @Bean
    public AWSKMS client(AWSCredentialsProvider awsCredentialsProvider, RegionProvider regionProvider) {
        return AWSKMSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(regionProvider.getRegion().getName())
                .build();
    }

    @Bean
    public CryptoConfiguration cryptoConfiguration(RegionProvider regionProvider) {
        return new CryptoConfiguration().withAwsKmsRegion(regionProvider.getRegion());
    }

    @Bean
    public KMSEncryptionMaterialsProvider materialsProvider(@Value("${keyArn}") String keyArn) {
        return new KMSEncryptionMaterialsProvider(keyArn);
    }

    @Bean("newAmazonS3Encryption")
    @Primary
    public AmazonS3Encryption amazonS3Encryption(AWSCredentialsProvider awsCredentialsProvider, RegionProvider regionProvider,
                                                 KMSEncryptionMaterialsProvider materialsProvider, CryptoConfiguration cryptoConfiguration) {
        return AmazonS3EncryptionClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withEncryptionMaterials(materialsProvider)
                .withCryptoConfiguration(cryptoConfiguration)
                .withRegion(regionProvider.getRegion().getName()).build();
    }

//    @Bean
//    public AWSCredentialsProvider awsCredentialsProvider() {
//        return new AWSCredentialsProviderChain(Arrays.asList(new EC2ContainerCredentialsProviderWrapper(), new EnvironmentVariableCredentialsProvider()));
//    }
}
