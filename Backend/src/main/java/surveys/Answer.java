package surveys;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;


@Entity
public class Answer implements Serializable {
  
  @Id
  private String id;

  private Question question;

  private String answer;

  private Answer(String id, Question question, String answer) {
    this.id = id;
    this.question = question;
    this.answer = answer;
  }

    public String getId() {
    return this.id;
  }

  public Question getQuestion(){
    return this.question;
  }

  public String getResponse(){
    return this.answer;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public void setResponse(String answer){
    this.answer = answer;
  }

  public static AnswerBuilder builder() {
    return new AnswerBuilder();
  }

  public static class AnswerBuilder {

    private String id;
    private Question question;
    private String answer;

    public AnswerBuilder setResponse(final String answer) {
      this.answer = answer;
      return this;
    }

    public AnswerBuilder setQuestion(final Question question) {
      this.question = question;
      return this;
    }

    public AnswerBuilder setId(final String id) {
      this.id = id;
      return this;
    }

    public Answer build() {
      return new Answer(id, question, answer);
    }

  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof Answer)) {
      return false;
    }
    Answer answer = (Answer) o;
    return Objects.equals(this.id, answer.id) && Objects.equals(this.question, answer.question) 
            && Objects.equals(this.answer, answer.answer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.answer);
  }

  @Override
  public String toString() {
    return "Response{" + "id=" + this.id + ", question='" + this.question + '\'' + ", answer='" + this.answer + '\'' + '}';
  }
}
