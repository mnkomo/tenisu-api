package com.latelier.tenisu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/sorted-by-rank")
    public ResponseEntity<List<Player>> getAllPlayersSortedByRank() {
        List<Player> players = playerService.getPlayersSortedByRankBestToWorst();
        if (players.isEmpty()) {
            throw new NoContentException("No players found");
        }
        return ResponseEntity.ok(players);
    }
}
