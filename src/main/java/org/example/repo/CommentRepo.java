package org.example.repo;

import org.example.entity.Comment;
import org.example.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    Page<Comment> findByTask(Task task, Pageable pageable);
}
