CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `name` varchar(100) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `dept_id` bigint(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(255) DEFAULT NULL COMMENT '状态 0:禁用，1:正常',
  `user_id_create` bigint(255) DEFAULT NULL COMMENT '创建用户id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `sex` bigint(32) DEFAULT NULL COMMENT '性别',
  `birth` datetime DEFAULT NULL COMMENT '出身日期',
  `pic_id` bigint(32) DEFAULT NULL,
  `live_address` varchar(500) DEFAULT NULL COMMENT '现居住地',
  `hobby` varchar(255) DEFAULT NULL COMMENT '爱好',
  `province` varchar(255) DEFAULT NULL COMMENT '省份',
  `city` varchar(255) DEFAULT NULL COMMENT '所在城市',
  `district` varchar(255) DEFAULT NULL COMMENT '所在地区',
  `Alipay` varchar(30) DEFAULT NULL COMMENT '支付宝账号',
  `Wechat` varchar(30) DEFAULT NULL COMMENT '微信账号',
  `inviteCode` varchar(20) DEFAULT NULL COMMENT '邀请码',
  `parentId` int(30) DEFAULT NULL COMMENT '邀请人id',
  `BillType` int(5) DEFAULT NULL COMMENT '计费方式 1.按天,2.按任务量',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户信息';


CREATE TABLE `Use_Account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Uid` int(11) NOT NULL COMMENT '用户id',
  `TotalGainMoney` decimal(18,2) DEFAULT 0 COMMENT '累计赚取佣金',
  `TotalWithdrawMoney` decimal(18,2) DEFAULT 0 COMMENT '累计提现金额',
  `ApplyWithdrawMoney` decimal(18,2) DEFAULT 0 COMMENT '申请提现金额合计（申请未到账的金额合计）',
  `UseMoney` decimal(18,2) DEFAULT 0 COMMENT '可用金额',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户账户信息';


CREATE TABLE `Use_AccountDetail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Aid` int(11) NOT NULL COMMENT '账户id',
  `OperateType` int(5) NOT NULL COMMENT '操作类型,1.结算,3.提现',
  `IsIncome` int(5) NOT NULL COMMENT '收支类型,1.收入，2.支出',
  `FrontAccount` decimal(18,2) NOT NULL COMMENT '交易前余额',
  `DealMoney` decimal(18,2) NOT NULL COMMENT '交易金额',
  `BackAccount` decimal(18,2) NOT NULL COMMENT '交易后余额',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资金账户明细';


CREATE TABLE `use_wechat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` bigint(11) DEFAULT NULL COMMENT '用户id',
  `Wechat` varchar(30) DEFAULT NULL COMMENT '微信号',
  `Password` varchar(20) DEFAULT NULL COMMENT '微信密码',
  `Data62` varchar(100) DEFAULT NULL COMMENT '62数据',
  `LastDate` timestamp NULL DEFAULT NULL COMMENT '最近一次做任务时间',
  `TotalTaskQuantity` int(11) DEFAULT '0' COMMENT '累计任务次数',
  `TodayTaskQuantity` int(11) DEFAULT '0' COMMENT '今日任务次数',
  `Stauts` int(5) NOT NULL COMMENT '状态 1.启用,2.停用,3.占用',
  `Remark` varchar(200) DEFAULT NULL COMMENT '微信状态备注',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `Taskid` int(11) DEFAULT NULL COMMENT '任务id',
`randomid` varchar(200) DEFAULT NULL COMMENT '服务id',
  `sessionkey` varchar(2000) DEFAULT NULL COMMENT 'loginedUser',
`deviceid` varchar(50) DEFAULT NULL COMMENT 'loginedUser.deviceid',
`maxsynckey` varchar(2000) DEFAULT NULL COMMENT 'loginedUser.maxsynckey',
`uin` varchar(1000) DEFAULT NULL COMMENT 'loginedUser.uin',
`AutoAuthKey` varchar(2000) DEFAULT NULL COMMENT 'loginedUser.AutoAuthKey',
`Cookies` varchar(2000) DEFAULT NULL COMMENT 'loginedUser.Cookies',
`CurrentsyncKey` varchar(2000) DEFAULT NULL COMMENT 'loginedUser.CurrentsyncKey',
`DeviceName` varchar(50) DEFAULT NULL COMMENT 'loginedUser.DeviceName',
`DeviceType` varchar(50) DEFAULT NULL COMMENT 'loginedUser.DeviceType',
`NickName` varchar(50) DEFAULT NULL COMMENT 'loginedUser.NickName',
`UserName` varchar(50) DEFAULT NULL COMMENT 'loginedUser.UserName',
`UserExt` varchar(2000) DEFAULT NULL COMMENT 'loginedUser.UserExt',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20003 DEFAULT CHARSET=utf8 COMMENT='微信号信息';


CREATE TABLE `Wx_taskInfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(100) NOT NULL COMMENT '链接地址',
  `WxName` varchar(30) DEFAULT NULL COMMENT '公众号名称',
  `WxId` varchar(30) DEFAULT NULL COMMENT '公众号id',
  `uin` varchar(30) DEFAULT NULL COMMENT 'uin,暂不知道什么用处',
  `key` varchar(100) DEFAULT NULL COMMENT 'key,暂不知道什么用处',
  `TaskType` int(5) NOT NULL COMMENT '任务类型,1.阅读，2.点赞，3.关注',
  `Price` decimal(18,2) NOT NULL COMMENT '任务单价',
  `Num` int(11) NOT NULL COMMENT '操作数量',
  `TotalMoney` decimal(18,2) NOT NULL COMMENT '任务总金额',
  `taskPeriod` int(11) DEFAULT 0 COMMENT '任务周期 单位(分钟)',
   `Stauts` int(5) NOT NULL COMMENT '状态 1.未开始，3.未完成，5已完成',
   `FinishNum` int(11) DEFAULT 0 COMMENT '已完成数量',
   `sort` int(5) DEFAULT 0 COMMENT '优先级别',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='微信任务信息';



CREATE TABLE `Wx_taskDetail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Taskid` int(11) DEFAULT NULL COMMENT '任务id',
  `Uid` int(11) NOT NULL COMMENT '用户id',
  `parentId` int(11) DEFAULT NULL COMMENT '父级用户id(邀请人id)',
  `WxId` int(11) NOT NULL COMMENT '微信id',
  `Price` decimal(18,2) NOT NULL COMMENT '任务单价',
  `TaskType` int(5) NOT NULL COMMENT '任务类型,1.阅读，2.点赞，3.关注',
  `Stauts` int(5) NOT NULL COMMENT '状态 1.成功,2.失败',
  `Remark` varchar(11) DEFAULT NULL COMMENT '失败备注',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='任务执行明细信息';


CREATE TABLE `Use_applyWithdrawInfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Uid` int(11) NOT NULL COMMENT '用户id',
   `Phone` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `ApplyMoney` decimal(18,2) NOT NULL COMMENT '申请提现金额',
  `PayType` varchar(11) NOT NULL COMMENT '支付方式 1.支付宝，2.微信',
  `Account` varchar(11) NOT NULL COMMENT '收款账号',
  `Stauts` int(5) NOT NULL COMMENT '状态 1.未支付,2.已支付,3.审核失败',
  `Remark` varchar(11) DEFAULT NULL COMMENT '备注',
  `Createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Modifydate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='提现申请';


CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `name` varchar(100) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `dept_id` bigint(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(255) DEFAULT NULL COMMENT '状态 0:禁用，1:正常',
  `user_id_create` bigint(255) DEFAULT NULL COMMENT '创建用户id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `sex` bigint(32) DEFAULT NULL COMMENT '性别',
  `birth` datetime DEFAULT NULL COMMENT '出身日期',
  `pic_id` bigint(32) DEFAULT NULL,
  `live_address` varchar(500) DEFAULT NULL COMMENT '现居住地',
  `hobby` varchar(255) DEFAULT NULL COMMENT '爱好',
  `province` varchar(255) DEFAULT NULL COMMENT '省份',
  `city` varchar(255) DEFAULT NULL COMMENT '所在城市',
  `district` varchar(255) DEFAULT NULL COMMENT '所在地区',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8;



alter table sys_user add `Alipay` varchar(30) DEFAULT NULL COMMENT '支付宝账号';
alter table sys_user add  `Wechat` varchar(30) DEFAULT NULL COMMENT '微信账号';
alter table sys_user add  `inviteCode` varchar(20) DEFAULT NULL COMMENT '邀请码';
alter table sys_user add  `parentId` int(30) DEFAULT NULL COMMENT '邀请人id';
alter table sys_user add  `Stauts` int(5) DEFAULT NULL COMMENT '状态 1.启用，2.停用';
alter table sys_user add  `BillType` int(5) DEFAULT NULL COMMENT '计费方式 1.按天,2.按任务量';

alter TABLE use_wechat add COLUMN parentId int(11) DEFAULT NULL COMMENT '父级用户id(邀请人id)';












