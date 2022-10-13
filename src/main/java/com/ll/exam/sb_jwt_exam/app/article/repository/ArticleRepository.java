package com.ll.exam.sb_jwt_exam.app.article.repository;

import com.ll.exam.sb_jwt_exam.app.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByOrderByIdDesc();
}
