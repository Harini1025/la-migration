package com.broadcom.laMigration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LAMigrationController {

    @GetMapping("/healthcheck")
    public ResponseEntity<String> createUserProfile() {
        return new ResponseEntity<>( HttpStatus.OK );
    }
}
