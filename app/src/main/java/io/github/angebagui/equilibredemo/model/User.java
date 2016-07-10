package io.github.angebagui.equilibredemo.model;


import com.j256.ormlite.field.DatabaseField;

import io.github.angebagui.equilibre.EquilibreClassName;
import io.github.angebagui.equilibre.EquilibreObject;
import io.github.angebagui.equilibre.EquilibreQuery;

/**
 * Created by angebagui on 11/06/2016.
 */
@EquilibreClassName("User")
public class User extends EquilibreObject<String> {

    @DatabaseField
    private String name;

    @DatabaseField(id = true)
    private String email;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static EquilibreQuery<User, String> getQuery(){
        return new EquilibreQuery(User.class);
    }

    @Override
    public String getEquilibreId() {
        return email;
    }


}
