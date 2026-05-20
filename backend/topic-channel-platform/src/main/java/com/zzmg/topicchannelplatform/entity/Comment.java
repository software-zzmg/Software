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
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "theme_post_id", nullable = false)
    private ThemePost themePost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser author;

    @Size(max = 500, message = "评论内容不能超过500字")
    @Column(name = "content", length = 500, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "audit_state")
    private String auditState;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public ThemePost getThemePost() {
        return themePost;
    }

    public void setThemePost(ThemePost themePost) {
        this.themePost = themePost;
    }

    public OrdinaryUser getAuthor() {
        return author;
    }

    public void setAuthor(OrdinaryUser author) {
        this.author = author;
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
