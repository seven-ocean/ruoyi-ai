package org.ruoyi.aihuman.service.impl;

import org.ruoyi.common.core.utils.MapstructUtils;
    import org.ruoyi.core.page.TableDataInfo;
    import org.ruoyi.core.page.PageQuery;
    import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ruoyi.aihuman.domain.bo.AihumanKeywordBo;
import org.ruoyi.aihuman.domain.vo.AihumanKeywordVo;
import org.ruoyi.aihuman.domain.AihumanKeyword;
import org.ruoyi.aihuman.mapper.AihumanKeywordMapper;
import org.ruoyi.aihuman.mapper.AihumanActionPresetMapper;
import org.ruoyi.aihuman.domain.AihumanActionPreset;
import org.ruoyi.common.core.exception.ServiceException;
import org.ruoyi.aihuman.service.AihumanKeywordService;
import org.ruoyi.common.core.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 关键词管理子表Service业务层处理
 *
 * @author ageerle
 * @date Wed Dec 03 15:08:04 GMT+08:00 2025
 */
@RequiredArgsConstructor
@Service
public class AihumanKeywordServiceImpl implements AihumanKeywordService {

    private final AihumanKeywordMapper baseMapper;
    private final AihumanActionPresetMapper actionPresetMapper;

    /**
     * 查询关键词管理子表
     */
    @Override
    public AihumanKeywordVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

        /**
         * 查询关键词管理子表列表
         */
        @Override
        public TableDataInfo<AihumanKeywordVo> queryPageList(AihumanKeywordBo bo, PageQuery pageQuery) {
            LambdaQueryWrapper<AihumanKeyword> lqw = buildQueryWrapper(bo);
            Page<AihumanKeywordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
            return TableDataInfo.build(result);
        }

    /**
     * 查询关键词管理子表列表
     */
    @Override
    public List<AihumanKeywordVo> queryList(AihumanKeywordBo bo) {
        LambdaQueryWrapper<AihumanKeyword> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AihumanKeyword> buildQueryWrapper(AihumanKeywordBo bo) {
        LambdaQueryWrapper<AihumanKeyword> lqw = Wrappers.lambdaQuery();
                    lqw.eq(StringUtils.isNotBlank(bo.getId()), AihumanKeyword::getId, bo.getId());
                    lqw.like(StringUtils.isNotBlank(bo.getKeyword()), AihumanKeyword::getKeyword, bo.getKeyword());
                    lqw.eq(StringUtils.isNotBlank(bo.getType()), AihumanKeyword::getType, bo.getType());
                    lqw.eq(StringUtils.isNotBlank(bo.getMatchMode()), AihumanKeyword::getMatchMode, bo.getMatchMode());
                    lqw.eq(bo.getPriority() != null, AihumanKeyword::getPriority, bo.getPriority());
                    lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AihumanKeyword::getStatus, bo.getStatus());
                    lqw.eq(StringUtils.isNotBlank(bo.getPublish()), AihumanKeyword::getPublish, bo.getPublish());
                    lqw.eq(StringUtils.isNotBlank(bo.getActionId()), AihumanKeyword::getActionId, bo.getActionId());
                    lqw.eq(StringUtils.isNotBlank(bo.getActionCode()), AihumanKeyword::getActionCode, bo.getActionCode());
                    lqw.like(StringUtils.isNotBlank(bo.getActionParams()), AihumanKeyword::getActionParams, bo.getActionParams());
                    lqw.like(StringUtils.isNotBlank(bo.getRemark()), AihumanKeyword::getRemark, bo.getRemark());
                    lqw.eq(bo.getCreateTime() != null, AihumanKeyword::getCreateTime, bo.getCreateTime());
                    lqw.eq(bo.getUpdateTime() != null, AihumanKeyword::getUpdateTime, bo.getUpdateTime());
        return lqw;
    }

    /**
     * 新增关键词管理子表
     */
    @Override
    public Boolean insertByBo(AihumanKeywordBo bo) {
        AihumanKeyword add = MapstructUtils.convert(bo, AihumanKeyword. class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改关键词管理子表
     */
    @Override
    public Boolean updateByBo(AihumanKeywordBo bo) {
        AihumanKeyword update = MapstructUtils.convert(bo, AihumanKeyword. class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AihumanKeyword entity) {
        if (StringUtils.isBlank(entity.getActionId()) && StringUtils.isNotBlank(entity.getActionCode())) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AihumanActionPreset> q1 = com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery();
            q1.eq(AihumanActionPreset::getActionCode, entity.getActionCode()).last("limit 1");
            AihumanActionPreset preset = actionPresetMapper.selectOne(q1);
            if (preset == null) throw new ServiceException("动作编码不存在");
            entity.setActionId(preset.getId());
        } else if (StringUtils.isNotBlank(entity.getActionId()) && StringUtils.isBlank(entity.getActionCode())) {
            AihumanActionPreset preset = actionPresetMapper.selectById(entity.getActionId());
            if (preset == null) throw new ServiceException("动作ID不存在");
            entity.setActionCode(preset.getActionCode());
        } else if (StringUtils.isNotBlank(entity.getActionId()) && StringUtils.isNotBlank(entity.getActionCode())) {
            AihumanActionPreset preset = actionPresetMapper.selectById(entity.getActionId());
            if (preset == null || !entity.getActionCode().equals(preset.getActionCode())) {
                throw new ServiceException("动作ID与编码不一致");
            }
        }
    }

    /**
     * 批量删除关键词管理子表
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
