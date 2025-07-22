package com.latelier.tenisu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "players")
public class Player {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String shortname;
    private String sex;
    private Country country;
    private String picture;
    private PlayerData data;
}