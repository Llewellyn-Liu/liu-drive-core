package com.lrl.liudrivecore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/drive/user")
public class UserController {

    @RequestMapping(value = "/{userId}/profile", method = RequestMethod.GET)
    public void getUserProfile(HttpServletRequest request,
                                    HttpServletResponse response){


    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public void testGetUserProfile(HttpServletRequest request,
                                    HttpServletResponse response){

        System.out.println("Accessing /drive/profile");

    }


}
