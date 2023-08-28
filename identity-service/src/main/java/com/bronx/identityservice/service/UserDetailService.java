package com.bronx.identityservice.service;

import com.bronx.identityservice.entity.UserCredential;
import com.bronx.identityservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredential> userCredential = userCredentialRepository.findByName(username);
        return userCredential.map(UserDetail::new).orElseThrow(()-> new UsernameNotFoundException("invalid username : "+username));
    }
}
