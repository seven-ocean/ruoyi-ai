package org.ruoyi.aihuman.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.ruoyi.common.idempotent.annotation.RepeatSubmit;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.aihuman.domain.vo.AihumanKeywordVo;
import org.ruoyi.aihuman.domain.bo.AihumanKeywordBo;
import org.ruoyi.aihuman.service.AihumanKeywordService;
import org.ruoyi.core.page.TableDataInfo;

/**
 * 关键词管理子表
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/aihuman/aihumanKeyword")
public class AihumanKeywordController extends BaseController {

    private final AihumanKeywordService aihumanKeywordService;

/**
 * 查询关键词管理子表列表
 */
@SaCheckPermission("aihuman:aihumanKeyword:list")
@GetMapping("/list")
    public TableDataInfo<AihumanKeywordVo> list(AihumanKeywordBo bo, PageQuery pageQuery) {
        return aihumanKeywordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出关键词管理子表列表
     */
    @SaCheckPermission("aihuman:aihumanKeyword:export")
    @Log(title = "关键词管理子表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AihumanKeywordBo bo, HttpServletResponse response) {
        List<AihumanKeywordVo> list = aihumanKeywordService.queryList(bo);
        ExcelUtil.exportExcel(list, "关键词管理子表", AihumanKeywordVo.class, response);
    }

    @SaCheckPermission("aihuman:aihumanKeyword:list")
    @GetMapping("/listByAction/{actionId}")
    public R<List<AihumanKeywordVo>> listByAction(@PathVariable String actionId) {
        AihumanKeywordBo bo = new AihumanKeywordBo();
        bo.setActionId(actionId);
        return R.ok(aihumanKeywordService.queryList(bo));
    }

    @SaCheckPermission("aihuman:aihumanKeyword:list")
    @GetMapping("/listByActionCode/{actionCode}")
    public R<List<AihumanKeywordVo>> listByActionCode(@PathVariable String actionCode) {
        AihumanKeywordBo bo = new AihumanKeywordBo();
        bo.setActionCode(actionCode);
        return R.ok(aihumanKeywordService.queryList(bo));
    }

    /**
     * 获取关键词管理子表详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("aihuman:aihumanKeyword:query")
    @GetMapping("/{id}")
    public R<AihumanKeywordVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aihumanKeywordService.queryById(id));
    }

    /**
     * 新增关键词管理子表
     */
    @SaCheckPermission("aihuman:aihumanKeyword:add")
    @Log(title = "关键词管理子表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AihumanKeywordBo bo) {
        return toAjax(aihumanKeywordService.insertByBo(bo));
    }

    /**
     * 修改关键词管理子表
     */
    @SaCheckPermission("aihuman:aihumanKeyword:edit")
    @Log(title = "关键词管理子表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AihumanKeywordBo bo) {
        return toAjax(aihumanKeywordService.updateByBo(bo));
    }

    /**
     * 删除关键词管理子表
     *
     * @param ids 主键串
     */
    @SaCheckPermission("aihuman:aihumanKeyword:remove")
    @Log(title = "关键词管理子表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aihumanKeywordService.deleteWithValidByIds(List.of(ids), true));
    }
}
