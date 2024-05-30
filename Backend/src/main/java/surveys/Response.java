package surveys;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Response {

  @Id
  private String id;
  private String surveyName;
  private String sid;
  private List<Answer> answers;


  private Response(String id, String surveyName, String sid, List<Answer> answers) {
    this.id = id;
    this.surveyName = surveyName;
    this.sid = sid;
    this.answers = answers;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getSurveyName() {
    return surveyName;
  }
  public void setSurveyName(String surveyName) {
    this.surveyName = surveyName;
  }
  public String getSid() {
    return sid;
  }
  public void setSid(String sid) {
    this.sid = sid;
  }
  public List<Answer> getAnswers() {
    return answers;
  }
  public void setAnswers(List<Answer> answers) {
    this.answers = answers;
  }

  public static ResponseBuilder builder() {
    return new ResponseBuilder();
  }

  public static class ResponseBuilder {
    
    private String id;
    private String surveyName;
    private String sid;
    private List<Answer> answers;
    
    public ResponseBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public ResponseBuilder setSurveyName(String surveyName) {
      this.surveyName = surveyName;
      return this;
    }

    public ResponseBuilder setSid(String sid) {
      this.sid = sid;
      return this;
    }

    public ResponseBuilder setAnswers(List<Answer> answers) {
      this.answers = answers;
      return this;
    }

    public Response build() {
      return new Response(this.id, this.surveyName, this.sid, this.answers);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((surveyName == null) ? 0 : surveyName.hashCode());
    result = prime * result + ((sid == null) ? 0 : sid.hashCode());
    result = prime * result + ((answers == null) ? 0 : answers.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Response other = (Response) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (surveyName == null) {
      if (other.surveyName != null)
        return false;
    } else if (!surveyName.equals(other.surveyName))
      return false;
    if (sid == null) {
      if (other.sid != null)
        return false;
    } else if (!sid.equals(other.sid))
      return false;
    if (answers == null) {
      if (other.answers != null)
        return false;
    } else if (!answers.equals(other.answers))
      return false;
    return true;
  }
  @Override
  public String toString() {
    return "Response [id=" + id + ", surveyName=" + surveyName + ", sid=" + sid + ", answers=" + answers + "]";
  }

  
}
