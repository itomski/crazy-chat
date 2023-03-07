package de.lubowiecki.crazychat;

import java.util.HashSet;
import java.util.Set;

public class CustomMessage {

    private String name;

    private String message;

    private Set<String> users = new HashSet<>();

    public CustomMessage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
