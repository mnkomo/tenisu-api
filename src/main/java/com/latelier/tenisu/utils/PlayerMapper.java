package com.latelier.tenisu.utils;

import org.springframework.stereotype.Component;

import com.latelier.tenisu.dto.CreatePlayerDto;
import com.latelier.tenisu.model.Player;

@Component
public class PlayerMapper {
    /**
     * Maps a CreatePlayerDto to a Player entity.
     *
     * @param dto the CreatePlayerDto to map
     * @return a Player entity with the data from the dto
     */
    public Player toPlayer(CreatePlayerDto dto) {
        if (dto == null) {
            return null;
        }
        Player player = new Player();
        player.setFirstname(dto.getFirstname());
        player.setLastname(dto.getLastname());
        player.setShortname(dto.getShortname());
        player.setSex(dto.getSex());
        player.setCountry(dto.getCountry());
        player.setPicture(dto.getPicture());
        player.setData(dto.getData());

        return player;
    }
}
