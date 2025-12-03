package org.ruoyi.aihuman.service;

import org.ruoyi.aihuman.domain.vo.AihumanActionPresetVo;
import org.ruoyi.aihuman.domain.bo.AihumanActionPresetBo;
    import org.ruoyi.core.page.TableDataInfo;
    import org.ruoyi.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 关键词管理Service接口
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */
public interface AihumanActionPresetService {

    /**
     * 查询关键词管理
     */
        AihumanActionPresetVo queryById(String id);

        /**
         * 查询关键词管理列表
         */
        TableDataInfo<AihumanActionPresetVo> queryPageList(AihumanActionPresetBo bo, PageQuery pageQuery);

    /**
     * 查询关键词管理列表
     */
    List<AihumanActionPresetVo> queryList(AihumanActionPresetBo bo);

    /**
     * 新增关键词管理
     */
    Boolean insertByBo(AihumanActionPresetBo bo);

    /**
     * 修改关键词管理
     */
    Boolean updateByBo(AihumanActionPresetBo bo);

    /**
     * 校验并批量删除关键词管理信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
