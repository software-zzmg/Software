package com.zzmg.topicchannelplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_id")
    private Long forumId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser creator;

    @Column(name = "forum_name", nullable = false)
    private String forumName;

    @Size(max = 500, message = "频道简介不能超过500字")
    @Column(name = "content", length = 500, columnDefinition = "TEXT")
    private String content;

    @Column(name = "audit_state")
    private String auditState;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public OrdinaryUser getCreator() {
        return creator;
    }

    public void setCreator(OrdinaryUser creator) {
        this.creator = creator;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuditState() {
        return auditState;
    }

    public void setAuditState(String auditState) {
        this.auditState = auditState;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
