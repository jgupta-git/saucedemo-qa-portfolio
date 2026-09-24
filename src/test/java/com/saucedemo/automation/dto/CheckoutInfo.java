package com.saucedemo.automation.dto;

/** Plain DTO carrying the inputs for checkout step one (Your Information). */
public class CheckoutInfo {

    private String firstName;
    private String lastName;
    private String zipCode;

    public CheckoutInfo() {
    }

    public CheckoutInfo(String firstName, String lastName, String zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
