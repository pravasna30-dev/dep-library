package com.example.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that exercise end-to-end workflows across User and UserService.
 */
class UserServiceIntegrationTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService();
    }

    @Test
    void fullUserLifecycle_createAndRetrieve() {
        User created = service.createUser("alice@example.com", "Alice Smith");

        User fetched = service.findById(created.getId());

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals("alice@example.com", fetched.getEmail());
        assertEquals("Alice Smith", fetched.getName());
    }

    @Test
    void createdUserAppearsInFindAll() {
        int initialSize = service.findAll().size();

        service.createUser("alice@example.com", "Alice Smith");
        service.createUser("bob@example.com", "Bob Jones");

        List<User> allUsers = service.findAll();
        assertEquals(initialSize + 2, allUsers.size());

        List<String> emails = allUsers.stream().map(User::getEmail).toList();
        assertTrue(emails.contains("alice@example.com"));
        assertTrue(emails.contains("bob@example.com"));
    }

    @Test
    void seededAndCreatedUsersCoexist() {
        User seeded = service.findById(1L);
        User created = service.createUser("new@example.com", "New User");

        assertNotNull(seeded);
        assertNotNull(service.findById(created.getId()));
        assertNotEquals(seeded.getId(), created.getId());

        List<User> all = service.findAll();
        assertTrue(all.contains(seeded));
        assertTrue(all.contains(created));
    }

    @Test
    void multipleServicesAreIndependent() {
        UserService service2 = new UserService();

        service.createUser("only-in-first@example.com", "First Only");

        assertEquals(3, service.findAll().size());
        assertEquals(2, service2.findAll().size());
    }

    @Test
    void createdUsersHaveCorrectEqualityBehavior() {
        User created = service.createUser("test@example.com", "Test User");
        User fetched = service.findById(created.getId());

        assertEquals(created, fetched);
        assertEquals(created.hashCode(), fetched.hashCode());
    }

    @Test
    void bulkCreateAndRetrieveAll() {
        for (int i = 0; i < 50; i++) {
            service.createUser("user" + i + "@example.com", "User " + i);
        }

        List<User> all = service.findAll();
        assertEquals(52, all.size()); // 2 seeded + 50 created

        // verify each created user is individually retrievable
        for (User user : all) {
            assertNotNull(service.findById(user.getId()));
        }
    }

    @Test
    void findAllSnapshotNotAffectedBySubsequentCreates() {
        List<User> snapshot = service.findAll();
        int snapshotSize = snapshot.size();

        service.createUser("late@example.com", "Late User");

        assertEquals(snapshotSize, snapshot.size());
    }

    @Test
    void createdUserToStringContainsAllData() {
        User created = service.createUser("display@example.com", "Display User");
        User fetched = service.findById(created.getId());

        String str = fetched.toString();
        assertTrue(str.contains(fetched.getId().toString()));
        assertTrue(str.contains("display@example.com"));
        assertTrue(str.contains("Display User"));
    }
}
