package com.bronx.identityservice.service;

import com.bronx.identityservice.entity.UserCredential;

import com.bronx.identityservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthSerivce {

    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveUser(UserCredential userCredential){
        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        userCredentialRepository.save(userCredential);
        return "User added to the system";
    }
}
