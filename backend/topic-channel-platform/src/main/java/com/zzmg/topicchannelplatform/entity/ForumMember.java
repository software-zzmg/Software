package com.zzmg.topicchannelplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "forum_member")
public class ForumMember {

    @EmbeddedId
    private ForumMemberId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser user;

    @ManyToOne
    @MapsId("forumId")
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @Column(name = "join_time")
    private LocalDateTime joinTime;

    public ForumMemberId getId() {
        return id;
    }

    public void setId(ForumMemberId id) {
        this.id = id;
    }

    public OrdinaryUser getUser() {
        return user;
    }

    public void setUser(OrdinaryUser user) {
        this.user = user;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    @Embeddable
    public static class ForumMemberId implements Serializable {
        private String userId;
        private Long forumId;

        public ForumMemberId() {
        }

        public ForumMemberId(String userId, Long forumId) {
            this.userId = userId;
            this.forumId = forumId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getForumId() {
            return forumId;
        }

        public void setForumId(Long forumId) {
            this.forumId = forumId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ForumMemberId that)) return false;
            return Objects.equals(userId, that.userId) && Objects.equals(forumId, that.forumId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, forumId);
        }
    }
}
