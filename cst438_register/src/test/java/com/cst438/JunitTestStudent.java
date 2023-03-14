package com.cst438;

// JUNIT TEST CLASS FOR STUDENT 

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;



@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
	
	// Using test student ID 1 for testing
	static final String URL = "http://localhost:8080";
	public static final int TEST_STUDENT_ID = 1;
	public static final String TEST_STUDENT_NAME = "test";
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final int TEST_STUDENT_STATUS_CODE = 0;
	public static final String TEST_STUDENT_STATUS = null;
	public static final String TEST_STUDENT_STATUS_HOLD = "HOLD";
	public static final int TEST_STUDENT_STATUS_CODE_HOLD = 1;
	public static final int TEST_STUDENT_BAD_ID = -1;
	
	@MockBean
	StudentRepository studentRepository;
	
	@Autowired
	private MockMvc mvc;
	
	
	// Story 1, Part 1: Test for adding a new student to the system with email and name
	// Status and status code will be set to default values of null and 0
	@Test
	public void addStudent() throws Exception {
		
		MockHttpServletResponse response;
		
		// Set up student with test student values
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// stubs to return data from repository findByEmail and save 
		// Set findByEmail to not find student (null), and when saving to return the test student
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		// Set up test DTO student
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.id = TEST_STUDENT_ID;
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME;
		
		// Perform the Post with the DTO 
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student")
				.content(asJsonString(studentDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that returned data has non zero primary key
		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertNotEquals( 0  , result.id);
		
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
		
	}
	
	
	// Story 2 Part 1: Test for changing existing student registration status to HOLD and code to 1
	@Test
	public void studentHold() throws Exception {
		MockHttpServletResponse response;
		
		// Set up test student object
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// stub to return data for searching for student by ID
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.of(student));

		// Perform the Post with path variable of student ID
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/hold/{id}", TEST_STUDENT_ID))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that repository changeStatus method was called with the parameters to change student with ID 1 to HOLD and code 1
		verify(studentRepository).changeStatus(TEST_STUDENT_STATUS_HOLD, TEST_STUDENT_STATUS_CODE_HOLD, TEST_STUDENT_ID);
		
	}
	
	
	// Story 3, Part 1: Test for releasing student hold registration status on an existing student
	@Test
	public void studentRelease() throws Exception {
		MockHttpServletResponse response;
		
		// Set up test student
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// stub for checking student by ID
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.of(student));

		// Perform post with path variable 
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/release/{id}", TEST_STUDENT_ID))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that repository changeStatus method was called with the parameters to release HOLD status on student with ID 1
		verify(studentRepository).changeStatus(TEST_STUDENT_STATUS, TEST_STUDENT_STATUS_CODE, TEST_STUDENT_ID);
		
	}
	
	
	
	// Story 1 Part 2: Test to try and add a student, but there is an existing email address, which will trigger the Bad Request Exception  and code 400
	// This tests the exception in the StudentController
	@Test
	public void addExistingStudent() throws Exception {
		MockHttpServletResponse response;
		
		// Set up student with test student values
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// Set repository search of findByEmail to find the existing student already in system
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		// Set up test DTO student
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.id = TEST_STUDENT_ID;
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME;
		
		// Perform the Post with the DTO 
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student")
				.content(asJsonString(studentDTO))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = Bad Request (value 400) which shows exception was triggered
		assertEquals(400, response.getStatus());
		
	}
	
	
	// Story 2, Part 2: Test for trying to change registration status on a non existing student ID
	// A Bad Request response (400) status should be returned, this tests the Exception in StudentController
	@Test
	public void studentHoldBadId() throws Exception {
		MockHttpServletResponse response;
		
		// Set up test student object (Not really needed for this test, but left in case later change to test case needed)
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// stub to return data for searching for student by ID, but return null as if student is not in system
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(null);

		// Perform the Post with path variable of a non existing student ID
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/hold/{id}", TEST_STUDENT_BAD_ID))
				.andReturn().getResponse();
		
		// verify that return status = Bad Request (value 400) to show exception was triggered 
		assertEquals(400, response.getStatus());
		
	}
	
	
	// Story 3, Part 2: Test for releasing a student hold registration status, but student ID does not exist in system
	// This tests the exception in StudentController
	@Test
	public void studentReleaseBadId() throws Exception {
		MockHttpServletResponse response;
		
		// Set up test student (Not really needed for this test but left in case alter test case)
		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE);
		student.setStatus(TEST_STUDENT_STATUS);
	
		// stub for checking student by ID, but it returns a null value as if no student is found
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(null);

		// Perform post with path variable of a non existing id
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/release/{id}", TEST_STUDENT_BAD_ID))
				.andReturn().getResponse();
		
		// verify that return status = Bad Request (value 400) and exception was triggered
		assertEquals(400, response.getStatus());
		
	}
	
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}
