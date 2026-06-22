package com.dream.sanbong;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class controller{
@GetMapping("/")
public String hello() {
    return "Xin chào buổi sáng";
}


}

