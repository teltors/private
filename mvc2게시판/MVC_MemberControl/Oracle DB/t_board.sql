create table t_Board(
	articleNO number(10) primary key,
	parentNO number(10) default 0,
	title  varchar2(500) not null,
	content varchar2(4000),
	imageFileName varchar2(30),
	writedate date default sysdate not null,
	id varchar2(10),
	CONSTRAINT FK_ID FOREIGN KEY(id)
	REFERENCES t_member(id)
);