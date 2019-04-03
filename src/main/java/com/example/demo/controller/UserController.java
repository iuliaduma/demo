package com.example.demo.controller;

import com.example.demo.dto.user.UserDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public List<UserDTO> findAllUsers(){

        return userService.findAll();
    }

    @PostMapping("/create")
    public UserDTO createUser(@RequestBody UserDTO userDTO){

        return userService.createUser(userDTO);
    }
}
