package com.zzmg.topicchannelplatform.service;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.repository.ForumRepository;
import com.zzmg.topicchannelplatform.repository.OrdinaryUserRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final ThemePostRepository postRepository;
    private final ForumRepository forumRepository;
    private final OrdinaryUserRepository userRepository;

    public SearchService(ThemePostRepository postRepository,
                         ForumRepository forumRepository,
                         OrdinaryUserRepository userRepository) {
        this.postRepository = postRepository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
    }

    public List<ThemePost> searchPosts(String keyword) {
        return postRepository.searchApproved(keyword);
    }

    public List<Forum> searchForums(String keyword) {
        return forumRepository.searchApproved(keyword);
    }

    public List<OrdinaryUser> searchUsers(String keyword) {
        return userRepository.findByUserNameContainingOrUserIdContaining(keyword, keyword);
    }
}
