package com.esrx.student.client;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RestServiceClient {
    @Value("${student.endpoint}")
    private String studentUrl;
    private org.springframework.web.client.RestClient restClient;

    @PostConstruct
    public void init(){
        this.restClient= org.springframework.web.client.RestClient.builder()
                .baseUrl(studentUrl)
                .build();
    }

    public StudentDto addStudent(StudentDto studentDto){
        return restClient.post()
                .uri("/add")
                .body(studentDto)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        ((request, response) -> {
                            System.out.println(request.getURI());
                            throw new CustomStudentException(new String(response.getBody().readAllBytes(),StandardCharsets.UTF_8));
                        })
                ).onStatus(
                        HttpStatusCode::is5xxServerError,
                        ((request, response) -> {
                            System.out.println(request.getMethod());
                            throw new CustomStudentException(new String(response.getBody().readAllBytes(),StandardCharsets.UTF_8));
                        })
                ).body(StudentDto.class);
    }

    public List<StudentDto> studentDtoList(){
        return restClient.get()
                .uri("/all")
                .retrieve()
                .body(new ParameterizedTypeReference<List<StudentDto>>() {});
    }

    public StudentDto getStudentById(Long id) {
        return restClient
                .get()
                .uri("/id/{id}",id)
                .retrieve()
                .body(StudentDto.class);
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
        return restClient
                .post()
                .uri(studentUrl+"/fetch")
                .body(studentInput)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            System.out.println(request.getURI());
                            String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new CustomStudentException(errorBody);
                        })
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            String errorMessage=new String(response.getBody().readAllBytes(),StandardCharsets.UTF_8);
                            System.out.println(request+"----");
                            throw new CustomStudentException(errorMessage);
                        })
                .body(StudentDto.class);
    }

    public String deleteStudentById(int id){
        restClient.delete()
                .uri("/id/{id}",id)
                .retrieve()
                //.toBodilessEntity();
                .body(String.class);
        return "deleted successfully";
    }

    public StudentDto getStudentByName(String name) {
        return restClient
                .get()
                .uri("/name/{name}",name)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,((request, response) ->
                {
                    throw new CustomStudentException(new String(response.getBody().readAllBytes(),StandardCharsets.UTF_8));

                }))
                .body(StudentDto.class);
    }


}

/*
@PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(studentUrl)
                .defaultStatusHandler(new CustomErrorHandler()) // global error handler
                .build();
    }


    static class CustomErrorHandler implements RestClient.ResponseSpec.ErrorHandler {

        @Override
        public void handle(HttpStatus status, ClientHttpResponse response) throws IOException {
            if (status.is4xxClientError()) {
                throw new RestClientException("Client error: " + status);
            } else if (status.is5xxServerError()) {
                throw new RestClientException("Server error: " + status);
            }
        }
    }
*/