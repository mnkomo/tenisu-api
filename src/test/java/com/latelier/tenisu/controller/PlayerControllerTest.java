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
        Player player1 = buildPlayer(101, "Raphael", "NADAL", 1);
        Player player2 = buildPlayer(12, "Jannik", "SINNER", 2);

        List<Player> players = List.of(player1, player2);
        when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(players);

        mockMvc.perform(get("/players/sorted-by-rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(101)))
                .andExpect(jsonPath("$[1].id", Matchers.is(12)));
    }

    @Test
    void getAllPlayersSortedByRank_shouldThrowNoContentException_whenPlayersExist() throws Exception {
        when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/players/sorted-by-rank"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message", Matchers.is("No players found")))
                .andExpect(jsonPath("$.statusCode", Matchers.is(204)));
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
