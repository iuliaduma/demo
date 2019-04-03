package com.example.demo.repository;

import com.example.demo.data.key.BucketKey;
import com.example.demo.data.key.ObjectKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObjectKeyRepository extends CrudRepository<ObjectKey, Long> {

    List<ObjectKey> findAllByBucket(BucketKey bucket);

    ObjectKey findByKeyAndBucket(String key, BucketKey bucketKey);
}
