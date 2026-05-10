package com.zzmg.topicchannelplatform.repository;

import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.entity.ForumMember.ForumMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumMemberRepository extends JpaRepository<ForumMember, ForumMemberId> {
}
