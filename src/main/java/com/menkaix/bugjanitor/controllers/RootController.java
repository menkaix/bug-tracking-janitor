package com.menkaix.bugjanitor.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return "Hello, World!";
    }
}
