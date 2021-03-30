package com.mySpringProject.main.web;

import com.mySpringProject.main.service.posts.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postsService.findAll());
        return "index";
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id,Model model) {
        model.addAttribute("post", postsService.findById(id));
        return "post-detail";
    }

    @GetMapping("/posts/{id}/update")
    public String postUpdate(@PathVariable Long id,Model model) {
        model.addAttribute("post", postsService.findById(id));
        return "post-update";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }
}
