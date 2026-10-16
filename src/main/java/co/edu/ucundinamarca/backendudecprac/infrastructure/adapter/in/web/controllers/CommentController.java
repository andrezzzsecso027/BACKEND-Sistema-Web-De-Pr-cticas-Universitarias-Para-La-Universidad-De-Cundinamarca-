package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CommentUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.CommentRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private final CommentUseCase commentUseCase;

    public CommentController(CommentUseCase commentUseCase) {
        this.commentUseCase = commentUseCase;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO dto) {
        try {
            Comment comment = new Comment();
            comment.setIdPractice(dto.getIdPractice());
            comment.setCommentText(dto.getCommentText());
            comment.setCompanyRating(dto.getCompanyRating());

            Comment created = commentUseCase.createComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/empresa/{nitCompany}")
    public ResponseEntity<List<Comment>> getApprovedCommentsByCompany(@PathVariable String nitCompany) {
        return ResponseEntity.ok(commentUseCase.getApprovedCommentsByCompany(nitCompany));
    }



}
