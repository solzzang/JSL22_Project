<<<<<<< HEAD
package com.disaster.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class CommentDTO {

    private Long commentId;
    private Long postId;
    private Long memberId;
    private String body;
    private String status;
    private Timestamp createdAt;
    private String nickname;
    private String title;
=======
package com.disaster.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long commentId;
    private Long postId;
    private Long memberId;
    private String body;
    private String status;
    private LocalDateTime createdAt;
    private String nickname;
    private String title;
    
    private Long parentId;
    private String reportReason; // 신고 사유를 담을 필드 추가
>>>>>>> refs/remotes/origin/승범
}