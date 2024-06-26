package com.fedorniakm.demo.service;

import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.model.UserPatch;
import com.fedorniakm.demo.service.patcher.UserPatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SimpleUserService implements UserService {

    private static class InMemoryMapUserRepository {

        private final UserPatcher userPatcher;
        private final AtomicLong atomicLong = new AtomicLong(1L);
        private final Map<Long, User> users;

        public InMemoryMapUserRepository(UserPatcher userPatcher) {
            this.userPatcher = userPatcher;
            this.users = new HashMap<>();
        }

        public List<User> getAll() {
            return new ArrayList<>(users.values());
        }

        public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
            var userStream = users.values().stream();
            if (from.isPresent()) {
                userStream = userStream.filter(user -> user.getBirthDate().isAfter(from.get()));
            }
            if (to.isPresent()) {
                userStream = userStream.filter(user -> user.getBirthDate().isBefore(to.get()));
            }
            return userStream.toList();
        }

        public Optional<User> getById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        public User create(User user) {
            Objects.requireNonNull(user);
            var id = atomicLong.getAndIncrement();
            user.setId(id);
            users.put(id, user);
            return user;
        }

        public boolean deleteById(Long id) {
            return users.remove(id) != null;
        }

        public boolean replace(User user) {
            return users.replace(user.getId(), user) != null;
        }

        public boolean patch(Long id, UserPatch userPatch) {
            var user = getById(id);
            if (user.isPresent()) {
                userPatcher.patch(user.get(), userPatch);
                return true;
            }
            return false;
        }
    }

    private final InMemoryMapUserRepository userRepository;

    public SimpleUserService(UserPatcher userPatcher) {
        this.userRepository = new InMemoryMapUserRepository(userPatcher);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        return userRepository.getAll(from, to);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public boolean patch(Long id, UserPatch userPatch) {
        return userRepository.patch(id, userPatch);
    }

    @Override
    public boolean deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public boolean replace(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        return userRepository.replace(user);
    }

}
