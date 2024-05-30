import { Question } from "./question"
export interface Response {
  id: String,
  sid: String,
  surveyName: String,
  answers: Answer[],
}

export interface Answer {
  id: String,
  question: Question,
  response: String
}

export interface Responses {
  id: string,
  question: string,
  type: string,
  additional: String,
  answers: String[]
}