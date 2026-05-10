package com.zzmg.topicchannelplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum_member")
@IdClass(ForumMember.ForumMemberId.class)
public class ForumMember {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @Column(name = "join_time")
    private LocalDateTime joinTime;

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

    public static class ForumMemberId implements Serializable {
        private String user;
        private String forum;
    }
}
