package nl.tudelft.sem.template.requests.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Class representing the Request entity in the database - Request table.
 */
@Entity
@Table(name = "request")
public class Request implements java.io.Serializable {

    static final long serialVersionUID = 42L;

    @EmbeddedId
    @AttributeOverrides({@AttributeOverride(name = "houseNr",
                    column = @Column(name = "house_nr", nullable = false)),
            @AttributeOverride(name = "username",
                    column = @Column(name = "username", nullable = false, length = 25))
    })
    private RequestId id;
        

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_nr", nullable = false, insertable = false, updatable = false)
    private House house;

    @Column(name = "approved")
    private boolean approved;


    // Requirement by Spring
    public Request() {
    }

    /**
     * Constructor for the Request entity.
     *
     * @param id    - the request id
     * @param house - the house object associated with the request
     * @param user  - the user object associated with the request
     */
    public Request(RequestId id, House house, User user) {
        this.id = id;
        this.house = house;
        this.user = user;
    }

    /**
     * Constructor for the Request entity.
     *
     * @param id       - the request id
     * @param house    - the house object associated with the request
     * @param user     - the user object associated with the request
     * @param approved - the state of the request
     */
    @JsonCreator
    public Request(@JsonProperty("id") RequestId id,
                   @JsonProperty("house") House house,
                   @JsonProperty("user") User user,
                   @JsonProperty("approved") boolean approved) {
        this.id = id;
        this.house = house;
        this.user = user;
        this.approved = approved;
    }

    public RequestId getId() {
        return this.id;
    }

    public void setId(RequestId id) {
        this.id = id;
    }

    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request request = (Request) o;

        return approved == request.approved
                && Objects.equals(id, request.id)
                && Objects.equals(user, request.user)
                && Objects.equals(house, request.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, house, approved);
    }
}
