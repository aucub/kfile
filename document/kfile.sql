create table if not exists file_detail
(
    id                varchar(32)  not null comment '文件id'
        primary key,
    url               varchar(512) not null comment '文件访问地址',
    size              bigint       null comment '文件大小，单位字节',
    filename          varchar(256) null comment '文件名称',
    original_filename varchar(256) null comment '原始文件名',
    base_path         varchar(256) null comment '基础存储路径',
    path              varchar(256) null comment '存储路径',
    ext               varchar(32)  null comment '文件扩展名',
    content_type      varchar(128) null comment 'MIME类型',
    platform          varchar(32)  null comment '存储平台',
    th_url            varchar(512) null comment '缩略图访问路径',
    th_filename       varchar(256) null comment '缩略图名称',
    th_size           bigint       null comment '缩略图大小，单位字节',
    th_content_type   varchar(128) null comment '缩略图MIME类型',
    object_id         varchar(32)  null comment '文件所属对象id',
    object_type       varchar(32)  null comment '文件所属对象类型，例如用户头像，评价图片',
    metadata          text         null comment '文件元数据',
    user_metadata     text         null comment '文件用户元数据',
    th_metadata       text         null comment '缩略图元数据',
    th_user_metadata  text         null comment '缩略图用户元数据',
    attr              text         null comment '附加属性',
    file_acl          varchar(32)  null comment '文件ACL',
    th_file_acl       varchar(32)  null comment '缩略图文件ACL',
    create_time       datetime     null comment '创建时间',
    sha256sum         varchar(255) null,
    createdBy         int          null
)
    comment '文件记录表' charset = utf8;

create table if not exists file_item
(
    id                 varchar(255)                       not null
        primary key,
    url                varchar(255)                       null,
    name               varchar(255)                       not null,
    ext                varchar(255)                       null,
    type               varchar(255)                       not null,
    content_type       varchar(255)                       null,
    directory          varchar(255)                       null,
    created_date       datetime default CURRENT_TIMESTAMP not null,
    last_modified_date datetime default CURRENT_TIMESTAMP not null,
    created_by         int                                null,
    last_modified_by   int                                null,
    share              int                                null,
    description        varchar(255)                       null
);

create table if not exists share
(
    id                 int auto_increment
        primary key,
    file_item_id       varchar(255)                       null,
    acl                varchar(255)                       null,
    acl_list           json                               null,
    password           varchar(255)                       null,
    created_date       datetime default CURRENT_TIMESTAMP not null,
    expire_date        datetime                           null,
    last_modified_date datetime default CURRENT_TIMESTAMP not null,
    created_by         int                                null,
    last_modified_by   int                                null
);

create table if not exists user
(
    id                 int auto_increment
        primary key,
    username           varchar(256)                           not null,
    nickname           varchar(256)                           not null,
    password           varchar(256)                           not null,
    enabled            tinyint(1)   default 1                 not null,
    mail               varchar(256)                           null,
    total_used_storage bigint       default 0                 not null comment '已用存储空间',
    login_ip           varchar(128) default ''                null comment '最后登录IP',
    login_date         datetime     default CURRENT_TIMESTAMP null comment '最后登录时间',
    created_date       datetime     default CURRENT_TIMESTAMP not null,
    last_modified_date datetime     default CURRENT_TIMESTAMP not null,
    constraint mail
        unique (mail),
    constraint username
        unique (username)
);

create table if not exists authority
(
    user_id   int                             not null,
    authority varchar(50) default 'ROLE_USER' not null,
    constraint ix_auth_user_id
        unique (user_id, authority),
    constraint fk_authority_user
        foreign key (user_id) references user (id)
);

