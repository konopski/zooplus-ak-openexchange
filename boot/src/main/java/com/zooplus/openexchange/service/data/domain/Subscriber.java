package com.zooplus.openexchange.service.data.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OPENEXCHANGE.SUBSCRIBERS")
public class Subscriber implements Serializable {
    private static final long serialVersionUID = -6125496563594902503L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "EMAIL", unique = true)
    String email;

    @Column(name = "PASSWORD")
    String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}