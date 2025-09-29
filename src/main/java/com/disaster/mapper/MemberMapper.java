package com.disaster.mapper;

import java.sql.Timestamp;
import java.util.List;
<<<<<<< HEAD

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.disaster.domain.MemberDTO;
import com.disaster.domain.MemberAddressDTO;
import org.apache.ibatis.annotations.Param;

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

    // 재설정 토큰 업데이트
    int updateResetToken(@Param("email") String email, 
                        @Param("resetToken") String resetToken, 
                        @Param("expiresAt") Timestamp expiresAt);

    // 토큰으로 사용자 조회
    MemberDTO findByResetToken(String resetToken);

    // 비밀번호 업데이트
    int updatePassword(@Param("email") String email, 
                      @Param("passwordHash") String passwordHash);

    // 토큰 삭제
    int clearResetToken(String email);
    
 // MemberMapper.java에 추가
    MemberAddressDTO findPrimaryAddressByEmail(String email);
    
    //전체 회원 정보 조회
    List<MemberDTO> findByMember();
    
    //신규 가입자 조회
    int countNewMembers();
    
    //전체 회원수 조회
    int totalMemberCount();
    
    //회원 삭제
    int deleteAddressesByMemberId(Long memberId);
    int deleteMemberById(Long memberId);
    
    // 회원 ID로 주소 정보 조회
    MemberAddressDTO findAddressByMemberId(Long memberId);
    
    
=======
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    // 재설정 토큰 업데이트
    int updateResetToken(@Param("email") String email, 
                        @Param("resetToken") String resetToken, 
                        @Param("expiresAt") Timestamp expiresAt);

    // 토큰으로 사용자 조회
    MemberDTO findByResetToken(String resetToken);

    // 비밀번호 업데이트
    int updatePassword(@Param("email") String email, 
                      @Param("passwordHash") String passwordHash);

    // 토큰 삭제
    int clearResetToken(String email);
    
 // MemberMapper.java에 추가
    MemberAddressDTO findPrimaryAddressByEmail(String email);
    
    /**
     * 다중 조건으로 검색된 회원 목록을 조회합니다. (페이징 포함)
     * @param params (searchType, keyword, pageSize, offset)
     * @return 검색 및 페이징 처리된 회원 목록
     */
    List<MemberDTO> findMembersPaginated(Map<String, Object> params);

    /**
     * 다중 조건으로 검색된 회원의 총 수를 조회합니다.
     * @param params (searchType, keyword)
     * @return 검색된 회원 수
     */
    int countMembers(Map<String, Object> params);
    
    //신규 가입자 조회
    int countNewMembers();
    
    //전체 회원수 조회
    int totalMemberCount();
    
    //회원 삭제
    int deleteAddressesByMemberId(Long memberId);
    int deleteMemberById(Long memberId);
    
    // 회원 ID로 주소 조회
    MemberAddressDTO findAddressByMemberId(Long memberId);

	MemberDTO findById(Long memberId);
>>>>>>> refs/remotes/origin/승범
    
}