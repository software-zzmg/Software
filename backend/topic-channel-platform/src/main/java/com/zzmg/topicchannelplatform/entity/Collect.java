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
@Table(name = "collect")
@IdClass(Collect.CollectId.class)
public class Collect {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "theme_post_id", nullable = false)
    private ThemePost themePost;

    @Column(name = "collect_time")
    private LocalDateTime collectTime;

    public OrdinaryUser getUser() {
        return user;
    }

    public void setUser(OrdinaryUser user) {
        this.user = user;
    }

    public ThemePost getThemePost() {
        return themePost;
    }

    public void setThemePost(ThemePost themePost) {
        this.themePost = themePost;
    }

    public LocalDateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(LocalDateTime collectTime) {
        this.collectTime = collectTime;
    }

    public static class CollectId implements Serializable {
        private String user;
        private String themePost;
    }
}
