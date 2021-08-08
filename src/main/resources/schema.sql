DROP TABLE IF EXISTS ARTICLE CASCADE;
DROP TABLE IF EXISTS BOARD CASCADE;
DROP TABLE IF EXISTS CATEGORY CASCADE;
DROP TABLE IF EXISTS COMMENT CASCADE;
DROP TABLE IF EXISTS MEMBER CASCADE;
DROP TABLE IF EXISTS MEMBER_ROLES CASCADE;
DROP TABLE IF EXISTS MEMBER_INFORMATION CASCADE;

CREATE TABLE ARTICLE (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, CONTENT VARCHAR(255) NOT NULL, TITLE VARCHAR(255) NOT NULL, BOARD_ID BIGINT, CATEGORY_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE BOARD (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, DESCRIPTION VARCHAR(255), NAME VARCHAR(255) NOT NULL, MEMBER_INFORMATION_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE CATEGORY (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, NAME VARCHAR(255) NOT NULL, BOARD_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE COMMENT (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, CONTENT VARCHAR(255) NOT NULL, ARTICLE_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE MEMBER (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, ACCOUNT_NON_EXPIRED BOOLEAN NOT NULL, ACCOUNT_NON_LOCKED BOOLEAN NOT NULL, CREDENTIALS_NON_EXPIRED BOOLEAN NOT NULL, ENABLED BOOLEAN NOT NULL, NAME VARCHAR(16) NOT NULL, PASSWORD VARCHAR(255) NOT NULL, USER_ID VARCHAR(16) NOT NULL, MEMBER_INFORMATION_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE MEMBER_ROLES (MEMBER_ID BIGINT NOT NULL, ROLES VARCHAR(255));
CREATE TABLE MEMBER_INFORMATION (ID BIGINT GENERATED BY DEFAULT AS IDENTITY, CREATED_BY VARCHAR(255), CREATED_DATE TIMESTAMP, LAST_MODIFIED_BY VARCHAR(255), LAST_MODIFIED_DATE TIMESTAMP, EMAIL VARCHAR(255), PRIMARY KEY (ID));

ALTER TABLE BOARD ADD CONSTRAINT UK_FIPJUH5HISFPPC2N34GIEJ12X UNIQUE (NAME);
ALTER TABLE CATEGORY ADD CONSTRAINT UK_46CCWNSI9409T36LURVTYLJAK UNIQUE (NAME);
ALTER TABLE MEMBER ADD CONSTRAINT UK_A9BW6SK85YKH4BACJPU0JU5F6 UNIQUE (USER_ID);
ALTER TABLE MEMBER_INFORMATION ADD CONSTRAINT UK_BG6N4PML2V4SKE9KGUQ9EV197 UNIQUE (EMAIL);
ALTER TABLE ARTICLE ADD CONSTRAINT FK2Y7W132XB5XP1AIOUIG87AQJO FOREIGN KEY (BOARD_ID) REFERENCES BOARD;
ALTER TABLE ARTICLE ADD CONSTRAINT FKY5KKOHBK00G0W88FI05K2HCW FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY;
ALTER TABLE BOARD ADD CONSTRAINT FKMQW4NXI8PCJQA7DBQM0XRNIFX FOREIGN KEY (MEMBER_INFORMATION_ID) REFERENCES MEMBER_INFORMATION;
ALTER TABLE CATEGORY ADD CONSTRAINT FK5UEHU4K54F9WM362NIA5KXUAS FOREIGN KEY (BOARD_ID) REFERENCES BOARD;
ALTER TABLE COMMENT ADD CONSTRAINT FK5YX0UPHGJC6IK6HB82KKW501Y FOREIGN KEY (ARTICLE_ID) REFERENCES ARTICLE;
ALTER TABLE MEMBER ADD CONSTRAINT FKIKIX2WYNQ2S6GXNBYIQ0R4NNK FOREIGN KEY (MEMBER_INFORMATION_ID) REFERENCES MEMBER_INFORMATION;
ALTER TABLE MEMBER_ROLES ADD CONSTRAINT FKET63DFLLH4O5QA9QWM7F5KX9X FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER;