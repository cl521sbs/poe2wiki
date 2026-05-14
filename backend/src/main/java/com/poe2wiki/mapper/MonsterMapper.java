package com.poe2wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poe2wiki.entity.Monster;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MonsterMapper extends BaseMapper<Monster> {
}
