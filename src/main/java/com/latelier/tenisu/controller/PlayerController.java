package com.latelier.tenisu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.latelier.tenisu.dto.CreatePlayerDto;
import com.latelier.tenisu.dto.StatisticsDto;
import com.latelier.tenisu.exception.NoContentException;
import com.latelier.tenisu.model.Player;
import com.latelier.tenisu.service.PlayerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    /**
     * Retrieves all players sorted by their rank from best to worst.
     *
     * @return a ResponseEntity containing a list of players sorted by rank.
     */
    @GetMapping()
    public ResponseEntity<List<Player>> getAllPlayersSortedByRank() {
        List<Player> players = playerService.getPlayersSortedByRankBestToWorst();
        if (players.isEmpty()) {
            throw new NoContentException("No players found");
        }
        return ResponseEntity.ok(players);
    }

    /**
     * Retrieves a player by their ID.
     *
     * @param id the ID of the player to retrieve.
     * @return a ResponseEntity containing the player with the specified ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        Player player = playerService.getPlayerById(id);
        return ResponseEntity.ok(player);
    }

    /**
     * Retrieves statistics about players, including the country with the highest
     * win ratio,
     * average IMC, and median height.
     *
     * @return a ResponseEntity containing the statistics of players.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getPlayerStatistics() {
        List<Player> players = playerService.getPlayersSortedByRankBestToWorst();
        StatisticsDto statistics = playerService.buildStatistics(players);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping()
    public ResponseEntity<Player> addNewPlayer(@RequestBody CreatePlayerDto dto) {
        Player newPlayer = playerService.savePlayer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPlayer);
    }

}
