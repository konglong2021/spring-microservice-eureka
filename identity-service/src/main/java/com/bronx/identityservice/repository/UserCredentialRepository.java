package com.bronx.identityservice.repository;

import com.bronx.identityservice.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential,Long> {
    Optional<UserCredential> findByName(String name);
    UserCredential findByEmail(String email);
}
