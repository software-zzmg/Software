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
@Table(name = "theme_post")
public class ThemePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_post_id")
    private Long themePostId;

    @ManyToOne
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser author;

    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 3000, message = "帖子内容不能超过3000字")
    @Column(name = "content", length = 3000, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "audit_state")
    private String auditState;

    public Long getThemePostId() {
        return themePostId;
    }

    public void setThemePostId(Long themePostId) {
        this.themePostId = themePostId;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public OrdinaryUser getAuthor() {
        return author;
    }

    public void setAuthor(OrdinaryUser author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public String getAuditState() {
        return auditState;
    }

    public void setAuditState(String auditState) {
        this.auditState = auditState;
    }
}
