package com.example.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudentRequest {

  private String firstName;
  private String lastName;
  private String email;
  //Address
  private String street;
  private String city;
  private List<CreateSubjectRequest> subjectsLearning;

}
