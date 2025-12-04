package org.ruoyi.aihuman.domain.vo;

    import java.time.LocalDateTime;
    import java.io.Serializable;
import org.ruoyi.aihuman.domain.AihumanKeyword;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
    import org.ruoyi.common.excel.annotation.ExcelDictFormat;
    import org.ruoyi.common.excel.convert.ExcelDictConvert;

    import java.util.Date;


/**
 * 关键词管理子表视图对象 aihuman_keyword
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AihumanKeyword.class)
public class AihumanKeywordVo implements Serializable {

    private String id;
    /**
     * 关键词文本
     */
    @ExcelProperty(value = "关键词文本")
    private String keyword;
    /**
     * 关键词类型:1唤醒,2停止,3动作触发,4对话
     */
    @ExcelProperty(value = "关键词类型:1唤醒,2停止,3动作触发,4对话", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "aihuman_keyword_type")
    private String type;
    /**
     * 匹配模式:0精确,1包含,2正则,3前缀,4后缀
     */
    @ExcelProperty(value = "匹配模式:0精确,1包含,2正则,3前缀,4后缀", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "aihuman_keyword_match_mode")
    private String matchMode;
    /**
     * 优先级(越大越优先)
     */
    @ExcelProperty(value = "优先级(越大越优先)")
    private Integer priority;
    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    private String status;
    /**
     * 发布状态
     */
    @ExcelProperty(value = "发布状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "aihuman_is_publish")
    private String publish;
    @ExcelProperty(value = "动作预设ID")
    private String actionId;
    @ExcelProperty(value = "动作编码")
    private String actionCode;
    /**
     * 动作参数(JSON)
     */
    @ExcelProperty(value = "动作参数(JSON)")
    private String actionParams;
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
