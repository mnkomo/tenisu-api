package com.latelier.tenisu.dto;

import com.latelier.tenisu.model.Country;
import com.latelier.tenisu.model.PlayerData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerDto {
    private String firstname;
    private String lastname;
    private String shortname;
    private String sex;
    private Country country;
    private String picture;
    private PlayerData data;
}
