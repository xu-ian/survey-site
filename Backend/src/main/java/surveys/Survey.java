package surveys;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;


@Entity
public class Survey {

  @Id
  private String id;

  private String name;

  private String owner;

  private List<Question> questions;

  protected Survey() {};

  private Survey(String id, String name, String owner, List<Question> questions) {    
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.questions = questions;
  };

  public String getId() {
    return this.id;
  }

  public String getName(){
    return this.name;
  }

  public String getOwner(){
    return this.owner;
  }

  public List<Question> getQuestions(){
    return this.questions;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public void setQuestions(List<Question> questions){
    this.questions = questions;
  }

  public static SurveyBuilder builder() {
    return new SurveyBuilder();
  }

  public static class SurveyBuilder {

    private String id;
    private String owner;
    private List<Question> questions;
    private String name;

    public SurveyBuilder setName(final String name) {
      this.name = name;
      return this;
    }

    public SurveyBuilder setOwner(final String owner) {
      this.owner = owner;
      return this;
    }

    public SurveyBuilder setQuestions(final List<Question> questions) {
      this.questions = questions;
      return this;
    }

    public SurveyBuilder setId(final String id) {
      this.id = id;
      return this;
    }

    public Survey build() {
      return new Survey(id, name, owner, questions);
    }

  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(!(o instanceof Survey)) {
      return false;
    }
    Survey survey = (Survey) o;
    return Objects.equals(this.id, survey.id) && Objects.equals(this.name, survey.name)
           && Objects.equals(this.questions, survey.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.questions);
  }

  @Override
  public String toString() {
    return "Survey{" + "id=" + this.id + ", name='" + this.name + '\'' + ", questions='" + this.questions + '\'' + '}';
  }

}
