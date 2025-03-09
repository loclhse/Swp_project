package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.News;
import com.example.Swp_Project.Repositories.newsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class newsService {
    @Autowired
    private newsRepositories newsrepo;
    public ResponseEntity<?> createNews(News news) {
        news.setNewsId(UUID.randomUUID());
        news.setCreatedAt(LocalDateTime.now());
        News savedNews = newsrepo.save(news);
        return ResponseEntity.ok(savedNews);
    }
    public List<News> getAllNews() {
        return newsrepo.findAll();
    }
    public ResponseEntity<?> updateNews(UUID id, News updatedNews) {
        Optional<News> existingNewsOpt = newsrepo.findByNewsId(id);

        if (existingNewsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        News existingNews = existingNewsOpt.get();
        existingNews.setTitle(updatedNews.getTitle());
        existingNews.setDescription(updatedNews.getDescription());
        existingNews.setSource(updatedNews.getSource());
        existingNews.setCategory(updatedNews.getCategory());

        newsrepo.save(existingNews);
        return ResponseEntity.ok(existingNews);
    }
    public ResponseEntity<?> deleteNews(UUID id) {
        if (!newsrepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        newsrepo.deleteById(id);
        return ResponseEntity.ok("News deleted successfully.");
    }
    public List<News> findByTitle(String title) {
        return newsrepo.findByTitleIgnoreCase(title);
    }
    public List<News> findByCategory(String category) {
        return newsrepo.findByCategoryIgnoreCase(category);
    }

}
