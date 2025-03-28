package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.News;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepositories extends MongoRepository<News,UUID> {
    List<News> findByTitleIgnoreCase(String title);
    List<News> findByCategoryIgnoreCase(String category);
    boolean existsById(UUID newsId);
    void deleteById(UUID newsId);
    Optional<News>findByNewsId(UUID newsId);

}
