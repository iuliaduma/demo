package com.example.demo.controller;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.demo.dto.key.ObjectDTO;
import com.example.demo.request.Request;
import com.example.demo.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/objects")
public class ObjectController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectService service;

    @GetMapping("/")
    public List<ObjectDTO> findAllObjects(){
        List<ObjectDTO> objects = service.findAllObjects();
        logger.info(String.format("There are %d objects", objects.size()));
        return objects;
    }

    @GetMapping("/{bucket}")
    public List<ObjectDTO> findAllObjectsByBucket(@PathVariable String bucket){
        List<ObjectDTO> objects = service.getObjectsByBucket(bucket);
        logger.info(String.format("Bucket %s contains %d objects", bucket, objects.size()));
        return objects;
    }

    @PostMapping("/upload")
    public ObjectDTO uploadObject(@RequestBody Request request){
        ObjectDTO object = service.uploadFileObject(request);
        if (object.equals(null)){
            logger.error("An error occurred while loading the object");
        }else {
            logger.info(String.format("Object %s was successfully uploaded", object.getKey()));
        }
        return object;
    }

    @PostMapping("/download")
    public String downloadObject(@RequestBody Request request){
        String url = service.downloadObject(request);
        logger.info("Object %s can be downloaded from %s", request.getObjKeyName(), url);
        return url;
    }
}
