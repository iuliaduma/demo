package com.example.demo.dto.document;

import com.example.demo.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Long id;
    private String name;
    private String type;
    private UserDTO created;
    private Date updated;
    private String creator;
    private String description;
    private Long version;
}