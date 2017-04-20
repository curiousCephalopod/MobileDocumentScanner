CREATE TABLE Login
(
  userID INT(16) NOT NULL AUTO_INCREMENT,
  hash VARCHAR(128) NOT NULL,
  salt VARCHAR(128) NOT NULL,
  username VARCHAR(56) NOT NULL,
  emailAddress (VARCHAR(128) NOT NULL
  PRIMARY KEY(userID)
  );
  
CREATE TABLE Storage
(
  imageID INT(128) NOT NULL,
  image BLOB NOT NULL,
  PRIMARY KEY(imageID)
);

CREATE TABLE ImageAccess
(
  userID INT(16) NOT NULL,
  imageID INT(128) NOT NULL,
  key CHAR(30) NOT NULL,
  PRIMARY KEY(userID, imageID),
  FOREIGN KEY userID REFERENCES Login(userID) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY imageID REFERENCES Storage(imageID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Tag
(
  tagID INT(16) NOT NULL,
  tag VARCHAR(56) NOT NULL,
  PRIMARY KEY(TagID)
);

CREATE TABLE TagApplication
(
  tagID INT(16) NOT NULL,
  imageID INT(128) NOT NULL,
  PRIMARY KEY(tagID, imageID)
);
