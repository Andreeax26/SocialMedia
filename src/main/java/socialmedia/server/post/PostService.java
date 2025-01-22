package socialmedia.server.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socialmedia.server.comment.Comment;
import socialmedia.server.comment.CommentDto;
import socialmedia.server.comment.CommentRepository;
import socialmedia.server.user.User;
import socialmedia.server.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream().map(post -> {
            // Map comments
            List<CommentDto> comments = post.getComments().stream()
                    .map(comment -> new CommentDto(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                            comment.getPost().getId(),
                            comment.getCreatedOn()
                    ))
                    .collect(Collectors.toList());

            // Map Post to PostDto
            return new PostDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getUser() != null ? post.getUser().getName() : "Unknown User",
                    comments,
                    post.getCreatedOn() // Include createdOn field
            );
        }).collect(Collectors.toList());
    }

    // Find a post by ID
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));
    }

    // Add a new post
    public Post addPost(Post post) {
        if (post.getUser() == null || post.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required to create a post.");
        }

        User user = userService.findById(post.getUser().getId());
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + post.getUser().getId());
        }

        post.setUser(user);
        post.setCreatedOn(LocalDateTime.now());

        return postRepository.save(post);
    }


    // Fetch all comments for a specific post
    public List<CommentDto> getAllCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                        comment.getPost().getId(),
                        comment.getCreatedOn()
                ))
                .collect(Collectors.toList());
    }

    // Update a post
    public Post updatePost(Long postId, PostDto updatedPostDto) {
        Post existingPost = findById(postId);

        existingPost.setTitle(updatedPostDto.getTitle());
        existingPost.setContent(updatedPostDto.getContent());

        if (updatedPostDto.getUserId() != null) {
            User user = userService.findById(updatedPostDto.getUserId());
            if (user != null) {
                existingPost.setUser(user);
            }
        }

        return postRepository.save(existingPost);
    }

    // Delete a post
    public void deletePost(Long postId) {
        Post post = findById(postId);
        commentRepository.deleteAll(post.getComments());
        postRepository.delete(post);
    }

    // Add a comment to a specific post
    public Comment addCommentToPost(Long postId, Comment comment) {
        Post post = findById(postId);

        if (comment.getUser() == null || comment.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required to add a comment.");
        }

        User user = userService.findById(comment.getUser().getId());
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + comment.getUser().getId());
        }

        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedOn(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<PostDto> getPostsByUser(Long userId) {
        return postRepository.findByUserId(userId).stream().map(post -> {
            List<CommentDto> comments = post.getComments().stream()
                    .map(comment -> new CommentDto(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                            comment.getPost().getId(),
                            comment.getCreatedOn()
                    ))
                    .collect(Collectors.toList());

            return new PostDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getUser() != null ? post.getUser().getName() : "Unknown User",
                    comments,
                    post.getCreatedOn()
            );
        }).collect(Collectors.toList());
    }

    public List<PostDto> searchPostsByKeyword(String keyword) {
        List<Post> posts = postRepository.findByKeyword(keyword);

        return posts.stream().map(post -> new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser() != null ? post.getUser().getName() : "Unknown User",
                post.getComments().stream().map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getName() : "Unknown User",
                        comment.getPost().getId(),
                        comment.getCreatedOn()
                )).collect(Collectors.toList()),
                post.getCreatedOn()
        )).collect(Collectors.toList());
    }

}
