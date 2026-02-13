package com.example.library;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorSetsFields() {
        User user = new User(1L, "test@example.com", "Test User");

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
    }

    @Test
    void constructorRejectsNullId() {
        assertThrows(NullPointerException.class,
                () -> new User(null, "test@example.com", "Test User"));
    }

    @Test
    void constructorRejectsNullEmail() {
        assertThrows(NullPointerException.class,
                () -> new User(1L, null, "Test User"));
    }

    @Test
    void constructorRejectsNullName() {
        assertThrows(NullPointerException.class,
                () -> new User(1L, "test@example.com", null));
    }

    @Test
    void equalsByIdOnly() {
        User user1 = new User(1L, "a@example.com", "Alice");
        User user2 = new User(1L, "b@example.com", "Bob");

        assertEquals(user1, user2);
    }

    @Test
    void notEqualWithDifferentId() {
        User user1 = new User(1L, "a@example.com", "Alice");
        User user2 = new User(2L, "a@example.com", "Alice");

        assertNotEquals(user1, user2);
    }

    @Test
    void notEqualToNull() {
        User user = new User(1L, "a@example.com", "Alice");

        assertNotEquals(null, user);
    }

    @Test
    void notEqualToDifferentType() {
        User user = new User(1L, "a@example.com", "Alice");

        assertNotEquals("not a user", user);
    }

    @Test
    void equalToSelf() {
        User user = new User(1L, "a@example.com", "Alice");

        assertEquals(user, user);
    }

    @Test
    void hashCodeConsistentWithEquals() {
        User user1 = new User(1L, "a@example.com", "Alice");
        User user2 = new User(1L, "b@example.com", "Bob");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toStringContainsAllFields() {
        User user = new User(1L, "test@example.com", "Test User");
        String str = user.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("test@example.com"));
        assertTrue(str.contains("Test User"));
    }
}
