package org.ruoyi.aihuman.domain.bo;

import org.ruoyi.aihuman.domain.AihumanActionPreset;
import org.ruoyi.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import java.io.Serializable;
import java.io.Serializable;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;

/**
 * 关键词管理业务对象 aihuman_action_preset
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */
@Data

@AutoMapper(target = AihumanActionPreset.class, reverseConvertGenerate = false)
public class AihumanActionPresetBo implements Serializable {

    private String id;

    /**
     * 动作编码
     */
    @NotBlank(message = "动作编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String actionCode;
    /**
     * 动作名称
     */
    @NotBlank(message = "动作名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
