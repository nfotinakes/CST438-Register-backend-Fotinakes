package com.cst438.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.EnrollmentDTO;


public class GradebookServiceREST extends GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	String gradebook_url;
	
	public GradebookServiceREST() {
		System.out.println("REST grade book service");
	}

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		
		//Completed for Assignment 4
		// When a student adds a class, a POST is also sent to the Gradebook Service backend
		// to enroll the student into the gradebook database
		
		// Create DTO and set values and print the enrollment DTO to console
		EnrollmentDTO enrollment = new EnrollmentDTO();
		enrollment.course_id = course_id;
		enrollment.studentEmail = student_email;
		enrollment.studentName = student_name;
		System.out.println("POST to gradebook " + enrollment);
		
		// Use postForObject to send to POST to gradebook and console log the response. 
		EnrollmentDTO response = restTemplate.postForObject(gradebook_url+"/enrollment", enrollment, EnrollmentDTO.class);
		System.out.println("Response from gradebook " + response);
		
	}

}
