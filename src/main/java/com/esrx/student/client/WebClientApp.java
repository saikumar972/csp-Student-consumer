package com.esrx.student.client;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentErrorDto;
import com.esrx.student.dto.WebClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class WebClientApp {
    private final WebClient webClient;
    public WebClientApp(){
        this.webClient=WebClient.create();
    }

    public WebClientResponse getStudentByName(String name, Map<String,String> headers){
        return webClient.get()
                .uri("http://localhost:8000/student/nameV3/"+name)
                .headers(httpHeaders-> headers.forEach(httpHeaders::add))
                .exchangeToMono(response->{
                            int httpStatus=response.statusCode().value();
                            if(httpStatus==200){
                                return response.bodyToMono(StudentDto.class).map(r->new WebClientResponse(r,response.statusCode().value()));
                            }else{
                                return response.bodyToMono(StudentErrorDto.class).map(r->new WebClientResponse(r,response.statusCode().value()));
                            }
                        }
                ).block();

    }

    public Object getStudentByNameV2(String name, Map<String,String> headers){
        return webClient.get()
                .uri("http://localhost:8000/student/nameV3/"+name)
                .headers(httpHeaders-> headers.forEach(httpHeaders::add))
                .exchangeToMono(response->{
                    int httpStatus=response.statusCode().value();
                    if(httpStatus==200){
                        return response.bodyToMono(StudentDto.class);
                    }else{
                        return response.bodyToMono(StudentErrorDto.class);
                    }
                        }
                ).block();

    }
}