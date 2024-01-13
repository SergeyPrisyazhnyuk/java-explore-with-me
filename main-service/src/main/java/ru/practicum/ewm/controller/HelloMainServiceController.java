package ru.practicum.ewm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloMainServiceController {
    @GetMapping
    public String sayHello() {
        return
                "<!DOCTYPE html>" +
                        "<html>" +
                        "	<head><title>This is a main service</title></head>" +
                        "	<body>Hi! This is a main service</body>" +
                        "</html>"
                ;
    }

}
