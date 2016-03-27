DROP TABLE IF EXISTS USERS;
CREATE TABLE USERS  (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT,
	EMAIL VARCHAR(200) NOT NULL,
	PASSWORD VARCHAR(200) NOT NULL,
    CREATEDAT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ENABLED BOOLEAN NOT NULL DEFAULT TRUE
	UNIQUE KEY (EMAIL),
	UNIQUE KEY (PASSWORD)
);