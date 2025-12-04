package org.ruoyi.aihuman.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 关键词管理子表对象 aihuman_keyword
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
@Data
@TableName("aihuman_keyword")
public class AihumanKeyword implements Serializable {


    /**
     * 主键ID
     */
        @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 关键词文本
     */
    private String keyword;

    /**
     * 关键词类型:1唤醒,2停止,3动作触发,4对话
     */
    private String type;

    /**
     * 匹配模式:0精确,1包含,2正则,3前缀,4后缀
     */
    private String matchMode;

    /**
     * 优先级(越大越优先)
     */
    private Integer priority;

    /**
     * 状态
     */
    private String status;

    /**
     * 发布状态
     */
    private String publish;

    /**
     * 关联配置ID(aihuman_config.id)
     */
    private String configId;

    /**
     * 关联实时配置ID(aihuman_real_config.id)
     */
    private String realConfigId;

    /**
     * 关联动作预设ID(aihuman_action_preset.id)
     */
    private String actionId;

    /**
     * 动作编码(回退/外部系统对齐)
     */
    private String actionCode;

    /**
     * 动作参数(JSON)
     */
    private String actionParams;

    /**
     * 知识库来源标识
     */
    private String kbSource;

    /**
     * 知识库ID
     */
    private String kbId;

    /**
     * 知识库查询模板
     */
    private String kbQueryTemplate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建部门ID
     */
    private String createDept;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志:0正常,1删除
     */
        @TableLogic
    private Boolean delFlag;


}
