package com.robogames.RoboCupMS.Business.Security;

import java.util.Date;

public class RegistrationObj {

    private String name;

    private String surname;

    private String email;

    private String password;

    private Date birthDate;

    public RegistrationObj() {
        this.name = null;
        this.surname = null;
        this.email = null;
        this.password = null;
        this.birthDate = null;
    }

    public RegistrationObj(String name, String surname, String email, String password, Date birthDate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
