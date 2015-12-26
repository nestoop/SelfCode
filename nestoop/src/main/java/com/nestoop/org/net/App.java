package com.nestoop.org.net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@RestController
@EnableAutoConfiguration
public class App 
{
	@RequestMapping(value ="/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello(){
		System.out.println("哎呀，请求过来了...................");
        return "hello world";
    }
 
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    } 
}
