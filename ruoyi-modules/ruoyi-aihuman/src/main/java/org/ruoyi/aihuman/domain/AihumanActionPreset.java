package org.ruoyi.aihuman.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 关键词管理对象 aihuman_action_preset
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */
@Data
@TableName("aihuman_action_preset")
public class AihumanActionPreset implements Serializable {


    /**
     * 主键ID
     */
        @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 动作编码
     */
    private String actionCode;

    /**
     * 动作名称
     */
    private String name;

    /**
     * 动作说明
     */
    private String description;

    /**
     * 参数结构(JSON Schema)
     */
    private String paramsSchema;

    /**
     * 状态
     */
    private String status;

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
