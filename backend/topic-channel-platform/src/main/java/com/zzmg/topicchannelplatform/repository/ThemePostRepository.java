package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.ThemePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThemePostRepository extends JpaRepository<ThemePost, Long> {

    @Query("""
            select p from ThemePost p
            where p.auditState = '审核通过'
              and (p.title like concat('%', :keyword, '%')
                   or p.content like concat('%', :keyword, '%'))
            order by p.publishTime desc
            """)
    List<ThemePost> searchApproved(@Param("keyword") String keyword);
}
