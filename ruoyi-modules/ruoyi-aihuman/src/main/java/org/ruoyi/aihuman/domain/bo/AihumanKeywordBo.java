package org.ruoyi.aihuman.domain.bo;

import org.ruoyi.aihuman.domain.AihumanKeyword;
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
 * 关键词管理子表业务对象 aihuman_keyword
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
@Data

@AutoMapper(target = AihumanKeyword.class, reverseConvertGenerate = false)
public class AihumanKeywordBo implements Serializable {

    private String id;

    /**
     * 关键词文本
     */
    @NotBlank(message = "关键词文本不能为空", groups = { AddGroup.class, EditGroup.class })
    private String keyword;
    /**
     * 关键词类型:1唤醒,2停止,3动作触发,4对话
     */
    @NotBlank(message = "关键词类型:1唤醒,2停止,3动作触发,4对话不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;
    /**
     * 匹配模式:0精确,1包含,2正则,3前缀,4后缀
     */
    @NotBlank(message = "匹配模式:0精确,1包含,2正则,3前缀,4后缀不能为空", groups = { AddGroup.class, EditGroup.class })
    private String matchMode;
    /**
     * 优先级(越大越优先)
     */
    @NotNull(message = "优先级(越大越优先)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer priority;
    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;
    /**
     * 发布状态
     */
    @NotBlank(message = "发布状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String publish;
    private String actionId;
    private String actionCode;
    /**
     * 动作参数(JSON)
     */
    private String actionParams;
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
