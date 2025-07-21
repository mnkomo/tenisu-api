package com.latelier.tenisu.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.latelier.tenisu.controller.handler.GlobalExceptionHandler;
import com.latelier.tenisu.exception.PlayerNotFoundException;
import com.latelier.tenisu.model.Player;
import com.latelier.tenisu.model.PlayerData;
import com.latelier.tenisu.service.PlayerService;

@ExtendWith(SpringExtension.class)
public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllPlayersSortedByRank_shouldReturnPlayers_whenPlayersExist() throws Exception {
        // Given
        Player player1 = buildPlayer(101, "Raphael", "NADAL", 1);
        Player player2 = buildPlayer(12, "Jannik", "SINNER", 2);
        List<Player> players = List.of(player1, player2);
        when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/sorted-by-rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(101)))
                .andExpect(jsonPath("$[1].id", Matchers.is(12)));
    }

    @Test
    void getAllPlayersSortedByRank_shouldThrowNoContentException_whenPlayersExist() throws Exception {
        // Given
        when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/players/sorted-by-rank"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message", Matchers.is("No players found")))
                .andExpect(jsonPath("$.statusCode", Matchers.is(204)));
    }

    @Test
    void getPlayerById_shouldReturnPlayer_whenPlayerExists() throws Exception {
        // Given
        long playerId = 10;
        Player player = buildPlayer(playerId, "Serena", "WILLIAMS", 3);
        when(playerService.getPlayerById(playerId)).thenReturn(player);

        // When & Then
        mockMvc.perform(get("/players/{id}", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(10)))
                .andExpect(jsonPath("$.firstname", Matchers.is("Serena")))
                .andExpect(jsonPath("$.lastname", Matchers.is("WILLIAMS")));
    }

    @Test
    void getPlayerById_shouldThrowPlayerNotFoundException_whenPlayerDoesNotExist() throws Exception {
        // Given
        long playerId = 99;
        when(playerService.getPlayerById(playerId)).thenThrow(
                new PlayerNotFoundException("Player not found with id: " + playerId));

        // When & Then
        mockMvc.perform(get("/players/{id}", playerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Player not found with id: " + playerId)))
                .andExpect(jsonPath("$.statusCode", Matchers.is(404)));
    }

    private Player buildPlayer(long id, String firstname, String lastname, int rank) {
        Player player = new Player();
        player.setId(id);
        player.setFirstname(firstname);
        player.setLastname(lastname);

        PlayerData data = new PlayerData();
        data.setRank(rank);

        return player;
    }
}
