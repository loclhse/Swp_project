package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.newsDto;
import com.example.Swp_Project.Model.News;
import com.example.Swp_Project.Service.newsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api")
@RestController
public class newsController {

    @Autowired
    private newsService newsservice;

    @PostMapping("/news-create")
    public News createNews(@RequestBody newsDto news) {
        return newsservice.createNews(news);
    }

    @GetMapping("/news-getall")
    public List<News>getAll(){
        return newsservice.getAllNews();
    }

    @PutMapping("/news/{id}")
    public ResponseEntity<?> updateNews(@PathVariable UUID id, @RequestBody newsDto news) {
        return newsservice.updateNews(id, news);
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable UUID id) {
        return newsservice.deleteNews(id);
    }

    @GetMapping("/news/{title}")
    public List<News> findByTitle(@PathVariable String title) {
        return newsservice.findByTitle(title);
    }

    @GetMapping("/news/{category}")
    public List<News> findByCategory(@PathVariable String category) {
        return newsservice.findByCategory(category);
    }

}
