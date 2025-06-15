package com.comillas.ingestor;

import com.comillas.common.model.City;
import com.comillas.common.utils.ConfigLoader;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class CityLoader {
    public static List<City> loadCities(String module, String fileName) throws Exception {
        return ConfigLoader.loadJsonList( module, fileName, new TypeReference<List<City>>() {});
    }
}