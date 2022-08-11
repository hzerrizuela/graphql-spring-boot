package com.example.response;

import com.example.entity.Student;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String street;
  private String city;
  private List<SubjectResponse> learningSubjects;
  // for internal use only DO NOT PUT IN SCHEMA
  private Student student;
  private String fullName;

  public StudentResponse(Student student) {
    this.student = student;

    this.id = student.getId();
    this.firstName = student.getFirstName();
    this.lastName = student.getLastName();
    this.email = student.getEmail();
    this.street = student.getAddress().getStreet();
    this.city = student.getAddress().getCity();
    // ...TODO FINISH
  }
}
