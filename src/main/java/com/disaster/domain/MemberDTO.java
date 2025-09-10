package com.disaster.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class MemberDTO {
    
    // member 테이블 필드들
    private Long memberId;           // member_id
    private String email;            // email (로그인 아이디)
    private String passwordHash;     // password_hash
    private String name;             // name (실명)
    private String nickname;         // nickname (닉네임) - DB 컬럼 추가
    private String phone;            // phone
    private String lineUserId;       // line_user_id
    private String role;             // role (USER/ADMIN)
    private Boolean isActive;        // is_active
    private Boolean marketingConsent; // marketing_consent
    private Timestamp termsAgreedAt;  // terms_agreed_at
    private Timestamp privacyAgreedAt; // privacy_agreed_at
    private Timestamp createdAt;     // created_at
    private Timestamp updatedAt;     // updated_at
    
    // HTML 폼에서만 사용하는 필드들 (DB 저장 안함)
    private String password;         // 평문 비밀번호 (해싱 전)
    private String password2;        // 비밀번호 확인
    private String verifyCode;       // 인증코드 (세션 처리)
    
    // 주소 관련 필드들 (member_address 테이블용)
    private String postcode;         // postal_code
    private String address1;         // addr_line1  
    private String address2;         // addr_line2
    private String prefCode;         // pref_code (우편번호에서 추출)
    private String muniCode;         // muni_code (우편번호에서 추출)
    
    // 약관 동의 관련
    private Boolean agreeTerms;      // 이용약관 동의
    private Boolean agreePrivacy;    // 개인정보처리방침 동의
    private Boolean agreeMarketing;  // 마케팅 수신 동의
    
    // 생성자
    public MemberDTO() {
        this.role = "USER";  // 기본값
        this.isActive = true; // 기본값
    }
}