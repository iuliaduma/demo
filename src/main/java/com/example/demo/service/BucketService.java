package com.example.demo.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.example.demo.config.Sns;
import com.example.demo.data.key.BucketKey;
import com.example.demo.mapping.GenericModelMapper;
import com.example.demo.repository.BucketKeyRepository;
import com.example.demo.request.Request;
import com.example.demo.dto.key.BucketDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BucketService {

    private static final Logger logger = LoggerFactory.getLogger(BucketService.class);

    private final BucketKeyRepository bucketRepository;

    private final GenericModelMapper bucketMapper;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    public BucketDTO createBucket(Request request) {

        Subsegment subsegment = AWSXRay.beginSubsegment("Create Bucket");
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(request.getClientRegion())
                    .build();

            if (!s3Client.doesBucketExistV2(request.getBucketName())) {
                s3Client.createBucket(new CreateBucketRequest(request.getBucketName()));
                BucketKey bucketKey = bucketRepository.save(BucketKey.builder().clientRegion(request.getClientRegion()).key(request.getBucketName()).build());
                bucketKey.setObjectKeys(null);
                logger.info(String.format("Bucket %s created ", request.getBucketName()));
//                Sns.sendNotification(String.format("Create bucket %s", request.getBucketName()), "succes");
                return bucketMapper.map(bucketKey, BucketDTO.class);
            }
        } catch (SdkClientException exception) {
            logger.error(exception.getLocalizedMessage(), exception);
            subsegment.addException(exception);
//            Sns.sendNotification(String.format("Error: Create bucket %s", request.getBucketName()), exception.getLocalizedMessage());
        } finally {
            AWSXRay.endSubsegment();
        }

        return null;
    }

    public BucketDTO getBucketKeyByKey(String name) {
        return bucketMapper.map(bucketRepository.findByKey(name), BucketDTO.class);
    }

    public BucketDTO getBucketFromS3(String bucketName){
        List<BucketDTO> buckets = findAllBuckets();
        for (BucketDTO bucket: buckets ) {
            if (bucket.getKey().equals(bucketName)){
                return bucket;
            }
        }
        return null;
    }

    public List<BucketDTO> findAllBuckets() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
        List<BucketDTO> bucketDTOS = new ArrayList<>();
        List<Bucket> buckets = s3Client.listBuckets();
        for (Bucket bucket : buckets ) {
            bucketDTOS.add(new BucketDTO().builder().key(bucket.getName()).clientRegion(Regions.EU_CENTRAL_1.getName()).build());
        }
        return bucketDTOS;
    }
}
