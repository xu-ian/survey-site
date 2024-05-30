package surveys;

import java.sql.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
@RequestMapping(path="/user")
public class UserController {

  @Autowired
  ObjectFactory<HttpSession> httpSessionFactory;

  //Creates a new user with username and password
  @PostMapping(path={"/signup"}) 
  public @ResponseBody ResponseEntity<?> SignUp(@RequestBody Map<String, String> json) {
    try {

      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM users WHERE username=?");
      Helper.stmt.setString(1, json.get("username"));
      if(Helper.countRows(Helper.stmt.executeQuery()) > 0) {
        return new ResponseEntity<>("Username already exists", HttpStatus.UNPROCESSABLE_ENTITY);
      }
      Helper.stmt.close();

      Helper.stmt = Helper.conn.prepareStatement("INSERT INTO users(uid, username, password) VALUES(?, ?, ?)");
      Helper.stmt.setString(1, Helper.generateString(64));
      Helper.stmt.setString(2, json.get("username"));
      Helper.stmt.setString(3, Helper.hashPassword(json.get("password"), Helper.generateString(32)));
      Helper.stmt.executeUpdate();

      return new ResponseEntity<>("Added successfully", HttpStatus.OK);
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

  //Logs in the user given a username and password
  @PostMapping(path={"/login"})
  public @ResponseBody ResponseEntity<?> LogIn(@RequestBody Map<String, String> json, HttpServletRequest request) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM users WHERE username=?");
      Helper.stmt.setString(1, json.get("username"));
      ResultSet results = Helper.stmt.executeQuery();
      if(!results.next()) {
        return new ResponseEntity<>("Username does not exist", HttpStatus.UNPROCESSABLE_ENTITY);
      }
      String password = results.getString("password");
      if(Helper.checkPassword(json.get("password"), password)) {
        httpSessionFactory.getObject().setAttribute("username", results.getString("uid"));
        ResponseCookie cookie = ResponseCookie.from("Username", results.getString("uid"))
        .path("/")
        .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged in");
      }
      return new ResponseEntity<>("Password is incorrect", HttpStatus.UNAUTHORIZED);
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

  //Logs out the user
  @PostMapping(path={"/logout"})
  public @ResponseBody ResponseEntity<?> LogOut(@RequestBody Map<String, String> json) {
    //TODO: Remove the browser cookies and session cookies here
    HttpSession session = httpSessionFactory.getObject();
    ResponseCookie cookie = ResponseCookie.from("Username", (String)session.getAttribute("username"))
        .path("/")
        .maxAge(0)
        .build();
    httpSessionFactory.getObject().removeAttribute("Username");
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body("Logged Out");
  }

  //Retrieves all or one of your surveys
  @GetMapping(path={"/{uid}/surveys", "/{uid}/surveys/{sid}"})
  public @ResponseBody ResponseEntity<?> GetSurveys(@PathVariable String uid, 
                                                    @PathVariable(required = false) String sid) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      if(sid != null) {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE owner=? AND sid=?");
        Helper.stmt.setString(1, uid);
        Helper.stmt.setString(2, sid);
      } else {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM surveys WHERE owner=?");
        Helper.stmt.setString(1, uid);
      }
      
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }

      ResultSet results = Helper.stmt.executeQuery();
      List<Survey> surveys = new ArrayList<>();
      while(results.next()) {
        Survey survey = Survey.builder().setId(results.getString("sid"))
                                        .setName(results.getString("survey_name"))
                                        .setOwner(results.getString("owner"))
                                        .setQuestions(Helper.getQuestions(results.getString("sid")))
                                        .build();
        surveys.add(survey);
      }
      return new ResponseEntity<>(surveys, HttpStatus.OK);
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

  //Retrieves all or one of your responses
  @GetMapping(path={"/{uid}/responses", "/{uid}/responses/{rid}"})
  public @ResponseBody ResponseEntity<?> GetResponses(@PathVariable String uid,
                                                      @PathVariable(required = false) String rid) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      if(rid != null) {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM responses WHERE owner=? AND rid=?");
        Helper.stmt.setString(1, uid);
        Helper.stmt.setString(2, rid);
      } else {
        Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM responses WHERE owner=?");
        Helper.stmt.setString(1, uid);
      }
      if(!uid.equals(httpSessionFactory.getObject().getAttribute("username"))) {
        return new ResponseEntity<>("User is unauthorized, please sign in", HttpStatus.UNAUTHORIZED);
      }
      ResultSet results = Helper.stmt.executeQuery();
      List<Response> responses = new ArrayList<Response>();
      while(results.next()) {
        responses.add(Helper.getResponses(results.getString("rid"),
                      Helper.getSurveyName(results.getString("survey")), 
                      results.getString("survey")));
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

  //Removes a response by response id
  @DeleteMapping(path={"/{uid}/responses/{rid}"})
  public @ResponseBody ResponseEntity<?> RemoveResponse(@PathVariable String uid, @PathVariable String rid) {
    try {
      Helper.conn = DriverManager.getConnection(Helper.connectionString);
      Helper.stmt = Helper.conn.prepareStatement("SELECT * FROM responses WHERE owner=? AND rid=?");

      if(!httpSessionFactory.getObject().getAttribute("username").equals(uid)) {
        return new ResponseEntity<>("User is not logged in or not permitted to make changes", HttpStatus.UNAUTHORIZED);
      }

      Helper.stmt.setString(1, uid);
      Helper.stmt.setString(2, rid);
      ResultSet result = Helper.stmt.executeQuery();
      if(result.next()){
        Helper.stmt = Helper.conn.prepareStatement("DELETE FROM responses WHERE owner=? AND rid=?");
        Helper.stmt.setString(1, uid);
        Helper.stmt.setString(2, rid);
        Helper.stmt.executeUpdate();
        return new ResponseEntity<>(Helper.getResponses(rid, Helper.getSurveyName(result.getString("survey")),
                                    result.getString("survey")), HttpStatus.OK);  
      } else {
        return new ResponseEntity<>("Response not found", HttpStatus.NOT_FOUND);
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
}