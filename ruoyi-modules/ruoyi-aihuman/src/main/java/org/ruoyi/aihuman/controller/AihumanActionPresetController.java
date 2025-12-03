package org.ruoyi.aihuman.controller;

import java.util.List;

import cn.dev33.satoken.annotation.SaIgnore;
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
import org.ruoyi.aihuman.domain.vo.AihumanActionPresetVo;
import org.ruoyi.aihuman.domain.bo.AihumanActionPresetBo;
import org.ruoyi.aihuman.service.AihumanActionPresetService;
import org.ruoyi.core.page.TableDataInfo;

/**
 * 关键词管理
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */

//临时免登录
@SaIgnore

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/aihuman/aihumanActionPreset")
public class AihumanActionPresetController extends BaseController {

    private final AihumanActionPresetService aihumanActionPresetService;

/**
 * 查询关键词管理列表
 */
@SaCheckPermission("aihuman:aihumanActionPreset:list")
@GetMapping("/list")
    public TableDataInfo<AihumanActionPresetVo> list(AihumanActionPresetBo bo, PageQuery pageQuery) {
        return aihumanActionPresetService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出关键词管理列表
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:export")
    @Log(title = "关键词管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AihumanActionPresetBo bo, HttpServletResponse response) {
        List<AihumanActionPresetVo> list = aihumanActionPresetService.queryList(bo);
        ExcelUtil.exportExcel(list, "关键词管理", AihumanActionPresetVo.class, response);
    }

    /**
     * 获取关键词管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:query")
    @GetMapping("/{id}")
    public R<AihumanActionPresetVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aihumanActionPresetService.queryById(id));
    }

    /**
     * 新增关键词管理
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:add")
    @Log(title = "关键词管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AihumanActionPresetBo bo) {
        return toAjax(aihumanActionPresetService.insertByBo(bo));
    }

    /**
     * 修改关键词管理
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:edit")
    @Log(title = "关键词管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AihumanActionPresetBo bo) {
        return toAjax(aihumanActionPresetService.updateByBo(bo));
    }

    /**
     * 删除关键词管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("aihuman:aihumanActionPreset:remove")
    @Log(title = "关键词管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aihumanActionPresetService.deleteWithValidByIds(List.of(ids), true));
    }
}
