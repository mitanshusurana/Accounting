package com.erp.inventory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "godowns")
public class Godown {

    @Id
    private String godownId;
    private String name;
    private String location;

    public Godown() {}

    public String getGodownId() { return godownId; }
    public void setGodownId(String godownId) { this.godownId = godownId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
