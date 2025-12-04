package org.ruoyi.aihuman.service;

import org.ruoyi.aihuman.domain.vo.AihumanKeywordVo;
import org.ruoyi.aihuman.domain.bo.AihumanKeywordBo;
    import org.ruoyi.core.page.TableDataInfo;
    import org.ruoyi.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 关键词管理子表Service接口
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
public interface AihumanKeywordService {

    /**
     * 查询关键词管理子表
     */
        AihumanKeywordVo queryById(String id);

        /**
         * 查询关键词管理子表列表
         */
        TableDataInfo<AihumanKeywordVo> queryPageList(AihumanKeywordBo bo, PageQuery pageQuery);

    /**
     * 查询关键词管理子表列表
     */
    List<AihumanKeywordVo> queryList(AihumanKeywordBo bo);

    /**
     * 新增关键词管理子表
     */
    Boolean insertByBo(AihumanKeywordBo bo);

    /**
     * 修改关键词管理子表
     */
    Boolean updateByBo(AihumanKeywordBo bo);

    /**
     * 校验并批量删除关键词管理子表信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
