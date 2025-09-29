package com.disaster.domain;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommunityDTO {
	
    private Long postId;
    private Long memberId;
    private Long addressId;
    private String muniCode;
    private String disasterType;
    private String title;
    private String body;
    private BigDecimal lat;
    private BigDecimal lon;
    private String status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname;
	
    private String reportReason; // 신고 사유를 담을 필드 추가
	
}
