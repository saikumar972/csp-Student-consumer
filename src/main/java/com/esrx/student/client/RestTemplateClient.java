package com.esrx.student.client;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class RestTemplateClient {
    @Autowired
    @Qualifier("restTemplate")
    RestTemplate restTemplate;
    @Value("${student.endpoint}")
    private String studentUrl;
    private final static ObjectMapper objectMapper=new ObjectMapper();
    public StudentDto createStudent(StudentDto studentDto) {
        HttpEntity<StudentDto> httpEntity = new HttpEntity<>(studentDto);
        try {
            ResponseEntity<StudentDto> createStudent = restTemplate.exchange(
                    studentUrl + "/add",
                    HttpMethod.POST,
                    httpEntity,
                    StudentDto.class
            );
            return createStudent.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Unable to reach student service: " + e.getMessage());
        }
    }

    public List<StudentDto> studentDtoList() {
        ResponseEntity<List<StudentDto>> studentResponse = restTemplate.exchange
                (studentUrl + "/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        return studentResponse.getBody();
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
        HttpEntity<StudentInput> httpEntity = new HttpEntity<>(studentInput);
        try {
            ResponseEntity<StudentDto> studentResponse = restTemplate.exchange(
                    studentUrl + "/fetch",
                    HttpMethod.POST,
                    httpEntity,
                    StudentDto.class
            );
            return studentResponse.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    //Rate limiter
    public StudentDto getStudentById(Long id) {
        try{
            return restTemplate.exchange(
                    studentUrl + "/id/" + id,
                    HttpMethod.GET,
                    null,
                    StudentDto.class
            ).getBody();
        }catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    //Retry
    public StudentDto getStudentByName(String name) {
        try {
            ResponseEntity<StudentDto> studentResponse = restTemplate.exchange(
                    studentUrl + "/name/" + name,
                    HttpMethod.GET,
                    null,
                    StudentDto.class
            );
            return studentResponse.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // ✅ 5xx error - RETRY
            throw e;

        }catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    //RetryV2
    public StudentDto getStudentByNameV2(String name) {
        try {
            ResponseEntity<StudentDto> studentResponse = restTemplate.exchange(
                    studentUrl + "/nameV2/" + name,
                    HttpMethod.GET,
                    null,
                    StudentDto.class
            );
            return studentResponse.getBody();
        } catch (HttpClientErrorException errorException) {
            int status = errorException.getStatusCode().value();

            // ✅ ALLOW retry for 429 & 408
            if (status == 408 || status == 429) {
                throw errorException;  // Retry will catch this
            }

            // ❌ Other 4xx should not retry, return normally
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        }
        catch (HttpServerErrorException e) {
            throw e; // ✅ 5xx errors already retryable
        }
        catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    public StudentDto getStudentByNameHttpClient(String name){
        String url = studentUrl + "/nameV2/" + name;
        HttpClient httpClient=HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status == 408 || status == 429) {
                // Allow retry, throw exception to trigger retry logic in caller
                throw new RuntimeException("Retryable error: " + status);
            }
            if (status >= 400 && status < 500) {
                // Other 4xx, do not retry, throw custom exception
                throw new CustomStudentException(response.body());
            }
            if (status >= 500) {
                // 5xx errors, allow retry
                throw new RuntimeException("Server error: " + status);
            }
            // Success (2xx)
            // Parse response.body() as StudentDto using your preferred library (Jackson, Gson, etc.)
            System.out.println(response);
            System.out.println(response.body());
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(response.body(), StudentDto.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    private HttpEntity<StudentInput> getHttpEntity(StudentInput studentInput) {
        String userName = "student";
        String password = "student";
        String encodedString = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
        String authenticated = "Basic " + encodedString;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", authenticated);
        httpHeaders.add("Content-Type", "application/json");
        return new HttpEntity<>(studentInput, httpHeaders);
    }

}
