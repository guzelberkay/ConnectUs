package com.connectus.services;

import com.connectus.dto.request.CommentDeleteRequestDTO;
import com.connectus.dto.request.CommentSaveRequestDTO;
import com.connectus.dto.response.CommentResponseDTO;
import com.connectus.entity.Auth;
import com.connectus.entity.Comment;
import com.connectus.entity.enums.EStatus;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.CommentRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(CommentSaveRequestDTO dto) {

        Comment comment = Comment.builder()
                .projectId(dto.projectId())
                .companyName(dto.companyName())
                .name(dto.name())
                .surname(dto.surname())
                .email(dto.email())
                .comment(dto.comment())
                .build();

        commentRepository.save(comment);
        return true;
    }
    public Boolean approveComment(String token, Long commentId) {
        // Token'dan kullanıcı ID'sini çıkartma
        Long authId = extractAuthIdFromToken(token);

        // Kullanıcıyı bulma
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        // Yorumu bulma
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorType.COMMENT_NOT_FOUND));

        // Yorumun "PENDING" statüsünde olup olmadığını kontrol etme
        if (comment.getStatus() == EStatus.PENDING) {
            comment.setStatus(EStatus.ACTIVE);
            commentRepository.save(comment);  // Yorumun durumunu "ACTIVE" olarak güncelleme
            return true;
        } else {
            // Yorum zaten onaylanmış veya reddedilmişse hata fırlatma
            throw new GeneralException(ErrorType.COMMENT_ALREADY_APPROVED_OR_REJECTED);
        }
    }


    public Boolean delete(CommentDeleteRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        Comment comment = commentRepository.findById(dto.commentId())
                .orElseThrow(() -> new GeneralException(ErrorType.COMMENT_NOT_FOUND));


        commentRepository.delete(comment);
        return true;
    }
    public List<CommentResponseDTO> getAllComents(String token) {
        Long authId = extractAuthIdFromToken(token);
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getProjectId(),
                        comment.getCompanyName(),
                        comment.getName(),
                        comment.getSurname(),
                        comment.getEmail(),
                        comment.getComment()
                ))
                .collect(Collectors.toList());
    }

    public List<CommentResponseDTO> getCommentsByProjectId(Long projectId) {
        List<Comment> comments = commentRepository.findByProjectIdAndStatus(projectId, EStatus.ACTIVE);

        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getProjectId(),
                        comment.getCompanyName(),
                        comment.getName(),
                        comment.getSurname(),
                        comment.getEmail(),
                        comment.getComment()
                ))
                .collect(Collectors.toList());
    }




    private Long extractAuthIdFromToken(String token) {
        Optional<Long> authIdOptional = jwtTokenManager.getAuthIdFromToken(token);
        if (authIdOptional.isPresent()) {
            return authIdOptional.get();
        } else {
            throw new RuntimeException("AuthId could not be extracted from token");
        }
    }

}
