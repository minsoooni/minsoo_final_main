show user;


create table spring_test
(no         number
,name       varchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_TEST이(가) 생성되었습니다.
commit;

select * from spring_test;


create table tbl_main_image_product
(imgno           number not null
,productname     varchar2(40) not null
,imgfilename     varchar2(100) not null
,constraint PK_tbl_prouct_image_imgno primary key(imgno)
);
-- Table TBL_MAIN_IMAGE_PRODUCT이(가) 생성되었습니다.

create sequence seq_prouct_image
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_PROUCT_IMAGE이(가) 생성되었습니다.

insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_prouct_image.nextval, '미샤', '미샤.png');  
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_prouct_image.nextval, '원더플레이스', '원더플레이스.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_prouct_image.nextval, '레노보', '레노보.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_prouct_image.nextval, '동원', '동원.png'); 

commit;

select imgno, productname, imgfilename
from tbl_main_image_product
order by imgno asc;