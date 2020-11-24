package nl.tudelft.sem.template.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String username;

    private int totalCredits;

    private String email;

    private int houseNumber;


    /** Constructor for user entity.
     *
     * @param totalCredits total credits of the user
     * @param email email of the user
     * @param houseNumber number of the house that the user belongs to
     */
    public User(int totalCredits, String email, int houseNumber) {
        this.totalCredits = totalCredits;
        this.email = email;
        this.houseNumber = houseNumber;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return totalCredits == user.totalCredits
                && houseNumber == user.houseNumber
                && username.equals(user.username)
                && email.equals(user.email);
    }
}
