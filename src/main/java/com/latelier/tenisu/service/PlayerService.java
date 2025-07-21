package com.latelier.tenisu.service;

import com.latelier.tenisu.exception.PlayerNotFoundException;
import com.latelier.tenisu.model.Player;
import com.latelier.tenisu.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * retourner la liste des joueurs classés du meilleur au moins bon
     * @return
     */
    public List<Player> getPlayersSortedByRankBestToWorst() {
        return playerRepository.findAll(Sort.by(Sort.Direction.ASC, "data.rank"));
    }

    /**
     * retourner un joueur par son id
     * @param id
     * @return Player
     * @throws PlayerNotFoundException si le joueur n'existe pas
     */
    public Player getPlayerById(long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
    }
}
