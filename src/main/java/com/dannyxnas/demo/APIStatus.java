package com.dannyxnas.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class APIStatus {

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        if(name == null) name = "World";
        return "Hello, " + name;
    }
}
