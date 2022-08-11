package com.example.entity;

import com.example.request.CreateStudentRequest;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Student  {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column
  private String email;

  @OneToOne
  @JoinColumn(name="address_id")
  private Address address;

  @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
  private List<Subject> learningSubjects;

  public Student(CreateStudentRequest createStudentRequest) {
  }
}
