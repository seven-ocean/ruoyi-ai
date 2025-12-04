# ruoyi-aihuman

模块概览
- RuoYi 微服务模块，提供数字人语音合成、配置管理、运行控制、火山引擎 TTS 集成与音频静态资源访问。

目录结构
- `controller`：`AihumanConfigController`、`AihumanRealConfigController`、`AihumanInfoController`、`AihumanVolcengineController`
- `service` / `service.impl`：对应业务服务与实现，涵盖配置、实时配置、数字人信息、TTS 集成
- `domain`：实体 `AihumanConfig`、`AihumanRealConfig`、`AihumanInfo`；业务对象 `bo` 与视图对象 `vo`
- `mapper`：MyBatis Plus Mapper（继承 `BaseMapperPlus`），XML 位于 `resources/mapper`
- `config`：`WebConfig` 将 `/voice/**` 映射到 `classpath:/voice/`
- `protocol`：WebSocket TTS 协议支撑（`SpeechWebSocketClient`、`Message`、`EventType`、`MsgType`）
- `volcengine`：双向 TTS 示例 `Bidirection`

核心功能
- 配置管理：常规 CRUD、导出、查询已发布配置
- 实时配置运行控制：按 `run_params` 组装命令启动/停止，跨平台进程与 PID 管理，防重复运行，退出清理
- 数字人信息：常规 CRUD 与分页查询
- 语音合成：通过 WebSocket 客户端逐字符请求与拼接，支持直接返回音频或落地为文件

主要接口
- `/aihuman/aihumanConfig`：`GET /list`、`POST /export`、`GET /{id}`、`POST`、`PUT`、`DELETE /{ids}`、`GET /publishedList`
- `/aihuman/aihumanRealConfig`：`GET /list`、`POST /export`、`GET /{id}`、`POST`、`PUT`、`DELETE /{ids}`、`PUT /run`、`PUT /stop`
- `/aihuman/info`：`GET /list`、`GET /{id}`、`POST`、`PUT`、`DELETE /{ids}`、`GET /test`
- `/aihuman/volcengine`：`POST /generate-voice-direct`（返回 `audio/wav`）、`POST /generate-voice`（生成文件并返回 `/voice/{file}`）

数据持久化与分页
- Mapper 继承 `BaseMapperPlus<Domain, Vo>`，配合 XML 完成 `selectVoPage/VoList/VoById`
- 使用 `PageQuery` 与 `LambdaQueryWrapper` 构造分页与条件（如 `publish`、`runStatus`）
- 通过 `MapstructUtils` 在 `bo`、`entity`、`vo` 间转换

静态资源与访问
- 音频输出优先写入 `src/main/resources/voice`（或项目根 `voice`），经 `WebConfig` 映射为 `/voice/**` 可直接访问
- 运行成功后页面跳转示例：`http://127.0.0.1:8010/webrtcapi-diy.html`

外部依赖
- 自研：`ruoyi-common-core`、`ruoyi-common-doc`、`ruoyi-common-mybatis`、`ruoyi-common-web`、`ruoyi-common-log`、`ruoyi-common-excel`
- 第三方：`velocity-engine-core`、`jna`、`jna-platform`、`Java-WebSocket`


# 接下来的任务
在ruoyi-aihuman继续迭代功能

## 要求
1、要符合这个工程文件的目录结构和代码规范
2、要在这个工程文件的基础上继续迭代，不能完全重新开始
3、表规范需要这个工程文件对MySQL的规范，包括表名、字段名、索引、约束等，每个表需要加上中文注释
4、每张表初始化写入逻辑闭环的默认数据，例如唤醒/停止词、动作触发词、对话关键词等
5、所有表的字符集都需要设置为 utf8mb4、排序规则为 utf8mb4_general_ci

## 新增功能
### 关键词管理
1、唤醒/停止词（通过关键词唤醒数字人交互，或者让数字人处于待机状态）
2、动作触发词（通过关键词触发数字人指定的预设动作）
3、对话关键词（通过关键词触发知识库检索问答）
4、可不断添加新的关键词，每个关键词都有自己的类型（唤醒/停止词、动作触发词、对话关键词）

按照以上要求先提供下MySQL的数据库设计表，包括表名、字段名、索引、约束等，每个表需要加上中文注释。且添加数据初始化逻辑闭环的默认数据，例如唤醒/停止词、动作触发词、对话关键词等。

## MySQL 数据库设计

`aihuman_keyword`
```
CREATE TABLE `aihuman_keyword` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(64) NOT NULL COMMENT '关键词文本',
  `type` TINYINT UNSIGNED NOT NULL COMMENT '关键词类型:1唤醒,2停止,3动作触发,4对话',
  `match_mode` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '匹配模式:0精确,1包含,2正则,3前缀,4后缀',
  `priority` INT NOT NULL DEFAULT 0 COMMENT '优先级(越大越优先)',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态:0正常,1停用',
  `publish` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '发布状态:0未发布,1已发布',
  `config_id` BIGINT UNSIGNED NULL COMMENT '关联配置ID(aihuman_config.id)',
  `real_config_id` BIGINT UNSIGNED NULL COMMENT '关联实时配置ID(aihuman_real_config.id)',
  `action_id` BIGINT UNSIGNED NULL COMMENT '关联动作预设ID(aihuman_action_preset.id)',
  `action_code` VARCHAR(64) NULL COMMENT '动作编码(回退/外部系统对齐)',
  `action_params` JSON NULL COMMENT '动作参数(JSON)',
  `kb_source` VARCHAR(64) NULL COMMENT '知识库来源标识',
  `kb_id` BIGINT UNSIGNED NULL COMMENT '知识库ID',
  `kb_query_template` VARCHAR(255) NULL COMMENT '知识库查询模板',
  `remark` VARCHAR(255) NULL COMMENT '备注',
  `create_dept` BIGINT UNSIGNED NULL COMMENT '创建部门ID',
  `create_by` BIGINT UNSIGNED NULL COMMENT '创建人ID',
  `create_time` DATETIME NULL COMMENT '创建时间',
  `update_by` BIGINT UNSIGNED NULL COMMENT '更新人ID',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0正常,1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_type_real` (`keyword`,`type`,`real_config_id`),
  UNIQUE KEY `uk_keyword_type_config` (`keyword`,`type`,`config_id`),
  KEY `idx_keyword_type` (`keyword`,`type`),
  KEY `idx_status` (`status`),
  KEY `idx_publish` (`publish`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_real_config_id` (`real_config_id`),
  KEY `idx_action_id` (`action_id`),
  KEY `idx_kb_id` (`kb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人关键词管理';
```

`aihuman_action_preset`
```
CREATE TABLE `aihuman_action_preset` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `action_code` VARCHAR(64) NOT NULL COMMENT '动作编码(唯一)',
  `name` VARCHAR(64) NOT NULL COMMENT '动作名称',
  `description` VARCHAR(255) NULL COMMENT '动作说明',
  `params_schema` JSON NULL COMMENT '参数结构(JSON Schema/约定)',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态:0正常,1停用',
  `remark` VARCHAR(255) NULL COMMENT '备注',
  `create_dept` BIGINT UNSIGNED NULL COMMENT '创建部门ID',
  `create_by` BIGINT UNSIGNED NULL COMMENT '创建人ID',
  `create_time` DATETIME NULL COMMENT '创建时间',
  `update_by` BIGINT UNSIGNED NULL COMMENT '更新人ID',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0正常,1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_action_code` (`action_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人动作预设字典';
```

说明与约束
- 表名以 `aihuman_` 前缀、小写下划线命名，符合现有模块规范。
- 关键词类型：1-唤醒，2-停止，3-动作触发，4-对话关键词；匹配模式：0-精确，1-包含，2-正则，3-前缀，4-后缀。
- 作用域：`config_id` 或 `real_config_id` 关联现有配置，均为可选；全局关键词可两者均为空。
- 动作触发：`action_id` 关联 `aihuman_action_preset`，同时保留 `action_code` 以便向前兼容或外部系统对齐。
- 对话关键词：通过 `kb_source`、`kb_id` 与 `kb_query_template` 记录检索来源与模板，不强制外键以兼容外部知识库。
- 逻辑删除：统一 `del_flag`；并保留 `status` 与 `publish` 以适配业务状态与发布流程。

初始化数据（逻辑闭环）
```
BEGIN;

-- 动作预设（幂等插入）
INSERT IGNORE INTO `aihuman_action_preset` (`action_code`,`name`,`description`,`params_schema`,`status`) VALUES
('start_interaction','开始交互','唤醒数字人进入交互态',NULL,1),
('stop_interaction','停止交互','让数字人进入待机/休眠',NULL,1),
('wave_hand','挥手动作','友好挥手致意','{"speed":"medium"}',1),
('nod_head','点头动作','确认/认可的点头','{"times":1}',1),
('search_kb_answer','知识库检索','根据关键词检索知识库',NULL,1);

-- 唤醒词（type=1）
INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '开始',1,0,100,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='start_interaction'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='开始' AND k.type=1 AND k.config_id IS NULL AND k.real_config_id IS NULL);

INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '唤醒',1,0,90,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='start_interaction'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='唤醒' AND k.type=1 AND k.config_id IS NULL AND k.real_config_id IS NULL);

-- 停止词（type=2）
INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '停止',2,0,100,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='stop_interaction'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='停止' AND k.type=2 AND k.config_id IS NULL AND k.real_config_id IS NULL);

INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '休眠',2,0,90,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='stop_interaction'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='休眠' AND k.type=2 AND k.config_id IS NULL AND k.real_config_id IS NULL);

-- 动作触发词（type=3）
INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '挥手',3,0,80,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='wave_hand'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='挥手' AND k.type=3 AND k.config_id IS NULL AND k.real_config_id IS NULL);

INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`action_id`,`action_code`)
SELECT '点头',3,0,80,1,1,p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='nod_head'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='点头' AND k.type=3 AND k.config_id IS NULL AND k.real_config_id IS NULL);

-- 对话关键词（type=4）
INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`kb_source`,`kb_query_template`,`action_id`,`action_code`)
SELECT '问答',4,1,50,1,1,'internal','根据关键词检索知识库: ${query}',p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='search_kb_answer'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='问答' AND k.type=4 AND k.config_id IS NULL AND k.real_config_id IS NULL);

INSERT INTO `aihuman_keyword` (`keyword`,`type`,`match_mode`,`priority`,`status`,`publish`,`kb_source`,`kb_query_template`,`action_id`,`action_code`)
SELECT '知识库',4,1,45,1,1,'internal','KB检索: ${query}',p.id,p.action_code FROM `aihuman_action_preset` p WHERE p.action_code='search_kb_answer'
AND NOT EXISTS (SELECT 1 FROM `aihuman_keyword` k WHERE k.keyword='知识库' AND k.type=4 AND k.config_id IS NULL AND k.real_config_id IS NULL);

COMMIT;
```


### 整理数据库设计
1、aihuman_keyword 和 aihuman_action_preset 有很多字典
2、这些字典数据的解释分别对应 sys_dict_data.sql 和 sys_dict_type.sql 两张表
3、因此需要检查 aihuman_keyword 和 aihuman_action_preset 的字典对应 sys_dict_data.sql 和 sys_dict_type.sql 中的数据是否一致
4、如果存在 aihuman_keyword 和 aihuman_action_preset 有，但是 sys_dict_data.sql 和 sys_dict_type.sql 没有的，需要提供 sys_dict_data 和 sys_dict_type 的 insert 语句 在此文件中
5、最后把关联关系整理在此文件中，例如 aihuman_keyword 和 aihuman_action_preset 中的 type 对应 sys_dict_type.sql 中的 dict_type

关联关系映射
- `aihuman_keyword.type` → `aihuman_keyword_type`
- `aihuman_keyword.match_mode` → `aihuman_keyword_match_mode`
- `aihuman_keyword.status` → `sys_normal_disable`
- `aihuman_keyword.publish` → `aihuman_is_publish`
- `aihuman_action_preset.status` → `sys_normal_disable`

字典文件位置
- 类型定义：`ruoyi-modules/ruoyi-aihuman/src/main/java/org/ruoyi/aihuman/sql/sys_dict_type.sql`
- 数据定义：`ruoyi-modules/ruoyi-aihuman/src/main/java/org/ruoyi/aihuman/sql/sys_dict_data.sql`

缺失字典插入语句（幂等）
```
INSERT INTO `sys_dict_type` (`dict_id`,`tenant_id`,`dict_name`,`dict_type`,`status`,`create_dept`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`)
SELECT UUID_SHORT(),'000000','关键词类型','aihuman_keyword_type','0',NULL,NULL,NOW(),NULL,NOW(),'数字人关键词类型'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_type');

INSERT INTO `sys_dict_type` (`dict_id`,`tenant_id`,`dict_name`,`dict_type`,`status`,`create_dept`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`)
SELECT UUID_SHORT(),'000000','匹配模式','aihuman_keyword_match_mode','0',NULL,NULL,NOW(),NULL,NOW(),'关键词匹配模式定义'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',1,'唤醒','1','aihuman_keyword_type','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'唤醒关键词'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_type' AND `dict_value`='1');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',2,'停止','2','aihuman_keyword_type','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'停止关键词'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_type' AND `dict_value`='2');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',3,'动作触发','3','aihuman_keyword_type','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'动作触发关键词'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_type' AND `dict_value`='3');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',4,'对话关键词','4','aihuman_keyword_type','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'对话检索关键词'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_type' AND `dict_value`='4');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',0,'精确','0','aihuman_keyword_match_mode','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'精确匹配'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode' AND `dict_value`='0');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',1,'包含','1','aihuman_keyword_match_mode','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'包含匹配'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode' AND `dict_value`='1');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',2,'正则','2','aihuman_keyword_match_mode','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'正则匹配'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode' AND `dict_value`='2');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',3,'前缀','3','aihuman_keyword_match_mode','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'前缀匹配'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode' AND `dict_value`='3');

INSERT INTO `sys_dict_data`(`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT UUID_SHORT(),'000000',4,'后缀','4','aihuman_keyword_match_mode','','','N','0',NULL,NULL,NOW(),NULL,NOW(),'后缀匹配'
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `tenant_id`='000000' AND `dict_type`='aihuman_keyword_match_mode' AND `dict_value`='4');
```


# 接下来的任务
在ruoyi-aihuman继续迭代功能

## 要求
1、要符合这个工程文件的目录结构和代码规范
2、要在这个工程文件的基础上继续迭代，不能完全重新开始

## 调整功能
### 关键词管理
1、 aihuman_keyword 是 aihuman_action_preset 的子表，aihuman_keyword 的 action_id，action_code 对应 aihuman_action_preset 的 id，action_code
2、 aihuman_action_preset 和 aihuman_keyword 的逻辑就像 sys_dict_type 和 sys_dict_data 的逻辑
3、 针对上述情况，增加后端代码
4、 完成上述人物后，提供接口 curl 调用方式，追加到这个 AiCode.md 文件末尾

### API 调用示例
- 获取动作预设列表
  - `curl -X GET "http://localhost:6039/aihuman/aihumanActionPreset/list?pageNum=1&pageSize=10" -H "Authorization: Bearer <token>"`
- 新增动作预设
  - `curl -X POST "http://localhost:6039/aihuman/aihumanActionPreset" -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d "{\"actionCode\":\"wave_hand\",\"name\":\"挥手动作\",\"status\":\"0\"}"`
- 根据动作ID获取关键词列表
  - `curl -X GET "http://localhost:6039/aihuman/aihumanKeyword/listByAction/1" -H "Authorization: Bearer <token>"`
- 根据动作编码获取关键词列表
  - `curl -X GET "http://localhost:6039/aihuman/aihumanKeyword/listByActionCode/wave_hand" -H "Authorization: Bearer <token>"`
- 新增关键词并关联动作（通过动作编码自动补全ID）
  - `curl -X POST "http://localhost:6039/aihuman/aihumanKeyword" -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d "{\"keyword\":\"挥手\",\"type\":\"3\",\"matchMode\":\"0\",\"priority\":80,\"status\":\"0\",\"publish\":\"1\",\"actionCode\":\"wave_hand\"}"`
- 删除动作预设（若存在关联关键词将被拒绝）
  - `curl -X DELETE "http://localhost:6039/aihuman/aihumanActionPreset/1" -H "Authorization: Bearer <token>"`




# 接下来的任务
在ruoyi-aihuman继续迭代功能

## 要求
1、要符合这个工程文件的目录结构和代码规范
2、要在这个工程文件的基础上继续迭代，不能完全重新开始

## 调整功能
### AihumanActionPreset 增加 ASR 触发按钮，我会在前端列表的每行数据增加一个按钮，按钮文字为 "ASR 识别"，点进去有一个小窗口，长按按钮就能输入语音，松开按钮语音输入结束，随后会调用 ASR 接口，用于触发 ASR 识别
1、AihumanActionPreset 的 params_schema 有 ASR 接口参数，参数为：
{
  "platform": "aliyun-asr",
  "apiKey": "sk-111d18189c244c22866b76f6c6e9189d",
  "model": "fun-asr-realtime",
  "format": "pcm",
  "sampleRate": 16000,
  "audio": {
    "sampleRate": 16000,
    "sampleSizeInBits": 16,
    "channels": 1,
    "signed": true,
    "bigEndian": false,
    "bufferSize": 1024,
    "durationMs": 300000,
    "sleepMs": 20
  }
}
2、目前只打通了 aliyun asr 接口，后续可以根据需要增加其他 ASR 接口，因此需要 if 判断 params_schema 中的 platform 字段
3、如果 platform 是 aliyun-asr，则调用 aliyun asr 接口
4、如果 platform 是其他值，则返回错误信息 "不支持的 ASR 平台"
5、如果调用 ASR 接口失败，则返回错误信息 "ASR 接口调用失败"
6、如果调用 ASR 接口成功，则返回 ASR 识别结果，返回结果示例：
{
  "code": 200,
  "msg": "success",
  "data": {
    "result": "开始了吗？",    // ASR 识别结果
    "match_keyword": "开始",  // 匹配到的AihumanKeyword的关键词keyword
    "match_action_code": "start_interaction", // 匹配到的AihumanActionPreset的动作编码actionCode
    "match_action_name": "开始交互" // 匹配到的AihumanActionPreset的动作名称name
    "is_match": true // 是否匹配到关键词
  }
}
### 提供curl调用示例，追加到这里

