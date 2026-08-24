package springboot.bg.harisauto.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.news.model.News;

/**
 * NewsRepository.java - Repository for News model.
 *
 * @author AI Engine
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
