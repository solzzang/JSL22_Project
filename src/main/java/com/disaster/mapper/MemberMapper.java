package com.disaster.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.disaster.domain.MemberDTO;
import com.disaster.domain.MemberAddressDTO;

@Mapper
public interface MemberMapper {
    
    // ========== 회원가입 관련 ==========
    
    // 이메일 중복 확인
    int countByEmail(String email);
    
    // 닉네임 중복 확인
    int countByNickname(String nickname);
    
    // 회원 등록
    int insertMember(MemberDTO member);
    
    // 주소 등록
    int insertMemberAddress(MemberAddressDTO address);
    
    // ========== 로그인 관련 ==========
    
    // 로그인 - 이메일로 회원 조회
    MemberDTO findByEmail(String email);
}