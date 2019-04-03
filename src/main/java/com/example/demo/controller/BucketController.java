package com.example.demo.controller;

import com.example.demo.dto.key.BucketDTO;
import com.example.demo.request.Request;
import com.example.demo.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("buckets")
public class BucketController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BucketService service;

    @GetMapping("/")
    public List<BucketDTO> findAllBuckets(){
        List <BucketDTO> buckets = service.findAllBuckets();
        logger.info(String.format("There are %d buckets",buckets.size()));
        return buckets;
    }

    @PostMapping("/create")
    public BucketDTO create(@RequestBody Request request){
        logger.info(String.format("Create a new bucket"));
        BucketDTO bucket = service.createBucket(request);
        if (bucket.equals(null)){
            logger.error(String.format("An error occurred during the creation process"));
        }else {
            logger.info(String.format("Bucket %s was successfully created", bucket.getKey()));
        }
        return bucket;
    }
}
