package com.example.demo.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bucket")
public class BucketController {

    private final AmazonS3 s3;

    @Autowired
    public BucketController(AmazonS3 s3) {
        this.s3 = s3;
    }

    @GetMapping("/create")
    public String createBucket(@RequestParam("bucketName") String bucketName) {
        if (!s3.doesBucketExistV2(bucketName)) {
            s3.createBucket(bucketName);
        }
        return "created";
    }

    @GetMapping("/read")
    public Boolean readBucket(@RequestParam("bucketName") String bucketName) {
        return s3.doesBucketExistV2(bucketName);
    }

    @GetMapping("/list")
    public List<String> listBuckets(@RequestParam("bucketName") String bucketName) {
        return s3.listBuckets().stream()
                .map(Bucket::getName)
                .collect(Collectors.toList());
    }

    @GetMapping("/delete")
    public String deleteBucket(@RequestParam("bucketName") String bucketName) {
        s3.deleteBucket(bucketName);
        return "removed";
    }
}
