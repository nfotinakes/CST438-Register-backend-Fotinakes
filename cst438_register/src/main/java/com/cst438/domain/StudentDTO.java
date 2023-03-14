package com.cst438.domain;

// Student Data Transfer Object

public class StudentDTO {
	public int id;
	public String name;
	public String email;
	public String status;
	public int statusCode;
	
	@Override
	public String toString() {
		return "StudentDTO [email= " + email + ", name= " + name + ", email= " + email + ", status= " + status + ", status code= " + statusCode + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentDTO other = (StudentDTO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

}
