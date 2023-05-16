package com.miit_api.processor_api.controller;


import com.miit_api.processor_api.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@RestController
@RequestMapping("/api/")
@Slf4j
public class LoginController {

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    public LoginController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${registration.service.base.url}")
    private String registrationServiceBaseUrl;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDTO loginDTO) {
        // Make a GET request to the Registration Microservice to retrieve the account with the given email address
        String url = registrationServiceBaseUrl + "/getByEmail/" + loginDTO.getEmailAddress();

        try {
            ResponseEntity<LoginDTO> response = restTemplate.getForEntity(url, LoginDTO.class);

            // If the account exists and the password matches, return a success response
            if (response.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(response.getBody()).getPassword().equals(loginDTO.getPassword()))
                return new ResponseEntity<>("User authenticated successfully", HttpStatus.OK);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return new ResponseEntity<>("Account not found or Check Credentials Again!", HttpStatus.NOT_FOUND);
            else return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Otherwise, return an error response
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

}