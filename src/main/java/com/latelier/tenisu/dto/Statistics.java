package com.latelier.tenisu.dto;

import com.latelier.tenisu.model.Country;

/**
 * Represents statistics for a country, including average IMC and median height.
 */
public record Statistics(Country country, double averageIMC, double medianHeight) {

}
