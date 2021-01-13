package nl.tudelft.sem.requests.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

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

    /**
     * Constructor for requests.
     *
     * @param id       request id.
     * @param user     the user which request will be send.
     * @param house    the house the user wants to join.
     * @param approved check if the request is approved.
     */
    public Request(RequestId id, House house, User user, boolean approved) {
        this.id = id;
        this.user = user;
        this.house = house;
        this.approved = approved;
    }

    // Requirement by Spring
    public Request() {
    }

    public RequestId getId() {
        return this.id;
    }

    public void setId(RequestId id) {
        this.id = id;
    }

    @JsonBackReference("r1")
    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @JsonBackReference("r2")
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
}
