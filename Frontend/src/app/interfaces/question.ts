export interface Question {
  id: String,
  name: String,
  qedit: Boolean,
  type: String,
  tedit: Boolean,
  additionalData: String,
  aedit: Boolean,
  position: Number,  
}

export interface QData {
  question: Question,
  answer: String,
}

export interface InputType {
  value: String,
  name: String
}