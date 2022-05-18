package com.kawahedukasi.user.controllers;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.kawahedukasi.user.services.UserService;
import com.kawahedukasi.user.services.VerifyEmailService;
import com.kawahedukasi.user.utils.SimpleResponse;

@Path("/api/v1/user/{login_name}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Management")
public class VerifyEmailController {
    @Inject
    VerifyEmailService verifyEmailService;
    
    @GET
    @Path("/verifyMe")
    public SimpleResponse sendVerification(@PathParam("login_name")String login_name){
        return verifyEmailService.sendVerification(login_name);
    }

    @GET
    @Path("/acceptVerification")
    public SimpleResponse acceptVerification(@PathParam("login_name")String login_name, @QueryParam("otpCode") String otpCode){
        return verifyEmailService.acceptVerification(login_name, otpCode);
    }
}
