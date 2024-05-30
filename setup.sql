create database surveysite;
CREATE TABLE surveysite.users (
  `uid`		 VARCHAR(64) NOT NULL,
  `username` VARCHAR(256) NOT NULL,
  `password` VARCHAR(256) NOT NULL,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  PRIMARY KEY (`uid`));
  
CREATE TABLE surveysite.surveys (
  `sid` VARCHAR(64) NOT NULL,
  `survey_name` VARCHAR(256) NOT NULL,
  `owner` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`sid`),
  INDEX `uid_idx` (`owner` ASC) VISIBLE,
  CONSTRAINT `uid`
    FOREIGN KEY (`owner`)
    REFERENCES surveysite.users (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE surveysite.survey_components (
  `scid` VARCHAR(64) NOT NULL,
  `survey` VARCHAR(64) NULL,
  `content_type` VARCHAR(25) NULL,
  `position` INT NULL,
  `question` VARCHAR(1000) NULL,
  PRIMARY KEY (`scid`),
  INDEX `sid_idx` (`survey` ASC) VISIBLE,
  CONSTRAINT `sid`
    FOREIGN KEY (`survey`)
    REFERENCES surveysite.surveys (`sid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
CREATE TABLE surveysite.responses (
  `rid` VARCHAR(64) NOT NULL,
  `owner` VARCHAR(64) NULL,
  `survey` VARCHAR(64) NULL,
  PRIMARY KEY (`rid`),
  INDEX `uid_idx` (`owner` ASC) VISIBLE,
  INDEX `sid_idx` (`survey` ASC) VISIBLE,
  CONSTRAINT `uid2`
    FOREIGN KEY (`owner`)
    REFERENCES surveysite.users (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `sid2`
    FOREIGN KEY (`survey`)
    REFERENCES surveysite.surveys (`sid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
CREATE TABLE surveysite.response_components (
  `rcid` VARCHAR(64) NOT NULL,
  `response` VARCHAR(64) NULL,
  `contents` VARCHAR(1000) NULL,
  `question` VARCHAR(64) NULL,
  `additional` VARCHAR(1000) NULL,
  PRIMARY KEY (`rcid`),
  INDEX `rid_idx` (`response` ASC) VISIBLE,
  INDEX `scid_idx` (`question` ASC) VISIBLE,
  CONSTRAINT `rid`
    FOREIGN KEY (`response`)
    REFERENCES surveysite.responses (`rid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `scid`
    FOREIGN KEY (`question`)
    REFERENCES surveysite.survey_components (`scid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


