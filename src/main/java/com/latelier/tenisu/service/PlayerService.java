package com.latelier.tenisu.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.latelier.tenisu.dto.Statistics;
import com.latelier.tenisu.exception.PlayerNotFoundException;
import com.latelier.tenisu.model.Country;
import com.latelier.tenisu.model.Player;
import com.latelier.tenisu.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * retourner la liste des joueurs classés du meilleur au moins bon
     * 
     * @return
     */
    public List<Player> getPlayersSortedByRankBestToWorst() {
        return playerRepository.findAll(Sort.by(Sort.Direction.ASC, "data.rank"));
    }

    /**
     * retourner un joueur par son id
     * 
     * @param id
     * @return Player
     * @throws PlayerNotFoundException si le joueur n'existe pas
     */
    public Player getPlayerById(long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
    }

    // Pays qui a le plus grand ratio de parties gagnées
    public Country getCountryWithHighestWinRatio(List<Player> players) {
        return players.stream()
                .map(this::getRatioByCountry)
                .sorted(Map.Entry.comparingByValue())
                .reduce((first, second) -> second) // Prend le dernier élément, qui a le plus grand ratio
                .map(Entry::getKey)
                .orElse(new Country("Unknown", "XX")); // Valeur par défaut si aucun joueur
    }

    /**
     * Calculer l'IMC moyen des joueurs
     * 
     * @param players
     * @return
     */
    public double getAverageIMC(List<Player> players) {
        return players.stream()
                .mapToDouble(this::getImc)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculer la taille médiane des joueurs
     * 
     * @param players
     * @return
     */
    public double getMedianHeight(List<Player> players) {
        return players.stream()
                .mapToInt(player -> player.getData().getHeight())
                .sorted()
                .skip((players.size() - 1) / 2)
                .limit(2 - players.size() % 2)
                .average()
                .orElse(0.0);
    }

    /**
     * Construire les statistiques à partir de la liste des joueurs
     * 
     * @param players
     * @return
     */
    public Statistics buildStatistics(List<Player> players) {
        Country country = getCountryWithHighestWinRatio(players);
        double averageIMC = getAverageIMC(players);
        double medianHeight = getMedianHeight(players);
        return new Statistics(country, averageIMC, medianHeight);
    }

    private double getImc(Player player) {
        double heightInMeters = player.getData().getHeight() / 100.0; // Convert cm to m
        double weightInKg = player.getData().getWeight() / 1000.0; // Convert g to kg
        return Math.round(weightInKg / (heightInMeters * heightInMeters) * 100.0) / 100.0; // Round to 2 decimal places
    }

    private Entry<Country, Double> getRatioByCountry(Player p) {
        int nbJeux = p.getData().getLast() != null ? p.getData().getLast().length : 0;
        int nbGagnés = (int) Arrays.stream(p.getData().getLast())
                .filter(i -> i > 0)
                .count();

        return Map.entry(p.getCountry(), nbJeux > 0 ? (double) nbGagnés / nbJeux : 0.0);
    }
}
