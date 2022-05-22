package com.kawahedukasi.user.controllers;

import com.kawahedukasi.user.services.UserService;
import com.kawahedukasi.user.swagger.requests.UserGetRequest;
import com.kawahedukasi.user.swagger.requests.UserLoginRequest;
import com.kawahedukasi.user.swagger.requests.UserSignUpRequest;
import com.kawahedukasi.user.swagger.requests.UserUpdateRequest;
import com.kawahedukasi.user.swagger.responses.UserGetResponse;
import com.kawahedukasi.user.swagger.responses.UserLoginResponse;
import com.kawahedukasi.user.utils.SimpleResponse;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBodySchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirementsSet;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Management")
public class UserController {
    @Inject
    UserService userService;

    @POST
    @Path("/login")
    @RequestBodySchema(value = UserLoginRequest.class)
    @APIResponseSchema(value = UserLoginResponse.class, responseCode = "200", responseDescription = "\"status\" : 200, \"message\": \"SUCCESS\"")
    public SimpleResponse login(Object param) {
        return userService.login(param);
    }


    @POST
    @Path("/logout")
    @APIResponseSchema(value = String.class, responseCode = "200", responseDescription = "\"status\" : 200, \"message\": \"SUCCESS\"")
    public SimpleResponse logout(@HeaderParam("X-Consumer-Custom-ID") String header){
        return userService.logout(header);
    }

    @POST
    @Path("/signup")
    @RequestBodySchema(value = UserSignUpRequest.class)
    @APIResponseSchema(value = String.class, responseCode = "200", responseDescription = "\"status\" : 200, \"message\": \"SUCCESS\"")
    public SimpleResponse signUp(Object param) {
        return userService.signup(param);
    }

    @POST
    @Path("/updateUser")
    @RequestBodySchema(value = UserUpdateRequest.class)
    @APIResponseSchema(value = String.class, responseCode = "200", responseDescription = "\"status\" : 200, \"message\": \"SUCCESS\"")
    public SimpleResponse updateUser(Object param, @HeaderParam("X-Consumer-Custom-ID") String header){
        return userService.updateUser(param);
    }

    @POST
    @Path("/getUser")
    @RequestBodySchema(value = UserGetRequest.class)
    @APIResponseSchema(value = UserGetResponse.class, responseCode = "200", responseDescription = "\"status\" : 200, \"message\": \"SUCCESS\"")
    public SimpleResponse getUser(Object param){
        return userService.getUser(param);
    }

    @GET
    @Path("/{login_name}/verifyUser")
    public SimpleResponse verifyUser(@PathParam("login_name")String login_name){
        return userService.verifyUser(login_name);
    }

    @GET
    @Path("/{login_name}/acceptVerification")
    public SimpleResponse acceptVerification(@PathParam("login_name")String login_name, @QueryParam("otpCode") String otpCode){
        return userService.acceptVerification(login_name, otpCode);
    }
}