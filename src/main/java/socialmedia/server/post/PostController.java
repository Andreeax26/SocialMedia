package socialmedia.server.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialmedia.server.comment.Comment;
import socialmedia.server.comment.CommentDto;
import socialmedia.server.user.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:8080")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> addPost(@RequestBody PostDto postDto) {
        try {
            Post post = new Post();
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());

            User user = new User();
            user.setId(postDto.getUserId());
            post.setUser(user);

            Post savedPost = postService.addPost(post);
            return ResponseEntity.ok(savedPost);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Fetch all posts
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        try {
            List<PostDto> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Add a comment to a specific post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addCommentToPost(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        try {
            Comment comment = new Comment();
            comment.setContent(commentDto.getContent());

            User user = new User();
            user.setId(commentDto.getUserId());
            comment.setUser(user);

            Comment savedComment = postService.addCommentToPost(postId, comment);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Long userId) {
        try {
            List<PostDto> userPosts = postService.getPostsByUser(userId);
            return ResponseEntity.ok(userPosts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Update a post
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId, @RequestBody PostDto updatedPostDto) {
        try {
            Post updatedPost = postService.updatePost(postId, updatedPostDto);
            PostDto response = new PostDto(
                    updatedPost.getId(),
                    updatedPost.getTitle(),
                    updatedPost.getContent(),
                    updatedPost.getUser().getName(),
                    updatedPost.getComments().stream().map(comment -> new CommentDto(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                            comment.getPost().getId(),
                            comment.getCreatedOn()
                    )).collect(Collectors.toList()),
                    updatedPost.getCreatedOn()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Delete a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok("Post deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting post: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPostsByKeyword(@RequestParam String keyword) {
        try {
            List<PostDto> posts = postService.searchPostsByKeyword(keyword);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
