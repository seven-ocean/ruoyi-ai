package org.ruoyi.aihuman.service.impl;

import org.ruoyi.common.core.utils.MapstructUtils;
    import org.ruoyi.core.page.TableDataInfo;
    import org.ruoyi.core.page.PageQuery;
    import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ruoyi.aihuman.domain.bo.AihumanActionPresetBo;
import org.ruoyi.aihuman.domain.vo.AihumanActionPresetVo;
import org.ruoyi.aihuman.domain.AihumanActionPreset;
import org.ruoyi.aihuman.mapper.AihumanActionPresetMapper;
import org.ruoyi.aihuman.service.AihumanActionPresetService;
import org.ruoyi.common.core.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 关键词管理Service业务层处理
 *
 * @author ageerle
 * @date Wed Dec 03 11:01:03 GMT+08:00 2025
 */
@RequiredArgsConstructor
@Service
public class AihumanActionPresetServiceImpl implements AihumanActionPresetService {

    private final AihumanActionPresetMapper baseMapper;

    /**
     * 查询关键词管理
     */
    @Override
    public AihumanActionPresetVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

        /**
         * 查询关键词管理列表
         */
        @Override
        public TableDataInfo<AihumanActionPresetVo> queryPageList(AihumanActionPresetBo bo, PageQuery pageQuery) {
            LambdaQueryWrapper<AihumanActionPreset> lqw = buildQueryWrapper(bo);
            Page<AihumanActionPresetVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
            return TableDataInfo.build(result);
        }

    /**
     * 查询关键词管理列表
     */
    @Override
    public List<AihumanActionPresetVo> queryList(AihumanActionPresetBo bo) {
        LambdaQueryWrapper<AihumanActionPreset> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AihumanActionPreset> buildQueryWrapper(AihumanActionPresetBo bo) {
        LambdaQueryWrapper<AihumanActionPreset> lqw = Wrappers.lambdaQuery();
                    lqw.eq(StringUtils.isNotBlank(bo.getActionCode()), AihumanActionPreset::getActionCode, bo.getActionCode());
                    lqw.like(StringUtils.isNotBlank(bo.getName()), AihumanActionPreset::getName, bo.getName());
                    lqw.eq(StringUtils.isNotBlank(bo.getDescription()), AihumanActionPreset::getDescription, bo.getDescription());
                    lqw.eq(StringUtils.isNotBlank(bo.getParamsSchema()), AihumanActionPreset::getParamsSchema, bo.getParamsSchema());
                    lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AihumanActionPreset::getStatus, bo.getStatus());
                    lqw.eq(StringUtils.isNotBlank(bo.getRemark()), AihumanActionPreset::getRemark, bo.getRemark());
                    lqw.eq(bo.getCreateTime() != null, AihumanActionPreset::getCreateTime, bo.getCreateTime());
                    lqw.eq(bo.getUpdateTime() != null, AihumanActionPreset::getUpdateTime, bo.getUpdateTime());
        return lqw;
    }

    /**
     * 新增关键词管理
     */
    @Override
    public Boolean insertByBo(AihumanActionPresetBo bo) {
        AihumanActionPreset add = MapstructUtils.convert(bo, AihumanActionPreset. class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改关键词管理
     */
    @Override
    public Boolean updateByBo(AihumanActionPresetBo bo) {
        AihumanActionPreset update = MapstructUtils.convert(bo, AihumanActionPreset. class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AihumanActionPreset entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除关键词管理
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
