package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.newsDto;
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
    public News createNews(newsDto news) {
        News nw=new News();
        nw.setNewsId(UUID.randomUUID());
        nw.setTitle(news.getTitle());
        nw.setCategory(news.getCategory());
        nw.setSource(news.getSource());
        nw.setDescription(news.getDescription());
        nw.setCreatedAt(LocalDateTime.now());
        return newsrepo.save(nw);
    }

    public List<News> getAllNews() {
        return newsrepo.findAll();
    }

    public ResponseEntity<?> updateNews(UUID id, newsDto updatedNews) {
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
