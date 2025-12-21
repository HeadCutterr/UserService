package com.example.userservice.controller;

import com.example.userservice.dto.UserCreateDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.assembler.UserResourceAssembler;
import com.example.userservice.model.UserResource;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final UserResourceAssembler assembler;

    @Autowired
    public UserController(UserService userService, UserResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users with links.")
    @GetMapping
    public ResponseEntity<CollectionModel<UserResource>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        List<UserResource> resources = users.stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<UserResource> collectionModel = CollectionModel.of(resources);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResource>> getUserById(
                                                                  @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        return userService.getUserById(id)
                .map(userDTO -> {
                    UserResource resource = assembler.toModel(userDTO);
                    return ResponseEntity.ok(EntityModel.of(resource));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details and returns the created user with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResource>> createUser(
                                                                 @Valid @RequestBody UserCreateDTO dto) {
        UserDTO createdUser = userService.createUser(dto);
        UserResource resource = assembler.toModel(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(resource));
    }

    @Operation(summary = "Update an existing user", description = "Updates an existing user identified by their ID and returns the updated user with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResource>> updateUser(
                                                                 @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
                                                                 @Valid @RequestBody UserCreateDTO dto) {
        UserDTO updatedUser = userService.updateUser(id, dto);
        UserResource resource = assembler.toModel(updatedUser);
        return ResponseEntity.ok(EntityModel.of(resource));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user identified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
                                            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}