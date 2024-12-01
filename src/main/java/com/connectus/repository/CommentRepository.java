package com.connectus.repository;

import com.connectus.entity.Comment;
import com.connectus.entity.enums.EStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProjectIdAndStatus(Long projectId, EStatus status);
    List<Comment> findByProjectId(Long projectId);


}
