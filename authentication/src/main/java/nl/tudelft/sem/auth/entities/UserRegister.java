package nl.tudelft.sem.auth.entities;

public class UserRegister {
    private String username;
    private String password;
    private String email;

    /**Constructor for creating user when registering for the first time.
     *
     * @param username username of the user
     * @param password password of the user
     * @param email email of the user
     */
    public UserRegister(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserRegister() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
