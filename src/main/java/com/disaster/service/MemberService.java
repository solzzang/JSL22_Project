package com.disaster.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.disaster.domain.MemberAddressDTO;
import com.disaster.domain.MemberDTO;
import com.disaster.mapper.MemberMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    
    // ========== Spring Security 로그인 관련 ==========
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username은 실제로는 email (로그인 시 email 사용)
        MemberDTO member = memberMapper.findByEmail(username);
        
        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        
        // 권한 설정
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole()));
        
        // Spring Security User 객체 반환
        return new User(
            member.getEmail(),          // username (email)
            member.getPasswordHash(),   // password (암호화된)
            member.getIsActive(),       // enabled
            true,                       // accountNonExpired
            true,                       // credentialsNonExpired
            true,                       // accountNonLocked
            authorities                 // authorities
        );
    }
    
    // ========== 회원가입 관련 ==========
    
    // 이메일 중복 확인
    public boolean isEmailExists(String email) {
        return memberMapper.countByEmail(email) > 0;
    }
    
    // 닉네임 중복 확인
    public boolean isNicknameExists(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }
    
    // 인증메일 발송
    public void sendVerificationEmail(String email, HttpSession session) {
        // 6자리 랜덤 인증코드 생성
        String verifyCode = String.format("%06d", new Random().nextInt(1000000));
        
        // 세션에 인증코드와 이메일 저장 (5분 후 만료)
        session.setAttribute("verifyCode", verifyCode);
        session.setAttribute("verifyEmail", email);
        session.setAttribute("verifyTime", System.currentTimeMillis());
        
        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[재난대응시스템] 이메일 인증코드");
        message.setText("인증코드: " + verifyCode + "\n\n5분 내에 입력해주세요.");
        
        mailSender.send(message);
    }
    
    // 인증코드 확인
    public boolean verifyEmailCode(String email, String inputCode, HttpSession session) {
        String sessionCode = (String) session.getAttribute("verifyCode");
        String sessionEmail = (String) session.getAttribute("verifyEmail");
        Long verifyTime = (Long) session.getAttribute("verifyTime");
        
        // 세션에 저장된 정보가 없으면 false
        if (sessionCode == null || sessionEmail == null || verifyTime == null) {
            return false;
        }
        
        // 5분(300초) 초과시 만료
        if (System.currentTimeMillis() - verifyTime > 300000) {
            session.removeAttribute("verifyCode");
            session.removeAttribute("verifyEmail");
            session.removeAttribute("verifyTime");
            return false;
        }
        
        // 이메일과 인증코드 일치 확인
        if (email.equals(sessionEmail) && inputCode.equals(sessionCode)) {
            session.setAttribute("emailVerified", true);
            return true;
        }
        
        return false;
    }
    
    // 회원가입 처리
    @Transactional
    public void registerMember(MemberDTO memberDTO, HttpSession session) {
        // 이메일 인증 확인 - 임시 주석처리
        /*
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
        if (emailVerified == null || !emailVerified) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }
        */
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPasswordHash(encodedPassword);
        
        // 약관 동의 시간 설정
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (memberDTO.getAgreeTerms() != null && memberDTO.getAgreeTerms()) {
            memberDTO.setTermsAgreedAt(now);
        }
        if (memberDTO.getAgreePrivacy() != null && memberDTO.getAgreePrivacy()) {
            memberDTO.setPrivacyAgreedAt(now);
        }
        
        // 마케팅 동의 설정
        memberDTO.setMarketingConsent(memberDTO.getAgreeMarketing());
        
        // 회원 정보 저장
        memberMapper.insertMember(memberDTO);
        
        // 주소 정보가 있으면 저장
        if (memberDTO.getPostcode() != null && !memberDTO.getPostcode().isEmpty()) {
            MemberAddressDTO addressDTO = new MemberAddressDTO();
            addressDTO.setMemberId(memberDTO.getMemberId());
            addressDTO.setPostalCode(memberDTO.getPostcode());
            addressDTO.setAddrLine1(memberDTO.getAddress1());
            addressDTO.setAddrLine2(memberDTO.getAddress2());
            
            // 우편번호에서 지역코드 추출
            if (memberDTO.getPostcode().length() >= 5) {
                addressDTO.setPrefCode(memberDTO.getPostcode().substring(0, 2));
                addressDTO.setMuniCode(memberDTO.getPostcode());
            }
            
            memberMapper.insertMemberAddress(addressDTO);
        }
        
        // 인증 세션 정보 삭제
        session.removeAttribute("verifyCode");
        session.removeAttribute("verifyEmail");
        session.removeAttribute("verifyTime");
        session.removeAttribute("emailVerified");
    }
}