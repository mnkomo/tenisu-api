package com.latelier.tenisu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.latelier.tenisu.dto.Statistics;
import com.latelier.tenisu.exception.PlayerNotFoundException;
import com.latelier.tenisu.model.Country;
import com.latelier.tenisu.model.Player;
import com.latelier.tenisu.model.PlayerData;
import com.latelier.tenisu.repository.PlayerRepository;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void getPlayersSortedByRankBestToWorst_shouldReturnEmptyList_whenNoContentFound() {
        // Given
        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of());

        // When
        List<Player> players = playerService.getPlayersSortedByRankBestToWorst();

        // Then
        assertThat(players).isNotNull().hasSize(0);
    }

    @Test
    void getPlayersSortedByRankBestToWorst_shouldReturnSortedList() {
        // Given
        Player player1 = buildPlayer(101, "Raphael", "NADAL", 1);
        Player player2 = buildPlayer(12, "", "SINNER", 2);
        Player player3 = buildPlayer(221, "Serena", "WILLIAM", 3);
        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of(
                player1, player2, player3));
        ArgumentCaptor<Sort> sortArgumentCaptor = ArgumentCaptor.forClass(Sort.class);

        // When
        List<Player> players = playerService.getPlayersSortedByRankBestToWorst();

        // Then
        assertThat(players).isNotNull().hasSize(3);

        // --- Vérification de l'appel de tri ---
        verify(playerRepository).findAll(sortArgumentCaptor.capture());

        Sort capturedSort = sortArgumentCaptor.getValue();
        assertNotNull(capturedSort);
        assertEquals(1, capturedSort.stream().count());

        Sort.Order order = capturedSort.stream().findFirst().orElse(null);
        assertNotNull(order);
        assertEquals("data.rank", order.getProperty());
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void getPlayerById_shouldReturnPlayer_whenPlayerExists() {
        // Given
        long playerId = 1L;
        Player expectedPlayer = buildPlayer(playerId, "Roger", "FEDERER", 1);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(expectedPlayer));

        // When
        Player actualPlayer = playerService.getPlayerById(playerId);

        // Then
        verify(playerRepository).findById(playerId);

        Assertions.assertThat(actualPlayer)
                .isNotNull()
                .isEqualTo(expectedPlayer);
    }

    @Test
    void getPlayerById_shouldThrowPlayerNotFoundException_whenPlayerDoesNotExist() {
        // Given
        long playerId = 999L;
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> playerService.getPlayerById(playerId))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessage("Player not found with id: " + playerId);
    }

    @Test
    void getCountryWithHighestWinRatio_shouldReturnCountryWithHighestWinRatio() {
        // Given
        Player player1 = new Player();
        player1.setData(new PlayerData());
        player1.getData().setLast(new int[] { 1, -1, 1, 1 }); // 3 wins, 1 loss
        player1.setCountry(new Country("picture-france", "FR"));

        Player player2 = new Player();
        player2.setData(new PlayerData());
        player2.getData().setLast(new int[] { 1, 1, 1 }); // 3 wins, no losses
        player2.setCountry(new Country("picture-spain", "ES"));

        Player player3 = new Player();
        player3.setData(new PlayerData());
        player3.getData().setLast(new int[] { 1, 0, 1 }); // 2 wins, 1 loss
        player3.setCountry(new Country("picture-usa", "US"));

        List<Player> players = List.of(player1, player2, player3);

        // When
        Country countryWithHighestWinRatio = playerService.getCountryWithHighestWinRatio(players);

        // Then
        Assertions.assertThat(countryWithHighestWinRatio)
                .isNotNull()
                .isEqualTo(player2.getCountry()); // ES has the highest win ratio (3/3)
    }

    @Test
    void getAverageIMC_shouldReturnCorrectAverageIMC() {
        // Given
        Player player1 = new Player();
        player1.setData(new PlayerData());
        player1.getData().setHeight(180); // 1.80 m
        player1.getData().setWeight(75000); // 75 kg

        Player player2 = new Player();
        player2.setData(new PlayerData());
        player2.getData().setHeight(170); // 1.70 m
        player2.getData().setWeight(65000); // 65 kg

        Player player3 = new Player();
        player3.setData(new PlayerData());
        player3.getData().setHeight(160); // 1.60 m
        player3.getData().setWeight(55000); // 55 kg
        List<Player> players = List.of(player1, player2, player3);

        // When
        double averageIMC = playerService.getAverageIMC(players);

        // Then
        assertEquals(22.37, averageIMC, 0.01); // Average
    }

    @Test
    void getMedianHeight_shouldReturnCorrectMedianHeight() {
        // Given
        Player player1 = new Player();
        player1.setData(new PlayerData());
        player1.getData().setHeight(180); // 1.80 m

        Player player2 = new Player();
        player2.setData(new PlayerData());
        player2.getData().setHeight(170); // 1.70 m

        Player player3 = new Player();
        player3.setData(new PlayerData());
        player3.getData().setHeight(160); // 1.60 m
        List<Player> players = List.of(player1, player2, player3);

        // When
        double medianHeight = playerService.getMedianHeight(players);

        // Then
        assertEquals(170.0, medianHeight); // Median of 160, 170, 180 is 170
    }

    @Test
    void buildStatistics_shouldReturnCorrectStatistics() {
        // Given
        Player player1 = new Player();
        player1.setData(new PlayerData());
        player1.getData().setHeight(180); // 1.80 m
        player1.getData().setWeight(75000); // 75 kg

        player1.setCountry(new Country("picture-france", "FR"));
        player1.getData().setLast(new int[] { 1, -1, 1, 1 }); // 3 wins, 1 loss

        Player player2 = new Player();
        player2.setData(new PlayerData());
        player2.getData().setHeight(170); // 1.70 m
        player2.getData().setWeight(65000); // 65 kg
        player2.setCountry(new Country("picture-spain", "ES"));
        player2.getData().setLast(new int[] { 1, 1, 1 }); // 3 wins, no losses

        Player player3 = new Player();
        player3.setData(new PlayerData());
        player3.getData().setHeight(160); // 1.60 m
        player3.getData().setWeight(55000); // 55 kg
        player3.setCountry(new Country("picture-usa", "US"));
        player3.getData().setLast(new int[] { 1, 0, 1 }); // 2 wins, 1 loss

        List<Player> players = List.of(player1, player2, player3);

        // When
        Statistics statistics = playerService.buildStatistics(players);

        // Then
        assertNotNull(statistics);
        assertEquals("ES", statistics.country().getCode()); // Spain has the highest win
        assertEquals(22.37, statistics.averageIMC(), 0.01);
        assertEquals(170.0, statistics.medianHeight(), 0.01);
        assertEquals("picture-spain", statistics.country().getPicture());
    }

    @Test
    void buildStatistics_shouldReturnEmptyStatistics_whenNoPlayers() {
        // Given
        List<Player> players = List.of();

        // When
        Statistics statistics = playerService.buildStatistics(players);

        // Then
        assertNotNull(statistics);
        assertEquals(new Country("Unknown", "XX"), statistics.country());
        assertEquals(0.0, statistics.averageIMC());
        assertEquals(0.0, statistics.medianHeight());
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