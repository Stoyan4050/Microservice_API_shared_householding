package nl.tudelft.sem.template.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

/**
 * Class representing the House entity in the database - House table.
 */
@Entity
@Table(name = "house", catalog = "projects_SEM-51")
public class House implements java.io.Serializable {

    static final long serialVersionUID = 42L;

    // Primary key in the database
    @Id
    @Column(name = "house_nr", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int houseNr;

    @Column(name = "name", length = 25)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "house")
    private Set<Request> requests = new HashSet<Request>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "house")
    private Set<User> users = new HashSet<User>(0);

    // Requirement by Spring
    public House() {
    }

    /**
     * Constructor for the House entity.
     *
     * @param houseNr - house number
     * @param name    - name of the house
     */
    public House(int houseNr, String name) {
        this.houseNr = houseNr;
        this.name = name;
    }

    /**
     * Constructor for the House entity.
     *
     * @param houseNr  - house number
     * @param name     - name of the house
     * @param requests - requests of the house
     * @param users    - users of the house
     */
    @JsonCreator
    public House(@JsonProperty("houseNr") int houseNr,
                 @JsonProperty("name") String name,
                 @JsonProperty("requests") Set<Request> requests,
                 @JsonProperty("users") Set<User> users) {
        this.houseNr = houseNr;
        this.name = name;
        this.requests = requests;
        this.users = users;
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