package com.frpr.controlller;

import com.frpr.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private User user;

    public JwtResponse(String jwttoken, User user) {
        this.jwttoken = jwttoken;
        this.user = user;
    }

    public String getToken() {
        return this.jwttoken;
    }
}