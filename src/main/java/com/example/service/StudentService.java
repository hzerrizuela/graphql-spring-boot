package com.example.service;

import com.example.entity.Address;
import com.example.entity.Student;
import com.example.entity.Subject;
import com.example.repository.AddressRepository;
import com.example.repository.StudentRepository;
import com.example.repository.SubjectRepository;
import com.example.request.CreateStudentRequest;
import com.example.request.CreateSubjectRequest;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  @Autowired StudentRepository studentRepository;
  @Autowired SubjectRepository subjectRepository;
  @Autowired AddressRepository addressRepository;

  public Student getStudentById(long id) {
    return studentRepository.findById(id).get();
  }

  public Student createStudent(CreateStudentRequest createStudentRequest) {
    Student student = new Student(createStudentRequest);

    Address address = new Address();
    address.setStreet(createStudentRequest.getStreet());
    address.setCity(createStudentRequest.getCity());

    address = addressRepository.save(address);
    student.setAddress(address);
    student = studentRepository.save(student);

    List<Subject> subjectsList = new ArrayList<>();

    if (createStudentRequest.getSubjectsLearning() != null) {
      for (CreateSubjectRequest createSubjectRequest : createStudentRequest.getSubjectsLearning()) {
        var subject = new Subject();
        subject.setSubjectName(createSubjectRequest.getSubjectName());
        subject.setMarksObtained(createSubjectRequest.getMarksObtained());
        subject.setStudent(student);

        subjectsList.add(subject);
      }

      subjectRepository.saveAll(subjectsList);
    }
    student.setLearningSubjects(subjectsList);
    return student;
  }
}
