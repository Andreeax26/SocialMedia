package socialmedia.server.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "http://localhost:8080")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Add a comment to a post
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentDto commentDto) {
        try {
            Comment savedComment = commentService.addComment(commentDto);
            CommentDto response = new CommentDto(
                    savedComment.getId(),
                    savedComment.getContent(),
                    savedComment.getUser().getName(),
                    savedComment.getUser().getId(),
                    savedComment.getPost().getId(),
                    savedComment.getCreatedOn()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get all comments for a specific post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsByPostId(@PathVariable Long postId) {
        try {
            List<CommentDto> comments = commentService.getAllCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId, @RequestBody CommentDto updatedCommentDto) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, updatedCommentDto);
            CommentDto response = new CommentDto(
                    updatedComment.getId(),
                    updatedComment.getContent(),
                    updatedComment.getUser().getName(),
                    updatedComment.getUser().getId(),
                    updatedComment.getPost().getId(),
                    updatedComment.getCreatedOn()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Delete a comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting comment: " + e.getMessage());
        }
    }

}
