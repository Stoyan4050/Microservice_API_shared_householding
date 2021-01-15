package nl.tudelft.sem.requests.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Class representing the User entity in the database - User table.
 */
@Entity
@Table(name = "user")
public class User implements java.io.Serializable {

    static final long serialVersionUID = 42L;

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

    @Cascade(CascadeType.DELETE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Request> requests = new HashSet<Request>(0);

    public User(String username) {
        this.username = username;
    }

    /**
     * Constructor for creating user.
     *
     * @param username     username of the user
     * @param house        house in which is the user
     * @param totalCredits credits of the user
     * @param email        email of the user
     * @param requests     all of the requests of the use for joining house
     */
    public User(String username, House house,
                float totalCredits, String email, Set<Request> requests) {
        this.username = username;
        this.house = house;
        this.totalCredits = totalCredits;
        this.email = email;
        this.requests = requests;
    }

    // Requirement by Spring
    public User() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonBackReference("u1")
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

    @JsonIgnore
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
        return Objects.hash(username, totalCredits, email);
    }
}