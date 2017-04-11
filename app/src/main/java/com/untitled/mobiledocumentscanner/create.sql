CREATE TABLE login
(
    userID CHAR(50),
    hash CHAR(256),
    salt CHAR(256),
    userKey CHAR(256),
    adminKey CHAR(256),
    PRIMARY KEY(userID)
);

CREATE TABLE userID
(
    userID CHAR(50),
    username VARCHAR(50),
    email VARCHAR(100),
    PRIMARY KEY(userID)
)

CREATE TABLE storage
(
    imageID CHAR(50),
    image BLOB,
    imagePrev BLOB,
    userID CHAR(50),
    PRIMARY KEY(imageID),
    FOREIGN KEY(userID) REFERENCES login(userID)
)

CREATE TABLE tags
(
    tagID CHAR(50),
    tagText VARCHAR(64),
    PRIMARY KEY(tagID),
)
CREATE TABLE tagAssignment
(
    tagID CHAR(50),
    imageID CHAR(50),
    PRIMARY KEY(tagID, imageID),
    FOREIGN KEY(tagID) REFERENCES tags(tagID) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY(imageID) REFERENCES storage(imageID) ON DELETE SET NULL ON UPDATE CASCADE
)