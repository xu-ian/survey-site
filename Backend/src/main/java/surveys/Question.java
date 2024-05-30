package surveys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Question implements Comparable<Question>, Serializable{
  
  @Id
  private String id;

  private String name; 

  private Integer position;

  private String type;

  private String additionalData;

  protected Question() {};

  private Question(String id, String name, Integer position, String type, String additionalData) {
    this.id = id;
    this.name = name;
    this.position = position;
    this.type = type;
    this.additionalData = additionalData;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Integer getPosition() {
    return this.position;
  }

  public String getType() {
    return this.type;
  }

  public String getAdditionalData() {
    return this.additionalData;
  }

  public String setId() {
    return this.id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setAdditionalData(String additionalData) {
    this.additionalData = additionalData;
  }

  public static QuestionBuilder builder() {
    return new QuestionBuilder();
  }

  public static class QuestionBuilder {

    private String id;
    private Integer position;
    private String type;
    private String name;
    private String additional;

    public QuestionBuilder setName(final String name) {
      this.name = name;
      return this;
    }

    public QuestionBuilder setPosition(final Integer position) {
      this.position = position;
      return this;
    }

    public QuestionBuilder setType(final String type) {
      this.type = type;
      return this;
    }

    public QuestionBuilder setId(final String id) {
      this.id = id;
      return this;
    }

    public QuestionBuilder setAdditionalData(final String additional) {
      this.additional = additional;
      return this;
    }

    public Question build() {
      return new Question(id, name, position, type, additional);
    }

  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof Question)) {
      return false;
    }

    Question q = (Question) o;

    return this.getName().equals(q.getName()) && this.getType().equals(q.getType()) 
          && this.getPosition().equals(q.getPosition());
  }

  @Override
  public int compareTo(Question o) {
    if(this.getPosition() > o.getPosition()) {
      return 1;
    } else if(this.getPosition() < o.getPosition()) {
      return -1;
    } else {
      return 0;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.type, this.position);
  }

  @Override
  public String toString() {
    //position is only added for debugging purposes
    return "Question{" + "id=" + this.id + ", name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", position='" + this.position + '\'' + '}';
  }

}
