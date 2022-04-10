package com.kawahedukasi.user.controllers;

import com.kawahedukasi.user.services.UserService;
import com.kawahedukasi.user.utils.SimpleResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    UserService userService;

    @POST
    @Path("/login")
    public SimpleResponse login(Object param) {
        return userService.login(param);
    }

    @POST
    @Path("/signup")
    public SimpleResponse signUp(Object param) {
        return userService.signup(param);
    }

    @POST
    @Path("/updateUser")
    public SimpleResponse updateUser(Object param){
        return userService.updateUser(param);
    }

    @POST
    @Path("/getUser")
    public SimpleResponse getUser(Object param){
        return userService.getUser(param);
    }
}