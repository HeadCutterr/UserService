package com.example.userservice.model;

import com.example.userservice.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
@Relation(itemRelation = "user", collectionRelation = "users")
public class UserResource extends RepresentationModel<UserResource> {
    private  UserDTO userDTO;

    public UserResource(){
        super();
    }

    public UserResource(UserDTO userDTO){
        super();
        this.userDTO = userDTO;
    }

    @JsonProperty
    public UserDTO getUserDTO(){
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO){
        this.userDTO = userDTO;
    }
}
