package com.fedorniakm.assignment.service;

import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();

    List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to);

    Optional<User> getById(Long id);

    User create(User user);

    boolean patch(Long id, UserPatch userPatch);

    boolean deleteById(Long id);

    boolean replace(User user);
}
