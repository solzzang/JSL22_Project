package com.disaster.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.disaster.domain.MemberDTO;
import com.disaster.domain.MemberAddressDTO;


@Mapper
public interface MemberMapper {

    // ========== 회원가입 관련 ==========

    int countByEmail(String email);
    int countByNickname(String nickname);
    int insertMember(MemberDTO member);
    int insertMemberAddress(MemberAddressDTO address);
    
    // 회원가입 시 user_pref 테이블에 기본값을 넣는 메소드
    @Insert("INSERT INTO user_pref (member_id, notify_line, notify_email) VALUES (#{memberId}, FALSE, FALSE)")
    void insertUserPref(Long memberId);

    // ========== 로그인 관련 ==========

    MemberDTO findByEmail(String email);

    // ========== 비밀번호 재설정 관련 ==========

    int updateResetToken(@Param("email") String email, 
                         @Param("resetToken") String resetToken, 
                         @Param("expiresAt") Timestamp expiresAt);

    MemberDTO findByResetToken(String resetToken);

    int updatePassword(@Param("email") String email, 
                       @Param("passwordHash") String passwordHash);

    int clearResetToken(String email);

    // ========== 마이페이지 및 회원정보 조회 ==========
    
    // user_pref 테이블과 JOIN하여 notify_line 값을 함께 조회
    MemberDTO findById(Long memberId);
    
    MemberAddressDTO findAddressByMemberId(Long memberId);
    MemberAddressDTO findPrimaryAddressByEmail(String email);

    @Update("UPDATE user_pref SET notify_line = #{isNotifyEnabled} WHERE member_id = #{memberId}")
    void updateNotifyLine(@Param("memberId") Long memberId, @Param("isNotifyEnabled") boolean isNotifyEnabled);

    @Update("UPDATE member SET line_user_id = #{lineUserId} WHERE member_id = #{memberId}")
    void updateLineUserId(@Param("memberId") Long memberId, @Param("lineUserId") String lineUserId);
    
    // ========== 프로필 정보 수정 관련 (추가된 부분) ==========

    /**
     * 회원의 기본 정보(이름, 닉네임, 연락처)를 수정합니다.
     * @param member 수정할 정보가 담긴 MemberDTO 객체
     * @return 영향을 받은 행의 수
     */
    int updateMemberProfile(MemberDTO member);

    /**
     * 회원의 주소 정보를 수정합니다.
     * @param address 수정할 정보가 담긴 MemberAddressDTO 객체
     * @return 영향을 받은 행의 수
     */
    int updateMemberAddress(MemberAddressDTO address);

    // ========== 관리자 페이지 관련 ==========

    List<MemberDTO> findByMember();
    int countNewMembers();
    int totalMemberCount();
    int deleteAddressesByMemberId(Long memberId);
    int deleteMemberById(Long memberId);
}