package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poe2wiki.entity.Favorite;
import com.poe2wiki.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper mapper;

    public void add(Long userId, Long guideId) {
        LambdaQueryWrapper<Favorite> qw = new LambdaQueryWrapper<>();
        qw.eq(Favorite::getUserId, userId).eq(Favorite::getGuideId, guideId);
        if (mapper.selectCount(qw) == 0) {
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setGuideId(guideId);
            mapper.insert(f);
        }
    }

    public void remove(Long userId, Long guideId) {
        LambdaQueryWrapper<Favorite> qw = new LambdaQueryWrapper<>();
        qw.eq(Favorite::getUserId, userId).eq(Favorite::getGuideId, guideId);
        mapper.delete(qw);
    }

    public List<Favorite> listByUser(Long userId) {
        LambdaQueryWrapper<Favorite> qw = new LambdaQueryWrapper<>();
        qw.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreatedAt);
        return mapper.selectList(qw);
    }
}
