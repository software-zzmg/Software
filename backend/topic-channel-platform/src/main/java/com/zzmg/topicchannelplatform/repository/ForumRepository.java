package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {

    @Query("""
            select f from Forum f
            where f.auditState = '审核通过'
              and (f.forumName like concat('%', :keyword, '%')
                   or f.content like concat('%', :keyword, '%'))
            order by f.createTime desc
            """)
    List<Forum> searchApproved(@Param("keyword") String keyword);
}
