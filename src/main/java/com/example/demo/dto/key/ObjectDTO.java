package com.example.demo.dto.key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDTO {

    private Long id;
    private String key;
    private BucketDTO bucket;

}