package com.latelier.tenisu.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.latelier.tenisu.model.Player;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class MongoDbJsonInitializer {

    private final ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner initDataBaseFromJson(MongoTemplate mongoTemplate) {
        return args -> {

            System.out.println("Initializing MongoDB data from JSON file...");

            if (mongoTemplate.collectionExists(Player.class)) {
                mongoTemplate.dropCollection(Player.class);
                System.out.println("Dropped existing 'players' collection.");
            }

            try {
                // Charge le fichier JSON depuis le classpath
                InputStream inputStream = new ClassPathResource("data/initial_data.json").getInputStream();

                // Désérialise le JSON en une liste d'objets Player
                // TypeReference est nécessaire pour désérialiser en une List<Player>
                List<Player> players = objectMapper.readValue(inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));

                // Insère tous les joueurs dans la base de données
                mongoTemplate.insertAll(players);
                System.out.println("Inserted " + players.size() + " players from JSON file.");

            } catch (Exception e) {
                System.err.println("Failed to load initial data from JSON: " + e.getMessage());
                e.printStackTrace();
            }

            // Vérification (optionnel)
            Query query = new Query();
            query.fields().include("id");

            long count = mongoTemplate.count(query, Player.class);
            System.out.println("Total players in DB after JSON init: " + count);
        };

    }

}
