package com.esrx.student.utiliy;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

public class JsonConverter {
    public static String jsonToString(String fileName) throws IOException {
        ClassPathResource classPathResource=new ClassPathResource(fileName);
        return new String(Files.readAllBytes(classPathResource.getFile().toPath()));
    }
}
