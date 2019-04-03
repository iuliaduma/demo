package com.example.demo.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.example.demo.config.Sns;
import com.example.demo.data.key.BucketKey;
import com.example.demo.data.key.ObjectKey;
import com.example.demo.dto.key.BucketDTO;
import com.example.demo.dto.key.ObjectDTO;
import com.example.demo.mapping.GenericModelMapper;
import com.example.demo.repository.ObjectKeyRepository;
import com.example.demo.request.Request;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ObjectService {

    private static final Logger logger = LoggerFactory.getLogger(ObjectService.class);

    private final ObjectKeyRepository objectRepository;

    private final GenericModelMapper objectMapper;

    private final GenericModelMapper bucketMapper;

    private final BucketService bucketService;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    public ObjectDTO uploadFileObject(Request request) {

        Subsegment subsegment = AWSXRay.beginSubsegment("Create File Object");
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(request.getClientRegion())
                    .build();

            PutObjectRequest objectRequest = new PutObjectRequest(request.getBucketName(), request.getObjKeyName(), new File(request.getPath()));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain");
            metadata.addUserMetadata("x-amz-meta-title", "updated");
            objectRequest.setMetadata(metadata);
            s3Client.putObject(objectRequest);

//            s3Client.putObject(request.getBucketName(), request.getObjKeyName(), "User updated");

            ObjectKey objectKey = getObjectKey(request);
//            Sns.sendNotification(String.format("Upload file object %s in bucket %s", request.getObjKeyName(), request.getBucketName()), "succes");
            return objectMapper.map(objectKey, ObjectDTO.class);

        } catch (NullPointerException | SdkClientException exception){
            logger.error(exception.getLocalizedMessage(), exception);
            subsegment.addException(exception);
//            Sns.sendNotification(String.format("Error: Upload file object %s in bucket %s", request.getObjKeyName(), request.getBucketName()), exception.getLocalizedMessage());
        } finally {
            AWSXRay.endSubsegment();
        }
        return null;
    }

    public ObjectDTO uploadDetailsObject(Request request) {

        Subsegment subsegment = AWSXRay.beginSubsegment("Create Details Object");
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(request.getClientRegion())
                    .build();

            s3Client.putObject(request.getBucketName(), request.getObjKeyName(), request.getDetails());

            ObjectKey objectKey = getObjectKey(request);
//            Sns.sendNotification(String.format("Upload details object %s in bucket %s", request.getObjKeyName(), request.getBucketName()), "succes");
            return objectMapper.map(objectKey, ObjectDTO.class);

        } catch (NullPointerException | SdkClientException exception){
            logger.error(exception.getLocalizedMessage(), exception);
            subsegment.addException(exception);
//            Sns.sendNotification(String.format("Error: Upload details object %s in bucket %s", request.getObjKeyName(), request.getBucketName()), exception.getLocalizedMessage());
        } finally {
            AWSXRay.endSubsegment();
        }
        return null;
    }

    public String downloadObject(Request request) {

        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(request.getClientRegion())
                    .build();

            s3Client.getUrl(request.getBucketName(), request.getObjKeyName());
            S3Object object = s3Client.getObject(request.getBucketName(), request.getObjKeyName());
            logger.info(String.format("Download object %s", request.getObjKeyName()));
            URL url = s3Client.getUrl(request.getBucketName(), request.getObjKeyName());
            return url.getAuthority() + url.getPath();

        } catch (SdkClientException exception) {
            logger.error(exception.getLocalizedMessage(), exception);
        }

        return null;
    }

    public List<ObjectDTO> getObjectsByBucket(String bucket) {
        BucketDTO bucketKey = BucketDTO.builder().clientRegion(Regions.EU_CENTRAL_1.getName()).key(bucket).build();
        bucketKey.setObjects(null);
        List<S3ObjectSummary> objects;
        List<ObjectDTO> objectList = new ArrayList<>();

        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(bucketKey.getClientRegion())
                    .build();
            ListObjectsV2Result result = s3Client.listObjectsV2(bucket);
            objects = result.getObjectSummaries();

            logger.info(String.format("There are %d objects", objects.size()));
            for (S3ObjectSummary object : objects) {
                objectList.add(ObjectDTO.builder()
                        .bucket(bucketKey)
                        .key(object.getKey())
                        .build());
                logger.info(String.format("Object %s from bucket %s last modified %s with storageClass %s ",
                        object.getKey(), object.getBucketName(),
                        object.getLastModified().toString(), object.getStorageClass()));
            }
        } catch (SdkClientException exception) {
            logger.error(exception.getLocalizedMessage(), exception);
        }

        return objectList;
    }

    public ObjectDTO getObjectFromBucket(String objectName, String bucket){
        List<ObjectDTO> objects = getObjectsByBucket(bucket);
        for (ObjectDTO object : objects) {
            if (object.getKey().equals(objectName)){
                return object;
            }
        }
        return null;
    }

    public List<ObjectDTO> findAllObjects() {
        List<ObjectDTO> objectKeys = new ArrayList<>();
        List<S3ObjectSummary> objects;

        try{
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.EU_CENTRAL_1)
                    .build();
            List<Bucket> buckets = s3Client.listBuckets();
            System.out.println("My buckets now are:");

            for (Bucket bucket : buckets) {
                ListObjectsV2Result result = s3Client.listObjectsV2(bucket.getName());
                objects = result.getObjectSummaries();
                BucketDTO bucketDTO = BucketDTO.builder().clientRegion(Regions.EU_CENTRAL_1.getName()).key(bucket.getName()).build();
                for (S3ObjectSummary object : objects) {
                    objectKeys.add(ObjectDTO.builder().key(object.getKey()).bucket(bucketDTO).build());
                }
            }
        }catch (SdkClientException exception){
            exception.printStackTrace();
        }
        return objectKeys;
    }

    private ObjectKey getObjectKey(Request request) {
        BucketKey bucketKey = bucketMapper.map(bucketService.getBucketFromS3(request.getBucketName()), BucketKey.class);
        ObjectKey objectKey = objectMapper.map(getObjectFromBucket(request.getObjKeyName(), request.getBucketName()), ObjectKey.class);
        if (objectKey == null) {
            objectKey = objectRepository.save(ObjectKey.builder().bucket(bucketKey).key(request.getObjKeyName()).build());
        }
        logger.info(String.format("%s updated", request.getObjKeyName()));
        return objectKey;
    }

}
