package com.esrx.student.controller;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import com.esrx.student.service.RestTemplateService;
import com.esrx.student.utiliy.JsonConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentRestTemplateController.class)
public class StudentControllerTest {
    @Autowired
    StudentRestTemplateController studentRestTemplateController;
    @MockitoBean
    RestTemplateService service;
    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Test
    public void test_getStudents_success() throws Exception {
        String fileName="studentList.json";
        objectMapper.registerModule(new JavaTimeModule());
        String jsonOutput= JsonConverter.jsonToString(fileName);
        List<StudentDto> studentList=objectMapper.readValue(jsonOutput, new TypeReference<>() {});
        when(service.studentDtoList()).thenReturn(studentList);
        mockMvc.perform(MockMvcRequestBuilders.get("/student/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("saiKumar"));
    }

    @Test
    public void test_getStudentById_success() throws Exception {
        String filename="studentById.json";
        Long id=1L;
        objectMapper.registerModule(new JavaTimeModule());
        String jsonOutput=JsonConverter.jsonToString(filename);
        StudentDto studentOutput=objectMapper.readValue(jsonOutput, StudentDto.class);
        when(service.getStudentById(id)).thenReturn(studentOutput);
        mockMvc.perform(MockMvcRequestBuilders.get("/student/id/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("saikumar"));
    }

    @Test
    public void test_getStudentByNameAndId_success() throws Exception {
        String filename="studentByNameAndId.json";
        String inputFileName="StudentIdAndName.json";
        String studentInput= JsonConverter.jsonToString(inputFileName);
        objectMapper.registerModule(new JavaTimeModule());
        String jsonOutput=JsonConverter.jsonToString(filename);
        StudentDto studentOutput=objectMapper.readValue(jsonOutput,StudentDto.class);
        StudentInput studentInput1=objectMapper.readValue(studentInput,StudentInput.class);
        when(service.getStudentByIdAndName(studentInput1)).thenReturn(studentOutput);
        mockMvc.perform(MockMvcRequestBuilders.post("/student/fetch").contentType(MediaType.APPLICATION_JSON).content(studentInput))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("saikumar"));
    }


}
