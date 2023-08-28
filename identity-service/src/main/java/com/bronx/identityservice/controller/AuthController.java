package com.bronx.identityservice.controller;

import com.bronx.identityservice.dto.AuthRequestDto;
import com.bronx.identityservice.entity.UserCredential;
import com.bronx.identityservice.repository.UserCredentialRepository;
import com.bronx.identityservice.service.AuthSerivce;
import com.bronx.identityservice.service.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthSerivce authSerivce;
    private JwtService jwtService;
    private UserCredentialRepository userCredentialRepository;

    public AuthController(AuthSerivce authSerivce, JwtService jwtService, UserCredentialRepository userCredentialRepository) {
        this.authSerivce = authSerivce;
        this.jwtService = jwtService;
        this.userCredentialRepository = userCredentialRepository;
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential userCredential){
        Optional<UserCredential> existedUser = userCredentialRepository.findByName(userCredential.getName());
        if (existedUser.isPresent()){
            return "User name has been register !";
        }
        authSerivce.saveUser(userCredential);
        return "Successfully added";
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequestDto authRequestDto){
        return jwtService.generateToken(authRequestDto);
    }

    @GetMapping("/validate")
    public String ValidateToken(@RequestParam("token") String token ){
        jwtService.validateToken(token);
        return "Token is valid";
    }
}
