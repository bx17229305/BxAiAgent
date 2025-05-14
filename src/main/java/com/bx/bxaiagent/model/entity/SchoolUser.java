package com.bx.bxaiagent.model.entity;

import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
public class SchoolUser implements User{
    private String id;
    private String name;
    private String password;
}
