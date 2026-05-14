package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;

    public PageResult<Skill> list(int page, int size, String type, String damageType, String keyword) {
        LambdaQueryWrapper<Skill> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            qw.eq(Skill::getType, type);
        }
        if (StringUtils.hasText(damageType)) {
            qw.eq(Skill::getDamageType, damageType);
        }
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Skill::getNameCn, keyword)
                         .or().like(Skill::getNameEn, keyword));
        }
        qw.orderByAsc(Skill::getId);
        return PageResult.from(skillMapper.selectPage(Page.of(page, size), qw));
    }

    public Skill getById(Long id) {
        return skillMapper.selectById(id);
    }
}
