package com.example.demo.dto.key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketDTO {

    private Long id;
    private String key;
    private String clientRegion;
    private List<ObjectDTO> objects;
}
