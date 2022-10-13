package com.ll.exam.sb_jwt_exam.app.article.controller;

import com.ll.exam.sb_jwt_exam.app.article.Service.ArticleService;
import com.ll.exam.sb_jwt_exam.app.article.entity.Article;
import com.ll.exam.sb_jwt_exam.app.base.RsData;
import com.ll.exam.sb_jwt_exam.app.security.entity.MemberContext;
import com.ll.exam.sb_jwt_exam.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("{id}")
    public ResponseEntity<RsData> delete(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext) {
        Article article = articleService.findById(id).orElse(null);

        if (article == null) {
            return Utility.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        if (articleService.actorCanDelete(memberContext, article) == false) {
            return Utility.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "삭제 권한이 없습니다."
                    )
            );
        }

        articleService.delete(article);

        return Utility.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "해당 게시물이 삭제되었습니다."
                )
        );
    }
}
