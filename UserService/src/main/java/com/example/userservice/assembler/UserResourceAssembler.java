package com.example.userservice.assembler;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.model.UserResource;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler extends RepresentationModelAssemblerSupport<UserDTO, UserResource> {
    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toModel(UserDTO userDTO) {
        UserResource resource = new UserResource(userDTO);

        resource.add(linkTo(methodOn(UserController.class).getUserById(userDTO.id())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        resource.add(linkTo(methodOn(UserController.class).updateUser(userDTO.id(), null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(userDTO.id())).withRel("delete"));

        return resource;
    }
}