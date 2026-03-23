package com.example.documentService.controller;

import com.example.documentService.service.UserClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/documents")
public class UserProxyController {

    private final UserClient userClient;

    public UserProxyController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/users")
    public Object getUsersFromUserService(
            @RequestHeader("Authorization") String authHeader) {

        return userClient.getUsers(authHeader);
    }
}