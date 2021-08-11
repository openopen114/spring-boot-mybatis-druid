package com.openopen.model;

import java.math.BigDecimal;
import java.util.Date;

public class Person {
    private String id;

    private String name;

    private Integer age;

    private String address;

    private BigDecimal weight;

    private Date dateNoTz;

    private Date dateWithTz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Date getDateNoTz() {
        return dateNoTz;
    }

    public void setDateNoTz(Date dateNoTz) {
        this.dateNoTz = dateNoTz;
    }

    public Date getDateWithTz() {
        return dateWithTz;
    }

    public void setDateWithTz(Date dateWithTz) {
        this.dateWithTz = dateWithTz;
    }
}