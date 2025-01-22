package socialmedia.server.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String userName;
    private Long userId;
    private Long postId;
    private LocalDateTime createdOn;

    public CommentDto(Long id, String content, String userName, Long postId, LocalDateTime createdOn) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.postId = postId;
        this.createdOn = createdOn;
    }

    public CommentDto(Long id, String content, Long userId, Long postId, LocalDateTime createdOn) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.createdOn = createdOn;
    }
}
