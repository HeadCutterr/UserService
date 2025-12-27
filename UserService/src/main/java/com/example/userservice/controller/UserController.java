package com.example.userservice.controller;

import com.example.userservice.dto.UserCreateDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.assembler.UserResourceAssembler; // Импортируем assembler
import com.example.userservice.model.UserResource; // Импортируем resource
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
import org.springframework.hateoas.CollectionModel; // Импортируем CollectionModel
import org.springframework.hateoas.EntityModel; // Импортируем EntityModel
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final UserResourceAssembler assembler; // Инжектируем assembler

    @Autowired
    public UserController(UserService userService, UserResourceAssembler assembler) { // Добавляем assembler в конструктор
        this.userService = userService;
        this.assembler = assembler;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users with links.")
    @GetMapping
    public ResponseEntity<CollectionModel<UserResource>> getAllUsers() { // Меняем возвращаемый тип
        List<UserDTO> users = userService.getAllUsers();
        List<UserResource> resources = users.stream()
                .map(assembler::toModel) // Используем assembler для преобразования
                .toList();
        CollectionModel<UserResource> collectionModel = CollectionModel.of(resources);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel()); // Добавляем ссылку на себя
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResource.class))), // Меняем тип возвращаемого значения в схеме
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResource>> getUserById( // Меняем возвращаемый тип
                                                                  @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        return userService.getUserById(id)
                .map(userDTO -> {
                    UserResource resource = assembler.toModel(userDTO); // Используем assembler
                    return ResponseEntity.ok(EntityModel.of(resource));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details and returns the created user with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserResource.class))), // Меняем тип возвращаемого значения в схеме
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResource>> createUser( // Меняем возвращаемый тип
                                                                 @Valid @RequestBody UserCreateDTO dto) {
        UserDTO createdUser = userService.createUser(dto);
        UserResource resource = assembler.toModel(createdUser); // Используем assembler
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(resource));
    }

    @Operation(summary = "Update an existing user", description = "Updates an existing user identified by their ID and returns the updated user with links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserResource.class))), // Меняем тип возвращаемого значения в схеме
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResource>> updateUser( // Меняем возвращаемый тип
                                                                 @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
                                                                 @Valid @RequestBody UserCreateDTO dto) {
        UserDTO updatedUser = userService.updateUser(id, dto);
        UserResource resource = assembler.toModel(updatedUser); // Используем assembler
        return ResponseEntity.ok(EntityModel.of(resource));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user identified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser( // Удалять ресурс - возвращаем просто статус
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