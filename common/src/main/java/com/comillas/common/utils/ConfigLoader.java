package com.comillas.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class ConfigLoader {

    //private static final AlertLogger logging = new AlertLogger(PropertyUtils.class.getName());

    private static final String commonPath = "/src/main/java/resources/";

    public static Properties loadProperties(String module, String fileName) throws IOException {
        Properties props = new Properties();
        // 1. Try to load from classpath
        InputStream inputFile = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputFile != null) {
            props.load(inputFile);
            return props;
        }

        // 2. Try to load from external path
        String externalPath = System.getProperty("user.dir") + File.separator + module + commonPath + fileName;
        Path path = Paths.get(externalPath);
        if (Files.exists(path)) {
            InputStream externalFile = Files.newInputStream(path);
            props.load(externalFile);
            return props;
        }

        // 3. throw Except
        String msg = "No se encontró el archivo '" + fileName + "' ni en el classpath ni en el sistema de archivos.";
        throw new FileNotFoundException(msg);
    }

    public static <T> List<T> loadJsonList(String module, String fileName, TypeReference<List<T>> typeRef) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Try to load from classpath
        InputStream inputJsonFile = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputJsonFile != null) {
            return mapper.readValue(inputJsonFile, typeRef);
        }

        // 2. Try to load from external path
        String externalJsonPath = System.getProperty("user.dir") + File.separator + module + commonPath + fileName;
        File externalJsonfile = new File(externalJsonPath);
        if (externalJsonfile.exists()) {
            return mapper.readValue(externalJsonfile, typeRef);
        }

        // 3. throw Except
        String msg = "No se encontró el archivo '" + fileName + "' ni en el classpath ni en el sistema de archivos.";
        throw new FileNotFoundException(msg);
    }
}
