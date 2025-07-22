package com.latelier.tenisu.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.latelier.tenisu.controller.handler.GlobalExceptionHandler;
import com.latelier.tenisu.dto.CreatePlayerDto;
import com.latelier.tenisu.dto.StatisticsDto;
import com.latelier.tenisu.exception.PlayerNotFoundException;
import com.latelier.tenisu.model.Country;
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
                Player player1 = buildPlayer("player101", "Raphael", "NADAL", 1);
                Player player2 = buildPlayer("player12", "Jannik", "SINNER", 2);
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
                String playerId = "player10";
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
                String playerId = "player99";
                when(playerService.getPlayerById(playerId)).thenThrow(
                                new PlayerNotFoundException("Player not found with id: " + playerId));

                // When & Then
                mockMvc.perform(get("/players/{id}", playerId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", Matchers.is("Player not found with id: " + playerId)))
                                .andExpect(jsonPath("$.statusCode", Matchers.is(404)));
        }

        @Test
        void getPlayerStatistics_shouldReturnStatistics_whenPlayersExist() throws Exception {
                // Given
                Player player1 = buildPlayer("player101", "Raphael", "NADAL", 1);
                player1.setCountry(new Country("picture-spain", "ES"));
                player1.getData().setLast(new int[] { 1, 1, 1, 0 }); // 3 wins, 1 loss
                player1.getData().setHeight(180); // Height in cm
                player1.getData().setWeight(70); // Weight in kg

                Player player2 = buildPlayer("player12", "Jannik", "SINNER", 2);
                player2.setCountry(new Country("picture-france", "FR"));
                player2.getData().setLast(new int[] { 1, 1, 0 }); // 2 wins, 1 loss
                player2.getData().setHeight(170); // Height in cm
                player2.getData().setWeight(65); // Weight in kg

                List<Player> players = List.of(player1, player2);
                when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(players);
                when(playerService.buildStatistics(players)).thenReturn(
                                new StatisticsDto(
                                                new Country("picture-spain", "ES"),
                                                22.37,
                                                170.0));

                // When & Then
                mockMvc.perform(get("/players/statistics"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.country.code", Matchers.is("ES"))) // Assuming Spain has the
                                                                                          // highest win ratio
                                .andExpect(jsonPath("$.averageIMC", Matchers.is(22.37)))
                                .andExpect(jsonPath("$.medianHeight", Matchers.is(170.0)));
        }

        @Test
        void getPlayerStatistics_shouldReturnDefaultStatistics_whenNoPlayersExist() throws Exception {
                // Given
                when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(Collections.emptyList());
                when(playerService.buildStatistics(Collections.emptyList())).thenReturn(
                                new StatisticsDto(
                                                new Country("Unknown", "XX"),
                                                0.0,
                                                0.0));
        }

        @Test
        void getPlayerStatistics_shouldReturnDefaultStatistics_whenPlayersExistWithNoData() throws Exception {
                // Given
                Player player1 = buildPlayer("player101", "Raphael", "NADAL", 1);
                player1.setCountry(new Country("picture-spain", "ES"));
                player1.setData(new PlayerData()); // No data set

                Player player2 = buildPlayer("player12", "Jannik", "SINNER", 2);
                player2.setCountry(new Country("picture-france", "FR"));
                player2.setData(new PlayerData()); // No data set

                List<Player> players = List.of(player1, player2);
                when(playerService.getPlayersSortedByRankBestToWorst()).thenReturn(players);
                when(playerService.buildStatistics(players)).thenReturn(
                                new StatisticsDto(
                                                new Country("Unknown", "XX"),
                                                0.0,
                                                0.0));
                // When & Then
                mockMvc.perform(get("/players/statistics"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.country.code", Matchers.is("XX"))) // Default country
                                .andExpect(jsonPath("$.averageIMC", Matchers.is(0.0)))
                                .andExpect(jsonPath("$.medianHeight", Matchers.is(0.0)));
        }

        @Test
        void addNewPlayer_shouldReturnCreatedPlayer_whenValidDtoIsProvided() throws Exception {
                // Given
                CreatePlayerDto dto = new CreatePlayerDto();
                dto.setFirstname("Roger");
                dto.setLastname("Federer");
                dto.setShortname("RF");
                dto.setSex("M");
                dto.setCountry(new Country("picture-switzerland", "CH"));
                dto.setPicture("picture-url");
                PlayerData data = new PlayerData();
                data.setRank(1);
                dto.setData(data);

                Player savedPlayer = new Player();
                savedPlayer.setId("1L");
                savedPlayer.setFirstname("Roger");
                savedPlayer.setLastname("Federer");
                savedPlayer.setShortname("RF");
                savedPlayer.setSex("M");
                savedPlayer.setCountry(new Country("picture-switzerland", "CH"));
                savedPlayer.setPicture("picture-url");
                savedPlayer.setData(data);

                when(playerService.savePlayer(dto)).thenReturn(savedPlayer);

                // When & Then
                mockMvc.perform(post("/players")
                                .contentType("application/json")
                                .content(new ObjectMapper().writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", Matchers.is(1)))
                                .andExpect(jsonPath("$.firstname", Matchers.is("Roger")))
                                .andExpect(jsonPath("$.lastname", Matchers.is("Federer")))
                                .andExpect(jsonPath("$.shortname", Matchers.is("RF")));
        }

        private Player buildPlayer(String id, String firstname, String lastname, int rank) {
                Player player = new Player();
                player.setId(id);
                player.setFirstname(firstname);
                player.setLastname(lastname);

                PlayerData data = new PlayerData();
                data.setRank(rank);
                player.setData(data);

                return player;
        }
}
