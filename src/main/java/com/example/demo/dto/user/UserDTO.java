package com.example.demo.dto.user;

import com.example.demo.data.user.Title;
import com.example.demo.dto.document.DocumentDTO;
import com.example.demo.dto.message.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private Title title;
    private RoleDTO role;
    private String firstName;
    private String lastName;
    private AddressDTO address;
    private List<DocumentDTO> documentList;
    private List<MessageDTO> messageList;
}