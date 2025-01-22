package socialmedia.server.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socialmedia.server.post.Post;
import socialmedia.server.post.PostService;
import socialmedia.server.user.User;
import socialmedia.server.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // Add a comment to a post
    public Comment addComment(CommentDto commentDto) {
        // Validate input
        if (commentDto.getUserId() == null || commentDto.getPostId() == null || commentDto.getContent() == null) {
            throw new IllegalArgumentException("User ID, Post ID, and content are required.");
        }

        Post post = postService.findById(commentDto.getPostId());
        if (post == null) {
            throw new RuntimeException("Post not found with ID: " + commentDto.getPostId());
        }

        User user = userService.findById(commentDto.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + commentDto.getUserId());
        }

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedOn(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // Get all comments for a specific post
    public List<CommentDto> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                        comment.getUser() != null ? comment.getUser().getId() : null,
                        comment.getPost().getId(),
                        comment.getCreatedOn()
                ))
                .collect(Collectors.toList());
    }

    // Update a comment
    public Comment updateComment(Long commentId, CommentDto updatedCommentDto) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        existingComment.setContent(updatedCommentDto.getContent());

        if (updatedCommentDto.getUserId() != null) {
            User user = userService.findById(updatedCommentDto.getUserId());
            if (user != null) {
                existingComment.setUser(user);
            }
        }

        return commentRepository.save(existingComment);
    }

    // Delete a comment
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        commentRepository.delete(comment);
    }

}
