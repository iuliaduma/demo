package com.example.demo.repository;

import com.example.demo.data.key.BucketKey;
import org.springframework.data.repository.CrudRepository;

public interface BucketKeyRepository  extends CrudRepository<BucketKey, Long> {

    BucketKey findByKey(String key);

}