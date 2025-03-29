package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.NewsDTO;
import com.example.Swp_Project.Model.News;
import com.example.Swp_Project.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api")
@RestController
public class NewsController {

    @Autowired
    private NewsService newsservice;

    @PostMapping("/news-create")
    public News createNews(@RequestBody NewsDTO news) {
        return newsservice.createNews(news);
    }

    @GetMapping("/news-getall")
    public List<News>getAll(){
        return newsservice.getAllNews();
    }

    @PutMapping("/news-update/{id}")
    public ResponseEntity<?> updateNews(@PathVariable UUID id, @RequestBody NewsDTO news) {
        return newsservice.updateNews(id, news);
    }

    @GetMapping("/news-getById/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable UUID id) {
        Optional<News> news = newsservice.findById(id);
        return ResponseEntity.ok(news.get());
    }

    @DeleteMapping("/news-delete/{id}")
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
