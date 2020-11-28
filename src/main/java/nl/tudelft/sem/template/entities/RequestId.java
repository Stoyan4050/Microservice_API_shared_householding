package nl.tudelft.sem.template.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;


@Embeddable
public class RequestId implements java.io.Serializable {

    @Column(name = "house_nr", nullable = false)
    private int houseNr;

    @Column(name = "username", nullable = false, length = 25)
    private String username;


    // Requirement by Spring
    public RequestId() {
    }

    /**
     * Constructor for RequestId class
     *
     * @param houseNr   - house number
     * @param username  - username
     */
    @JsonCreator
    public RequestId(@JsonProperty("houseNr") int houseNr,
                     @JsonProperty("username") String username) {
        this.houseNr = houseNr;
        this.username = username;
    }

    public int getHouseNr() {
        return this.houseNr;
    }

    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestId requestId = (RequestId) o;
        return houseNr == requestId.houseNr &&
                Objects.equals(username, requestId.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNr, username);
    }
}
