package com.cst438.controller;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;


// Student Controller
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;
	
	// Story 1: As an administrator, I can add a student to the system.  I input the student email and name.  The student email must not already exists in the system.
	// This allows administrator to add a new student user to the system by name and email. Status will be null and status code set to 0. 
	// If student email already exists, then throw exception
	@PostMapping("/student")
	@Transactional
	public StudentDTO addStudent( @RequestBody StudentDTO studentDTO) {
		
		// Create student object and set name and email from body
		Student student = new Student();
		student.setName(studentDTO.name);
		student.setEmail(studentDTO.email);
		
		// Check system using email to see if student already exists
		Student check = studentRepository.findByEmail(studentDTO.email);
		
		// If no existing student add student to system, otherwise throw exception
		if(check == null) {
			Student savedStudent = studentRepository.save(student);
			StudentDTO result = createStudentDTO(savedStudent);
			return result;	
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student with that email already exists.");
		}
	}
	
	// Story 2: As an administrator, I can put student registration on HOLD
	// Allows administrator to change a students registration status, checking that student exists
	@PostMapping("/student/hold/{student_id}")
	@Transactional
	public void changeStatusToHold (@PathVariable int student_id) {
		
		// Search for student by id in path variable, return null if not found
		Student student = studentRepository.findById(student_id).orElse(null);
		
		// If ID matches students change status, otherwise throw exception
		if(student != null) {
			student.setStatus("HOLD");
			student.setStatusCode(1);
			studentRepository.changeStatus(student.getStatus(), student.getStatusCode(), student.getStudent_id());
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Error Changing Status, no student with that ID" );
		}
	}
	
	// Story 3: As an administrator, I can release the HOLD on student registration.
	// Allows administrator to release students hold status, checking that student exists first
	@PostMapping("/student/release/{student_id}")
	@Transactional
	public void releaseHold (@PathVariable int student_id) {
		
		// Search for student by ID in path
		Student student = studentRepository.findById(student_id).orElse(null);
		
		// If ID matches change status otherwise throw exception
		if(student != null) {
			student.setStatus(null);
			student.setStatusCode(0);
			studentRepository.changeStatus(student.getStatus(), student.getStatusCode(), student.getStudent_id());
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Error Changing Status, no student with that ID" );
		}
	}
	
	
	// Helper method
	private StudentDTO createStudentDTO(Student s) {
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.id = s.getStudent_id();
		studentDTO.name = s.getName();
		studentDTO.email = s.getEmail();
		studentDTO.status = s.getStatus();
		studentDTO.statusCode = s.getStatusCode();
		return studentDTO;
	}

}
