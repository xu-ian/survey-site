import axios from 'axios';
import Cookies from 'js-cookie';
import { Question, QData } from '../interfaces/question';

const SERVER = "http://localhost:8080";
const uid = "Username";
//Authentication API Calls

//Signs up a new user
export function signUp(username: string, password: string, next: Function, error: Function) {
  console.log(username);
  console.log(password);
  axios.post(SERVER + "/user/signup", {"username": username, "password": password}).then(response => {
    next(response.data);
  }).catch(err => {
    error(err.response.data);
  });
}

//Logs in a new user
export function logIn(username: String, password: string, next: Function, error: Function) {
  axios.post(SERVER + "/user/login", {"username": username, "password": password}, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    error(err.response.data);
  });
}

//Logs out user
export function logOut(next: Function) {
  axios.post(SERVER + "/user/logout", {}, { withCredentials: true}).then(response => {
    next();
  }).catch(err => {
    console.log(err);
  });
}

//Survey API Calls

//Creates a new survey for the specified user
export function createSurvey(name: String, next: Function) {
  axios.post(SERVER + "/user/"+Cookies.get(uid)+"/survey", {name: name}, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Gets a survey by its' id
export function getSurvey(id: String, next: Function) {
  axios.get(SERVER + "/user/" + Cookies.get(uid) + "/survey/" + id, { withCredentials: true}).then(response => {
    next(response.data[0].questions);
  }).catch(err => {
    console.log(err);
  });
}

//Gets all surveys for the user
export function getAllSurveys(user: String, next: Function) {
  axios.get(SERVER + "/user/"+ Cookies.get(uid) + "/surveys", { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Gets all responses for a survey
export function getResponsesForSurvey(survey: String, next: Function) {
  axios.get(SERVER + "/user/" + Cookies.get(uid) + "/survey/" + survey + "/responses", {withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Removes a survey by id
export function deleteSurvey(id: String, next: Function) {
  axios.delete(SERVER + "/user/" + Cookies.get(uid) + "/survey/" + id, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Question API Calls

//Creates a new question for the specified survey
export function createQuestion(id: String, question: String, content: String, next: Function): void {
  axios.post(SERVER + "/user/"+Cookies.get(uid)+ "/survey/"+id+"/questions", {"question": question, "content_type": content},
             { withCredentials: true, headers: {"Content-Type": "application/json"}}).then(
    response => {
      next(response.data.questions);
    }
  ).catch(err => {
    console.log(err);
  });
}

//Removes a question from the specified survey
export function removeQuestion(id: String, qid: String, next: Function): void {
  axios.delete(SERVER + "/user/"+Cookies.get(uid)+"/survey/" + id + "/questions/" + qid, { withCredentials: true}).then(response => {
    next(response.data.questions);
  }).catch(err => {
    console.log(err);
  })
}

//Updates a question's choice type, question asked, and additional value
export function updateQuestion(id: String, q: Question, next: Function): void {
  axios.patch(SERVER + "/user/"+Cookies.get(uid)+"/survey/" + id + "/questions/" + q.id, 
      {"content_type": q.type, "name":q.name, "additional": q.additionalData}, 
      { withCredentials: true}).then(response => {
    next(response.data.questions);
  }).catch(err => {
    console.log(err);
  });
}

//Swaps two questions from the specified survey
export function swapQuestion(id: String, pos1: Number, pos2: Number, next: Function): void {
  axios.patch(SERVER + "/user/"+Cookies.get(uid)+"/survey/" + id + "/questions?pos1="+pos1+"&pos2="+pos2, {}, { withCredentials: true}).then(response => {
    next(response.data.questions);
  }).catch(err => {
    console.log(err);
  });
}

// Response API Calls

//Creates a new response
export function createResponse(id: String, next: Function, error: Function): void {
  axios.post(SERVER + "/user/"+Cookies.get(uid)+"/survey/" + id + "/responses", {}, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    error(err.message);
  })
}

//Get a response by user and response id
export function getResponse(rid: String, next: Function): void {
  axios.get(SERVER + "/user/"+ Cookies.get("Username") +"/responses/" + rid, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Get all responses by user
export function getResponses(next: Function): void {
  axios.get(SERVER + "/user/"+ Cookies.get("Username") +"/responses", { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Updates a response
export function updateResponse(id: String, rid: String, responses: QData[], next: Function): void {
  let responsemap: {[id: string] : String} = {}; 
  responses.forEach(response => {
    responsemap[response.question.id.toString()] = response.answer;
  });
  axios.patch(SERVER + "/user/"+Cookies.get(uid)+"/survey/" + id + "/responses/" + rid, responsemap, { withCredentials: true}).then(response => {
    next(response.data);
  }).catch(err => {
    console.log(err);
  });
}

//Removes a response
export function removeResponse(rid: String, next: Function): void {
  axios.delete(SERVER + "/user/"+uid+"/responses/" + rid, { withCredentials: true}).then(response => {
    next();
  }).catch(err => {
    console.log(err);
  });
}