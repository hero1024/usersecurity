create database if not exists user_security;
use user_security;

create table if not exists permission
(
    id          int auto_increment comment '主键ID'
        primary key,
    path        varchar(255)             null comment '资源路径',
    role_ids    varchar(255) default '1' null comment '角色ID',
    description varchar(255)             null comment '描述',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

create table if not exists role
(
    id          int     auto_increment      not null comment '主键ID'
        primary key,
    name        varchar(255) null comment '角色名称',
    description varchar(255) null comment '描述',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

create table if not exists user
(
    id       bigint     auto_increment    not null comment '主键ID'
        primary key,
    username varchar(255)  null unique comment '用户名',
    password varchar(255)  null comment '密码',
    nickname varchar(30)   null comment '姓名',
    phone      bigint(11)           null comment '手机号',
    email    varchar(50)   null comment '邮箱',
    status   int default 1 null comment '状态',
    role_id  int default 1 null comment '角色ID',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE if not exists `user_bind_third_login`  (
                                          `id` bigint(0) NOT NULL  AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY ,
                                          `type` varchar(255)  NULL DEFAULT NULL COMMENT '平台类型',
                                          `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户表主键ID',
                                          `open_id` varchar(255)  NULL DEFAULT NULL COMMENT '用户在第三方平台的唯一ID'  UNIQUE,
                                          `nickname` varchar(255)  NULL DEFAULT NULL COMMENT '昵称',
                                          `head_sculpture` varchar(255)  NULL DEFAULT NULL COMMENT '第三方头像',
                                          `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `role` (`id`, `name`, `description`) VALUES (1, 'common', '普通用户');
INSERT INTO `role` (`id`, `name`, `description`) VALUES (2, 'admin', '管理员');
INSERT INTO `role` (`id`, `name`, `description`) VALUES (3, 'admin_super', '超级管理员');

INSERT INTO `permission` (`id`, `path`, `role_ids`, `description`) VALUES (1, '/user/**', '3', '用户管理');
INSERT INTO `permission` (`id`, `path`, `role_ids`, `description`) VALUES (2, '/role/**', '3', '角色管理');
INSERT INTO `permission` (`id`, `path`, `role_ids`, `description`) VALUES (3, '/permission/**', '3', '权限管理');
INSERT INTO `permission` (`id`, `path`, `role_ids`, `description`) VALUES (4, '/smiling/dbsource/**', '2,3', '数据源管理');
INSERT INTO `permission` (`id`, `path`, `role_ids`, `description`) VALUES (5, '/smiling/question/**', '2,3', '问数后管');


INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `status`, `role_id`) VALUES (1, 'common', '$2a$10$mYpn.aSvG4D4h.nLng/tvOTvZEOeJNQh/IGfpRDitCXEen/tb0ebu', '普通用户', NULL, NULL, 1, 1);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `status`, `role_id`) VALUES (2, 'admin', '$2a$10$2iRKjG8aTE4teYyeZJcQ0u3zvv905740VH4dQcvqLTVw5uuNZk4kS', '管理员', NULL, NULL, 1, 2);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `status`, `role_id`) VALUES (3, 'admin_super', '$2a$10$vyBT/d7kyGVwAP2FMxSZ3.wAzQ5Yl7fl0.Wp9N9fSGZM4/rnNb6de', '超级管理员', 123456, NULL, 1, 3);
