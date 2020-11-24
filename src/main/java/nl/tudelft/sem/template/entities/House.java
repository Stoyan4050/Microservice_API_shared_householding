package nl.tudelft.sem.template.entities;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int houseNumber;

    private String name;


    /** Constructor for House entity.
     *
     * @param name name of the house
     */
    public House(String name) {
        this.name = name;
    }

    public House() {
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return houseNumber == house.houseNumber
                && name.equals(house.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNumber, name);
    }
}
