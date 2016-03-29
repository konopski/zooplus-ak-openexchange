DROP TABLE IF EXISTS USERROLES;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS ROLES;
CREATE MEMORY TABLE USERS (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(200) UNIQUE NOT NULL,
	PASSWORD VARCHAR(200) UNIQUE NOT NULL,
	EMAIL VARCHAR(200) NOT NULL,
	CREATEDAT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ENABLED BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE MEMORY TABLE ROLES (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(200) UNIQUE NOT NULL,
);
CREATE MEMORY TABLE USERROLES (
	USER_ID BIGINT,
	ROLE_ID BIGINT,
	FOREIGN KEY (USER_ID) REFERENCES USERS (ID),
	FOREIGN KEY (ROLE_ID) REFERENCES ROLES (ID)
);