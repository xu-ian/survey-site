package surveys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;


public class Helper {

  //Variable that keeps track of the database
  protected static final String connectionString = "jdbc:mysql://localhost:3306/surveysite?user=yourUsername&password=yourPassword";

  //Variable that keeps track of the object mapper
  protected static final ObjectMapper mapper = new ObjectMapper();

  //Variable that keeps track of the main connection
  protected static Connection conn;

  //Variable that keeps track of the main statement
  protected static PreparedStatement stmt;

  //Variable that keeps track of errors
  protected static String error;

  //Helper function to generate random strings for salt and ids
  protected static String generateString(int length) {
    return new Random().ints(48, 123)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(length)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
  }

  //Helper function to log SQLException errors
  protected static void onSQLException(SQLException ex){
    error = ex.getMessage();
    System.out.println("SQLException: " + ex.getMessage());
    System.out.println("SQLState: " + ex.getSQLState());
    System.out.println("VendorError: " + ex.getErrorCode());
    ex.printStackTrace();
  }

  //Helper function that logs a json exception
  protected static void onJSONException(JsonProcessingException ex) {
      error = ex.getMessage();
      System.out.println("JSON Processing Exception: " + ex.getMessage());
    }
    
  //Helper function to hash password with salt
  protected static String hashPassword(String password, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder(2 * hash.length);
      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString() + "." + salt;

    } catch(NoSuchAlgorithmException ex) {
      System.out.println("Error: " + ex.getMessage());
      return "";
    }
  }

  //Helper function to compare passwords
  protected static boolean checkPassword(String password, String hashedPassword) {
    String[] hashSplit = hashedPassword.split("\\.");
    return hashedPassword.equals(hashPassword(password, hashSplit[1]));
  }

  //Helper function that counts the number of rows in a result set
  protected static int countRows(ResultSet result) {
    int rows = 0;
    try {
      while(result.next()) {
        rows += 1;
      }
    } catch (SQLException ex) {
      onSQLException(ex);
    }
    return rows;
  }
  
  //Helper function that gets the name of a survey id
  protected static String getSurveyName(String id) {
    PreparedStatement statement = null;
    ResultSet res = null;
    try {
      statement = conn.prepareStatement("SELECT survey_name FROM surveys WHERE sid=?");
      statement.setString(1, id);
      res = statement.executeQuery();
      res.next();
      return res.getString("survey_name");
    } catch(SQLException ex) {
      onSQLException(ex);
      return "";
    } finally {
      try {
        statement.close();
        res.close();
      } catch(SQLException ex) {
        return null;
      }
    }

  }

  //Helper function to create new responses for a specific question in a given response 
  protected static void createResponseComponent(String rid, String scid, String response) {
    try {
      PreparedStatement statement = conn.prepareStatement("SELECT * FROM response_components WHERE response=? and question=?");
      statement.setString(1, rid);
      statement.setString(2, scid);
      ResultSet result = statement.executeQuery();
      if(result.next()) {
        statement.close();
        statement = conn.prepareStatement("UPDATE response_components SET contents=? WHERE " + 
                                                        "response=? AND question=?");
        statement.setString(1, response);
        statement.setString(2, rid);
        statement.setString(3, scid);
      } else {
        statement.close();
        statement = conn.prepareStatement("INSERT INTO response_components(rcid, response," + 
                                                          "contents, question) VALUES(?,?,?,?)");
        statement.setString(1, generateString(64));
        statement.setString(2, rid);
        statement.setString(3, response);
        statement.setString(4, scid);
      }
      statement.executeUpdate();
      statement.close();
    } catch(SQLException ex) {
      onSQLException(ex);
    }
  }

  //Helper function to get all questions from a survey
  protected static List<Question> getQuestions(String survey_id) {
    List<Question> questions = new ArrayList<>();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement("SELECT * FROM survey_components WHERE survey=?");
      statement.setString(1, survey_id);

      ResultSet results = statement.executeQuery();
      //System.out.println(results.getString(0));
      while(results.next()) {
        Question q = Question.builder().setId(results.getString("scid"))
                                       .setName(results.getString("question"))
                                       .setPosition(results.getInt("position"))
                                       .setType(results.getString("content_type"))
                                       .setAdditionalData(results.getString("additional"))
                                       .build();
        questions.add(q);
      }
      results.close();
      questions.sort(null);
      return questions;
    } catch(SQLException ex) {
      onSQLException(ex);
      return null;
    } finally {
      try {
        statement.close();
      } catch(SQLException ex) {
        return null;
      }
    }
  }

  //Helper function to get all answers for a response
  protected static Response getResponses(String rid, String surveyName, String sid) {
    List<Answer> responses = new ArrayList<Answer>();
    List<Question> questions = getQuestions(sid);
    PreparedStatement statement = null;
    try {

      statement = conn.prepareStatement("SELECT * FROM response_components WHERE response=?");
      statement.setString(1, rid);

      ResultSet results = statement.executeQuery();

      while(results.next()) {
        String qid = results.getString("question");
        Answer q = Answer.builder().setId(results.getString("rcid"))
                                       .setQuestion(questions.stream()
                                                             .filter(x -> x.getId().equals(qid))
                                                             .collect(Collectors.toList()).get(0))
                                       .setResponse(results.getString("contents"))
                                       .build();
        responses.add(q);
      }

      results.close();
      Response response = Response.builder().setId(rid)
                                            .setSurveyName(surveyName)
                                            .setSid(sid)
                                            .setAnswers(responses)
                                            .build();
      return response;
    } catch(SQLException ex) {
      onSQLException(ex);
      return null;
    } finally {
      try {
        statement.close();
      } catch(SQLException ex) {
        return null;
      }
    }

  }

}
