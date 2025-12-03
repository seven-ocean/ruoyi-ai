package org.ruoyi.aihuman.domain.vo;

    import java.time.LocalDateTime;
    import java.io.Serializable;
import org.ruoyi.aihuman.domain.AihumanActionPreset;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
    import org.ruoyi.common.excel.annotation.ExcelDictFormat;
    import org.ruoyi.common.excel.convert.ExcelDictConvert;

    import java.util.Date;


/**
 * 关键词管理视图对象 aihuman_action_preset
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AihumanActionPreset.class)
public class AihumanActionPresetVo implements Serializable {

    private String id;
    /**
     * 动作编码
     */
    @ExcelProperty(value = "动作编码")
    private String actionCode;
    /**
     * 动作名称
     */
    @ExcelProperty(value = "动作名称")
    private String name;
    /**
     * 动作说明
     */
    @ExcelProperty(value = "动作说明")
    private String description;
    /**
     * 参数结构(JSON Schema)
     */
    @ExcelProperty(value = "参数结构(JSON Schema)")
    private String paramsSchema;
    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;
    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
