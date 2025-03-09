package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.News;
import com.example.Swp_Project.Service.newsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/news")
@RestController
public class newsController {
@Autowired
    private newsService newsservice;
    @PostMapping
    public ResponseEntity<?> createNews(@RequestBody News news) {
        return newsservice.createNews(news);
    }
    @GetMapping
    public List<News>getAll(){
        return newsservice.getAllNews();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable UUID id, @RequestBody News news) {
        return newsservice.updateNews(id, news);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable UUID id) {
        return newsservice.deleteNews(id);
    }
    @GetMapping("/title/{title}")
    public List<News> findByTitle(@PathVariable String title) {
        return newsservice.findByTitle(title);
    }
    @GetMapping("/category/{category}")
    public List<News> findByCategory(@PathVariable String category) {
        return newsservice.findByCategory(category);
    }

}
