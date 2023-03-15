package com.cst438.domain;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends CrudRepository <Student, Integer> {
	
	// declare the following method to return a single Student object
	// default JPA behavior that findBy methods return List<Student> except for findById.
	public Student findByEmail(String email);
	
	// To save student to database
	@SuppressWarnings("unchecked")
	Student save(Student s); 
	
	// To update student status 
	@Modifying
	@Query("UPDATE Student s SET s.status=:status, s.statusCode=:status_code WHERE s.student_id=:student_id")
	void changeStatus( @Param("status") String status, @Param("status_code") int statusCode, @Param("student_id") int student_id );

}
