package com.esrx.student.client;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RestTemplateClientTest {
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    RestTemplateClient restTemplateClient;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    public void testStudentDtoList_success(){
        List<StudentDto> mockList=List.of(new StudentDto(1L,"saikumar",List.of("science,commerce"),LocalDate.of(2021,1,1),2300,"commerce"),new StudentDto(2L,"saikumar",List.of("science,commerce"),LocalDate.of(2021,1,1),2300,"commerce"));
        ResponseEntity<List<StudentDto>> response=new ResponseEntity<>(mockList, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(response);
        List<StudentDto> result = restTemplateClient.studentDtoList();
        assertEquals(mockList.size(),result.size());
    }

    @Test
    public void testGetStudentById_success(){
        Long input=1L;
        StudentDto expectedValue=new StudentDto(1L,"saikumar",List.of("science,commerce"),LocalDate.of(2021,1,1),2300,"commerce");
        ResponseEntity<StudentDto> response=new ResponseEntity<>(expectedValue,HttpStatus.OK);
        when(restTemplate.exchange(anyString(),eq(HttpMethod.GET),isNull(),eq(StudentDto.class))).thenReturn(response);
        StudentDto output= restTemplateClient.getStudentById(input);
        assertEquals(expectedValue.getId(),output.getId());
    }

    @Test
    public void testGetStudentById_invalidId() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(StudentDto.class)))
                .thenThrow(HttpClientErrorException.BadRequest.class);

        assertThrows(CustomStudentException.class, () -> restTemplateClient.getStudentById(999L));
    }

    @Test
    public void testGetStudentById_serviceDown() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(StudentDto.class)))
                .thenThrow(ResourceAccessException.class);
        assertThrows(RuntimeException.class, () -> restTemplateClient.getStudentById(1L));
    }

    @Test
    public void testGetStudentByNameAndId_success(){
        StudentInput studentInput=new StudentInput(1L,"saikumar");
        StudentDto expectedValue=new StudentDto(1L,"saikumar",List.of("science,commerce"),LocalDate.of(2021,1,1),2300,"commerce");
        ResponseEntity<StudentDto> response=new ResponseEntity<>(expectedValue,HttpStatus.OK);
        when(restTemplate.exchange(anyString(),eq(HttpMethod.POST),any(HttpEntity.class),eq(StudentDto.class))).thenReturn(response);
        StudentDto output= restTemplateClient.getStudentByIdAndName(studentInput);
        assertEquals(expectedValue.getName(),output.getName());
    }
    @Test
    public void testGetStudentByIdAndName_invalidInput() {
        StudentInput studentInput=new StudentInput(19L,"saikumar");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(StudentDto.class)))
                .thenThrow(HttpClientErrorException.BadRequest.class);
        assertThrows(CustomStudentException.class, () -> restTemplateClient.getStudentByIdAndName(studentInput));
    }

    @Test
    public void testGetStudentByIdAndName_serverError() {
        StudentInput studentInput=new StudentInput(19L,"saikumar");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(StudentDto.class)))
                .thenThrow(HttpServerErrorException.InternalServerError.class);
        assertThrows(RuntimeException.class, () -> restTemplateClient.getStudentByIdAndName(studentInput));
    }

}
