package com.esrx.student.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WebClientResponse {
    private Object object;
    @JsonIgnore
    private int status;
}
