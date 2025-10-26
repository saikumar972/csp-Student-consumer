package com.esrx.student.client;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class HttpClientCall {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String url = "http://localhost:8000/student/";

    public HttpClientCall(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public StudentDto getStudentByNameHttpClient(String name, Map<String, String> headers) {
        //To do headers validation
        String[] headersArray = headers.entrySet()
                .stream()
                .filter(e -> !isRestrictedHeader(e.getKey()))
                .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                .toArray(String[]::new);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "nameV3/" + name))
                .headers(headersArray)
                .timeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("HttpResponse " + httpResponse);
        if (httpResponse.statusCode() == 200) {
            return studentMapper(httpResponse.body());
        } else if (httpResponse.statusCode() != 200) {
            //return StudentErrorDto(httpResponse.body());
            return null;
        }
        return null;
    }

    @SneakyThrows
    private StudentDto studentMapper(String student) {
        return objectMapper.readValue(student, StudentDto.class);
    }

    @SneakyThrows
    private StudentErrorDto studentErrorMapper(String student) {
        return objectMapper.readValue(student, StudentErrorDto.class);
    }

    private boolean isRestrictedHeader(String name) {
        return switch (name.toLowerCase()) {
            case "host", "connection", "content-length", "expect", "date", "upgrade" -> true;
            default -> false;
        };

    }

}
