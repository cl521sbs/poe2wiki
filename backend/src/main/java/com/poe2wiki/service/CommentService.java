package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poe2wiki.entity.Comment;
import com.poe2wiki.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper mapper;

    public List<Comment> listByGuideId(Long guideId) {
        LambdaQueryWrapper<Comment> qw = new LambdaQueryWrapper<>();
        qw.eq(Comment::getGuideId, guideId).orderByAsc(Comment::getCreatedAt);
        return mapper.selectList(qw);
    }

    public Comment create(Comment comment) {
        mapper.insert(comment);
        return comment;
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
