package com.fedorniakm.demo.controller;

import com.fedorniakm.demo.model.Data;
import com.fedorniakm.demo.model.DateRange;
import com.fedorniakm.demo.model.UserPatch;
import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "/v1/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Data<List<User>>> getAllUsers(@Valid DateRange dateRange) {
        var data = Data.of(userService.getAll(
                Optional.ofNullable(dateRange.getFrom()),
                Optional.ofNullable(dateRange.getTo())
        ));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Data<User>> getUserById(@PathVariable Long id) {
        var user = userService.getById(id).orElse(null);
        var data = Data.of(user);
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody Data<User> userData) {
        Objects.requireNonNull(userData,
                "Incoming user fields must be wrapped the \"data\" field.");
        Objects.requireNonNull(userData.data(),
                "Incoming user fields must be wrapped the \"data\" field.");
        var createdUser = userService.create(userData.data());
        var resourceUri = URI.create("/v1/users/" + createdUser.getId());
        return ResponseEntity.created(resourceUri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> replaceUser(@PathVariable Long id,
                                              @Valid @RequestBody Data<User> user) {
        user.data().setId(id);
        var isReplaced = userService.replace(user.data());
        return isReplaced ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @Valid @RequestBody Data<UserPatch> userPatch) {
        return userService.patch(id, userPatch.data()) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        var isDeleted = userService.deleteById(id);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

}
