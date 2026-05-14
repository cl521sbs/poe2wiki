package com.poe2wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poe2wiki.entity.Guide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GuideMapper extends BaseMapper<Guide> {
    @Update("UPDATE guides SET view_count = #{viewCount} WHERE id = #{id}")
    int incrementViewCount(Long id, Long viewCount);
}
