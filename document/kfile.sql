create table if not exists user
(
    id                       int auto_increment
        primary key,
    username                 varchar(256)                           not null,
    nickname                 varchar(256)                           not null,
    password                 varchar(256)                           not null,
    enabled                  tinyint(1)   default 1                 not null,
    mail                     varchar(256)                           null,
    used_up_rate             bigint       default 0                 not null comment '已用上传流量',
    used_down_rate           bigint       default 0                 not null comment '已用下载流量',
    free_up_rate             bigint       default 1073741824        not null comment '可用上传流量',
    free_down_rate           bigint       default 1073741824        not null comment '可用下载流量',
    rate_reset_left_mills    bigint       default 2592000000        not null comment '流量重置时间',
    free_user                tinyint(1)   default 1                 not null comment '是否是免费用户',
    account_expire_left_time bigint       default 0                 not null comment '账号过期时间',
    total_used_storage       bigint       default 0                 not null comment '已用存储空间',
    login_ip                 varchar(128) default ''                null comment '最后登录IP',
    login_date               timestamp    default CURRENT_TIMESTAMP null comment '最后登录时间',
    created_by               int                                    null,
    created_date             timestamp    default CURRENT_TIMESTAMP not null,
    last_modified_by         int                                    null,
    last_modified_date       timestamp    default CURRENT_TIMESTAMP not null,
    constraint mail
        unique (mail)
);

create table if not exists authority
(
    user_id   int         not null,
    authority varchar(50) not null,
    constraint ix_auth_user_id
        unique (user_id, authority),
    constraint fk_authority_user
        foreign key (user_id) references user (id)
);

