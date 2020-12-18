package nl.tudelft.sem.auth.entities;

public class UserRequest {

    private String username;
    private String email;

    public UserRequest() {
    }

    /**
     * Constructor for creating user to send to the requests microservice.
     *
     * @param username username of the user
     * @param email    email of the user
     */
    public UserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
