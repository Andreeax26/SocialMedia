package socialmedia.server.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import socialmedia.server.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String userName;
    private Long userId;
    private List<CommentDto> comments;
    private LocalDateTime createdOn;

    public PostDto(Long id, String title, String content, String userName, List<CommentDto> comments, LocalDateTime createdOn) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userName = userName;
        this.comments = comments;
        this.createdOn = createdOn;
    }
}
