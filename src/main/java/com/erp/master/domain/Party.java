package com.erp.master.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "parties")
public class Party {

    public enum PartyType {
        CUSTOMER, SUPPLIER, BOTH
    }

    @Id
    private String id;
    private String name;

    @Enumerated(EnumType.STRING)
    private PartyType type;

    private String gstin;
    private String address;
    private String phone;
    private String email;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PartyType getType() { return type; }
    public void setType(PartyType type) { this.type = type; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
