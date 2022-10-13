package com.ll.exam.sb_jwt_exam.app.article.controller;

import com.ll.exam.sb_jwt_exam.app.article.Service.ArticleService;
import com.ll.exam.sb_jwt_exam.app.article.entity.Article;
import com.ll.exam.sb_jwt_exam.app.base.RsData;
import com.ll.exam.sb_jwt_exam.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("")
    public ResponseEntity<RsData> list() {
        List<Article> articles = articleService.findAll();

        return Utility.spring.responseEntityOf(
                RsData.successOf(
                        Utility.mapOf(
                                "articles", articles
                        )
                )
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<RsData> detail(@PathVariable Long id) {
        Article article = articleService.findById(id).orElse(null);

        if (article == null) {
            return Utility.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        return Utility.spring.responseEntityOf(
                RsData.successOf(
                        Utility.mapOf(
                                "article", article
                        )
                )
        );
    }
}
