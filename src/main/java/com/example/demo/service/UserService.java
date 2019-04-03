package com.example.demo.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.example.demo.data.user.Address;
import com.example.demo.data.user.Role;
import com.example.demo.data.user.User;
import com.example.demo.dto.key.BucketDTO;
import com.example.demo.dto.user.AddressDTO;
import com.example.demo.dto.user.RoleDTO;
import com.example.demo.dto.user.UserDTO;
import com.example.demo.mapping.GenericModelMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final AddressService addressService;

    private final GenericModelMapper userMapper;

    private final GenericModelMapper roleMapper;

    private final GenericModelMapper addressMapper;

    private final BucketService bucketService;

    private final ObjectService objectService;

    public UserDTO createUser(UserDTO userDTO){

        User user = userMapper.map(userDTO, User.class);
        RoleDTO role = roleService.saveOrUpdate(userDTO.getRole());
        AddressDTO address = addressService.saveOrUpdate(userDTO.getAddress());
        user.setAddress(addressMapper.map(address, Address.class));
        user.setRole(roleMapper.map(role, Role.class));

        user = userRepository.save(user);

        Request request = Request.builder()
                .bucketName(String.format("%s-%s", user.getFirstName().toLowerCase(), user.getLastName().toLowerCase()))
                .clientRegion(Regions.EU_CENTRAL_1.getName())
                .build();

        Subsegment subsegment = AWSXRay.beginSubsegment("Save User");
        try {
            BucketDTO bucket = bucketService.createBucket(request);

            if (bucket != null) {

                request.setObjKeyName(String.format("%s-%s", user.getFirstName(), user.getLastName()));
                request.setDetails(String.format("%s %s %s %s %s", user.getTitle(), user.getFirstName(), user.getFirstName(), user.getRole(), user.getAddress()));

                objectService.uploadDetailsObject(request);
            }
        } catch (SdkClientException exception){
            subsegment.addException(exception);
//            Sns.sendNotification(String.format("Create user %s %s", user.getFirstName(), user.getLastName()), exception.getLocalizedMessage());
        } finally {
            AWSXRay.endSubsegment();
        }
        return userMapper.map(user, UserDTO.class);
    }

    public UserDTO findById(String id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setDocumentList(null);
            user.get().setMessageList(null);
        }

        return userMapper.map(user.get(), UserDTO.class);
    }

    public List<UserDTO> findAll(){
        List<User> users = (List<User>) userRepository.findAll();
        for (User user : users) {
            user.setMessageList(null);
            user.setDocumentList(null);
        }

        return userMapper.map(users);
    }
}
