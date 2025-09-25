// ProfileUpdateDto.java
package com.disaster.domain;

import java.math.BigDecimal; // BigDecimal import 추가
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDTO {
    private String name;
    private String nickname;
    private String phone;
    
    // === 주소 필드 수정 및 추가 ===
    private String postalCode; // 우편번호 (7자리)
    private String addrLine1;  // 정목 (town)
    private String addrLine2;  // 상세주소 (detailAddress)

    // 서버 전송용 코드 및 좌표
    private String prefCode;   // 도도부현 코드
    private String muniCode;   // 시구정촌 코드
    private BigDecimal lat;      // 위도
    private BigDecimal lon;      // 경도
}