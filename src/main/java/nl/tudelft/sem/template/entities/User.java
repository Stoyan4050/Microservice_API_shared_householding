package nl.tudelft.sem.template.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class representing the User entity in the database - User table.
 */
@Entity
@Table(name = "user", catalog = "projects_SEM-51")
public class User implements java.io.Serializable {

    static final long serialVersionUID = 42L;

    // Primary key in the database
    @Id
    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_nr")
    private House house;

    @Column(name = "total_credits", precision = 12, scale = 0)
    private float totalCredits;

    @Column(name = "email", length = 1000)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Request> requests = new HashSet<Request>(0);


    // Requirement by Spring
    public User() {
    }

    /**
     * Constructor for the User class.
     *
     * @param username - the username of the user
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Constructor for the User class.
     *
     * @param username     - username of a user
     * @param house        - house the user belongs to
     * @param totalCredits - total number of credits
     * @param email        - email of user
     * @param requests     - the requests of the user
     */
    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("house") House house,
                @JsonProperty("totalCredits") float totalCredits,
                @JsonProperty("email") String email,
                @JsonProperty("requests") Set<Request> requests) {
        this.username = username;
        this.house = house;
        this.totalCredits = totalCredits;
        this.email = email;
        this.requests = requests;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public float getTotalCredits() {
        return this.totalCredits;
    }

    public void setTotalCredits(float totalCredits) {
        this.totalCredits = totalCredits;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return Float.compare(user.totalCredits, totalCredits) == 0
                && Objects.equals(username, user.username)
                && Objects.equals(house, user.house)
                && Objects.equals(email, user.email)
                && Objects.equals(requests, user.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, house, totalCredits, email, requests);
    }
}