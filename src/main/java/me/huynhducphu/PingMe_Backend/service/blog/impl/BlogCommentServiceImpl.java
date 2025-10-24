package me.huynhducphu.PingMe_Backend.service.blog.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.repository.BlogCommentRepository;
import me.huynhducphu.PingMe_Backend.service.blog.BlogService;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin 10/24/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class BlogCommentServiceImpl {

    private final CurrentUserProvider currentUserProvider;

    private final BlogCommentRepository blogCommentRepository;

    private final ModelMapper modelMapper;


}
