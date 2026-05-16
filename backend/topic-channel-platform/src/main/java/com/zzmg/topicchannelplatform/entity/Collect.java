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
@Table(name = "collect")
public class Collect {

    @EmbeddedId
    private CollectId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private OrdinaryUser user;

    @ManyToOne
    @MapsId("themePostId")
    @JoinColumn(name = "theme_post_id", nullable = false)
    private ThemePost themePost;

    @Column(name = "collect_time")
    private LocalDateTime collectTime;

    public CollectId getId() {
        return id;
    }

    public void setId(CollectId id) {
        this.id = id;
    }

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

    @Embeddable
    public static class CollectId implements Serializable {
        private String userId;
        private String themePostId;

        public CollectId() {
        }

        public CollectId(String userId, String themePostId) {
            this.userId = userId;
            this.themePostId = themePostId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getThemePostId() {
            return themePostId;
        }

        public void setThemePostId(String themePostId) {
            this.themePostId = themePostId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CollectId that)) return false;
            return Objects.equals(userId, that.userId) && Objects.equals(themePostId, that.themePostId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, themePostId);
        }
    }
}
