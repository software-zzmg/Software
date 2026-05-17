package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "post") String type,
                         Model model) {
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        String normalizedType = normalizeType(type);

        model.addAttribute("keyword", trimmedKeyword);
        model.addAttribute("type", normalizedType);

        if (trimmedKeyword.isEmpty()) {
            model.addAttribute("posts", List.of());
            model.addAttribute("forums", List.of());
            model.addAttribute("users", List.of());
            return "search/result";
        }

        switch (normalizedType) {
            case "forum" -> model.addAttribute("forums", searchService.searchForums(trimmedKeyword));
            case "user" -> model.addAttribute("users", searchService.searchUsers(trimmedKeyword));
            default -> model.addAttribute("posts", searchService.searchPosts(trimmedKeyword));
        }

        return "search/result";
    }

    private String normalizeType(String type) {
        if ("forum".equals(type) || "user".equals(type)) {
            return type;
        }
        return "post";
    }
}
