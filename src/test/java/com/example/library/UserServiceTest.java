package com.example.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService();
    }

    @Test
    void newServiceContainsSeededUsers() {
        List<User> users = service.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findByIdReturnsSeededUser() {
        User user = service.findById(1L);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
    }

    @Test
    void findByIdReturnsSecondSeededUser() {
        User user = service.findById(2L);

        assertNotNull(user);
        assertEquals(2L, user.getId());
        assertEquals("jane.doe@example.com", user.getEmail());
        assertEquals("Jane Doe", user.getName());
    }

    @Test
    void findByIdReturnsNullForUnknownId() {
        assertNull(service.findById(999L));
    }

    @Test
    void findByIdReturnsNullForNullId() {
        assertNull(service.findById(null));
    }

    @Test
    void findAllReturnsCopy() {
        List<User> users = service.findAll();
        users.clear();

        assertEquals(2, service.findAll().size());
    }

    @Test
    void createUserReturnsNewUser() {
        User user = service.createUser("new@example.com", "New User");

        assertNotNull(user);
        assertEquals("new@example.com", user.getEmail());
        assertEquals("New User", user.getName());
    }

    @Test
    void createUserAssignsId() {
        User user = service.createUser("new@example.com", "New User");

        assertNotNull(user.getId());
    }

    @Test
    void createUserIsRetrievableById() {
        User created = service.createUser("new@example.com", "New User");
        User found = service.findById(created.getId());

        assertEquals(created, found);
    }

    @Test
    void createUserIncreasesTotal() {
        service.createUser("new@example.com", "New User");

        assertEquals(3, service.findAll().size());
    }

    @Test
    void createMultipleUsersAssignsUniqueIds() {
        User user1 = service.createUser("a@example.com", "A");
        User user2 = service.createUser("b@example.com", "B");

        assertNotEquals(user1.getId(), user2.getId());
    }
}
