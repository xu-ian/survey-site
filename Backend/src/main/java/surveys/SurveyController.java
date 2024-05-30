package surveys;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import surveys.Survey.SurveyBuilder;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
@RequestMapping(path="/user/{uid}/survey")
public class SurveyController {

  @Autowired
  ObjectFactory<HttpSession> httpSessionFactory;
  
  //Creates a new survey with the given name
  @PostMapping(path="")
  public @ResponseBody ResponseEntity<?> addNewSurvey (@PathVariable String uid, @RequestBody Map<String, String> json) {
    try {
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      String id = Helper.generateString(64);
      Helper.stmt = Helper.conn.prepareStatement("INSERT INTO surveys(sid, survey_name, owner) VALUES (?, ?, ?)");
      Helper.stmt.setString(1, id);
      Helper.stmt.setString(2, json.get("name"));
      Helper.stmt.setString(3, uid);
      Helper.stmt.executeUpdate();
      Map<String, String> response = new HashMap<String, String>();
      response.put("id", id);
      response.put("name", json.get("name"));
      return new ResponseEntity<>(Helper.mapper.writeValueAsString(response), HttpStatus.OK);
    } catch(SQLException | JsonProcessingException ex) {
      if(ex instanceof SQLException) {
        Helper.onSQLException((SQLException)ex);
      } else {
        Helper.onJSONException((JsonProcessingException)ex);        
      }
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  //Get a survey by the id, or get all surveys
  @GetMapping(path={"", "/{id}"})
  public @ResponseBody ResponseEntity<?> getSurveys(@PathVariable(required = false) String id) {

    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      if(id == null) {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys");
      } else {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE sid = ?");
        Helper.stmt.setString(1, id);  
      }
      ResultSet results = Helper.stmt.executeQuery();
      List<Survey> surveyList = new ArrayList<>();

      while(results.next()) {
        SurveyBuilder b = Survey.builder().setId(results.getString("sid"))
                                          .setName(results.getString("survey_name"))
                                          .setOwner(results.getString("owner"));
        if(id != null) {
          b = b.setQuestions(Helper.getQuestions(results.getString("sid")));
        }
        Survey s = b.build();
        surveyList.add(s);
      }

      return new ResponseEntity<>(surveyList, HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    
  }

  //Updates the contents of a question
  @PatchMapping(path={"/{id}/questions/{qid}"})
  public @ResponseBody ResponseEntity<?> patchSurvey(@PathVariable String uid, @PathVariable String id, @PathVariable String qid,
                                                     @RequestBody Map<String, String> json) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("UPDATE survey_components SET content_type=?, question=?, additional=? WHERE survey=? AND scid=?");
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      Helper.stmt.setString(1, json.get("content_type"));
      Helper.stmt.setString(2, json.get("name"));
      Helper.stmt.setString(3, json.get("additional"));
      Helper.stmt.setString(4, id);
      Helper.stmt.setString(5, qid);
      Helper.stmt.executeUpdate();
      Helper.stmt.close();

      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE sid=?");
      Helper.stmt.setString(1, id);
      ResultSet results = Helper.stmt.executeQuery();
      results.next();
      Survey survey = Survey.builder().setId(id)
                                      .setName(results.getString("survey_name"))
                                      .setOwner(results.getString("owner"))
                                      .setQuestions(Helper.getQuestions(id))
                                      .build();
      results.close();
      return new ResponseEntity<>(survey, HttpStatus.OK);

    } catch(SQLException ex) {
      Helper.onSQLException((SQLException)ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

  }

  //Adds a question to the end of a survey
  @PostMapping(path={"/{id}/questions"})
  public @ResponseBody ResponseEntity<?> addQuestion(@PathVariable String uid, @PathVariable String id, 
                                                    @RequestBody Map<String, String> json) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM survey_components WHERE survey=?");
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      Helper.stmt.setString(1, id);
      ResultSet result = Helper.stmt.executeQuery();
      int existingQuestions = Helper.countRows(result);
      //Add the new survey question
      Helper.stmt = Helper.conn.prepareStatement("INSERT INTO survey_components(scid, survey, content_type, position, question)" +
                                   "VALUES(?, ?, ?, ?, ?)");
      Helper.stmt.setString(1, Helper.generateString(64));
      Helper.stmt.setString(2, id);
      Helper.stmt.setString(3, json.get("content_type"));
      Helper.stmt.setInt(4, existingQuestions+1);
      Helper.stmt.setString(5, json.get("question"));
                            
      if(Helper.stmt.executeUpdate() != 1) {
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Helper.stmt.close();
      
      //Return the new survey
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE sid=?");
      Helper.stmt.setString(1, id);
      ResultSet results = Helper.stmt.executeQuery();
      results.next();
      Survey survey = Survey.builder().setId(id)
                                      .setName(results.getString("survey_name"))
                                      .setOwner(results.getString("owner"))
                                      .setQuestions(Helper.getQuestions(id))
                                      .build();

      return new ResponseEntity<>(survey, HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

  }

  //Swaps two questions given their positions in a query
  @PatchMapping(path={"/{id}/questions"})
  public @ResponseBody ResponseEntity<?> swapQuestion(@PathVariable String uid, @PathVariable String id, 
                                                      @RequestParam int pos1, @RequestParam int pos2) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM survey_components WHERE survey=? AND (position=? OR position=?)");
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }

      Helper.stmt.setString(1, id);
      Helper.stmt.setInt(2, pos1);
      Helper.stmt.setInt(3, pos2);
      if(Helper.countRows(Helper.stmt.executeQuery()) != 2) {
        return new ResponseEntity<>("One or more of the questsions do not exist", HttpStatus.NOT_FOUND);
      }
      Helper.stmt.close();

      //Swaps the two survey positions by using the -1 position
      Helper.stmt = Helper.conn.prepareStatement("UPDATE survey_components SET position=? WHERE survey=? AND position=?");
      Helper.stmt.setInt(1, -1);
      Helper.stmt.setString(2, id);
      Helper.stmt.setInt(3, pos1);
      Helper.stmt.executeUpdate();
      Helper.stmt.setInt(1, pos1);
      Helper.stmt.setInt(3, pos2);
      Helper.stmt.executeUpdate();
      Helper.stmt.setInt(1, pos2);
      Helper.stmt.setInt(3, -1);
      Helper.stmt.executeUpdate(); 
      Helper.stmt.close();
      
      //Return the new survey
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE sid=?");
      Helper.stmt.setString(1, id);
      ResultSet results = Helper.stmt.executeQuery();
      results.next();
      Survey survey = Survey.builder().setId(id)
                                      .setName(results.getString("survey_name"))
                                      .setOwner(results.getString("owner"))
                                      .setQuestions(Helper.getQuestions(id))
                                      .build();

      return new ResponseEntity<>(survey, HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

  }

  //Removes a question given its' id and the survey it is in
  @DeleteMapping(path={"/{sid}/questions/{qid}"})
  public @ResponseBody ResponseEntity<?> removeQuestion(@PathVariable String uid, @PathVariable String sid, @PathVariable String qid) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM survey_components WHERE scid=? AND survey=?");
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }

      Helper.stmt.setString(1, qid);
      Helper.stmt.setString(2, sid);
      ResultSet results = Helper.stmt.executeQuery();
      if(results.next()) {
        int pos = results.getInt("position");
        Helper.stmt.close();
        results.close();
        Helper.stmt = Helper.conn.prepareStatement("DELETE FROM survey_components WHERE scid=? AND survey=?");
        Helper.stmt.setString(1, qid);
        Helper.stmt.setString(2, sid);
        Helper.stmt.executeUpdate();
        Helper.stmt.close();
        Helper.stmt = Helper.conn.prepareStatement("UPDATE survey_components SET position=position-1 WHERE survey=? AND position>?");
        Helper.stmt.setString(1, sid);
        Helper.stmt.setInt(2, pos);
        Helper.stmt.executeUpdate();
        Helper.stmt.close();
        //Return the new survey
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE sid=?");
        Helper.stmt.setString(1, sid);
        results = Helper.stmt.executeQuery();
        results.next();
        Survey survey = Survey.builder().setId(sid)
                                        .setName(results.getString("survey_name"))
                                        .setOwner(results.getString("owner"))
                                        .setQuestions(Helper.getQuestions(sid))
                                        .build();
        results.close();
        return new ResponseEntity<>(survey, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Survey Question not found", HttpStatus.NOT_FOUND);
      }
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  //Remove a survey by id
  @DeleteMapping(path="/{id}")
  public @ResponseBody ResponseEntity<?> removeSurvey(@PathVariable String uid, @PathVariable String id) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("DELETE FROM surveys WHERE sid=?");
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }

      Helper.stmt.setString(1, id);
      if(Helper.stmt.executeUpdate() == 1) {
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
      }
      return new ResponseEntity<>("Survey not found", HttpStatus.NOT_FOUND);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  //Create a new response to a survey
  @PostMapping(path="/{id}/responses")
  public @ResponseBody ResponseEntity<?> createResponse(@PathVariable String uid, @PathVariable String id) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("INSERT INTO responses(rid, owner, survey) VALUES(?,?,?)");
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      String rid = Helper.generateString(64);
      Helper.stmt.setString(1, rid);
      Helper.stmt.setString(2, uid);
      Helper.stmt.setString(3, id);
      Helper.stmt.executeUpdate();

      Helper.stmt.close();
      
      Helper.stmt = Helper.conn.prepareStatement("SELECT scid FROM survey_components WHERE survey=?");
      Helper.stmt.setString(1, id);
      ResultSet result = Helper.stmt.executeQuery();
      while(result.next()) {
        Helper.createResponseComponent(rid, result.getString("scid"), "");
      }

      return new ResponseEntity<>(id, HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  //Get a all responses to a survey
  @GetMapping(path="/{id}/responses")
  public @ResponseBody ResponseEntity<?> getSurveyResponses(@PathVariable String uid, @PathVariable String id) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM responses WHERE survey=?");
      Helper.stmt.setString(1, id);

      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      //TODO: Check if user owns survey
      ResultSet results = Helper.stmt.executeQuery();
      List<Response> responses = new ArrayList<Response>();
      String surveyName = Helper.getSurveyName(id);
      while(results.next()) {
        String responseId = results.getString("rid");
        responses.add(Helper.getResponses(responseId, surveyName, id));
      }
      return new ResponseEntity<>(responses, HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

  }

  //Update response to the survey
  //json is a map of question id to responses to question
  @PatchMapping(path="{id}/responses/{rid}")
  public @ResponseBody ResponseEntity<?> updateResponse(@PathVariable String uid, @PathVariable String id, 
           @PathVariable String rid, @RequestBody Map<String, String> json) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT scid FROM survey_components WHERE survey=?");
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      
      Helper.stmt.setString(1, id);
      ResultSet results = Helper.stmt.executeQuery();
      while(results.next()) {
        String scid = results.getString("scid");
        Helper.createResponseComponent(rid, scid, json.get(scid));
      }
      return new ResponseEntity<>(Helper.getResponses(rid, Helper.getSurveyName(id), 
                                  id), HttpStatus.OK);
    } catch(SQLException ex) {
      Helper.onSQLException(ex);
      return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        Helper.conn.close();
        Helper.stmt.close();
      } catch(SQLException ex) {
        Helper.onSQLException(ex);
        return new ResponseEntity<>("SQL Error: " + Helper.error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

  }

  
}