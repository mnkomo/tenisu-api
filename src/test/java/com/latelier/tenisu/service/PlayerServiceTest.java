package com.latelier.tenisu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.latelier.tenisu.exception.PlayerNotFoundException;
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