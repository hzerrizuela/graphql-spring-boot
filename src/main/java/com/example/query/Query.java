package com.example.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.example.request.SampleRequest;
import com.example.response.StudentResponse;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {
  @Autowired StudentService studentService;

  public String firstQuery() {
    return "First query";
  }

  public String fullName(SampleRequest sampleRequest) {
    return sampleRequest.getFirstName() + " " + sampleRequest.getLastName();
  }

  public StudentResponse student(long id) {
    return new StudentResponse(studentService.getStudentById(id));
  }
}
