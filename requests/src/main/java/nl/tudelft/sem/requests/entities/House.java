package nl.tudelft.sem.requests.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Class representing the House entity in the database - House table.
 */
@Entity
@Table(name = "house")
public class House implements java.io.Serializable {

    static final long serialVersionUID = 42L;

    // Primary key in the database
    @Id
    @Column(name = "house_nr", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int houseNr;


    @Column(name = "name", length = 1000)
    private String name;

    @Cascade(CascadeType.DELETE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "house")
    private Set<Request> requests = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "house")
    private Set<User> users = new HashSet<>(0);

    public House(int houseNr, String name) {
        this.houseNr = houseNr;
        this.name = name;
    }

    /**
     * Constructor for a house.
     *
     * @param houseNr  the number of the house
     * @param name     name of the house
     * @param requests all of the requests for joining this house
     * @param users    all of the users living in this house
     */
    public House(int houseNr, String name, Set<Request> requests, Set<User> users) {
        this.houseNr = houseNr;
        this.name = name;
        this.requests = requests;
        this.users = users;
    }

    // Requirement by Spring
    public House() {
    }

    public int getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof House)) {
            return false;
        }
        House house = (House) o;
        return houseNr == house.houseNr
            && name.equals(house.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNr, name);
    }
}