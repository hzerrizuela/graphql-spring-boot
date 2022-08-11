package com.example.response;

import com.example.entity.Subject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectResponse {

  private long id;

  private String subjectName;

  private Double marksObtained;

  private Subject subject; // this is for internal use only

  public SubjectResponse(Subject subject) {
    this.subject = subject;

    this.id = subject.getId();
    this.subjectName = subject.getSubjectName();
    this.marksObtained = subject.getMarksObtained();
  }
}
