package com.lbs.user.user.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-06
 * 풀이방법
 **/

@Getter
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(state, address.state) && Objects.equals(zip, address.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zip);
    }
}
