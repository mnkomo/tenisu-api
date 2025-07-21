package com.latelier.tenisu.model;

import lombok.Data;

@Data
public class PlayerData {
    private int rank;
    private int points;
    private int weight;
    private int height;
    private int age;
    private int[] last;
}
