package nl.tudelft.sem.template.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int requestId;

    private String username;

    private int houseNumber;

    private boolean approved;


    /** Constructor for request entity.
     *
     * @param username name of the user who requests to join to a house
     * @param houseNumber number of the house that the user requests to join
     * @param approved status of the request
     */
    public Request(String username, int houseNumber, boolean approved) {
        this.username = username;
        this.houseNumber = houseNumber;
        this.approved = approved;
    }

    public Request() {
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
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
        if (!(o instanceof Request)) {
            return false;
        }
        Request request = (Request) o;
        return requestId == request.requestId
                && houseNumber == request.houseNumber
                && approved == request.approved
                && username.equals(request.username);
    }
}
