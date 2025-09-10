package com.disaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.disaster.domain.MemberDTO;
import com.disaster.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    
    // ========== 회원가입 관련 ==========
    
    // 회원가입 폼 페이지
    @GetMapping("/signup")
    public String signup() {
        return "member/signup";
    }
    
    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname) {
        boolean exists = memberService.isNicknameExists(nickname);
        return exists ? "exist" : "ok";
    }
    
    // 이메일 중복 확인
    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        boolean exists = memberService.isEmailExists(email);
        return exists ? "exist" : "ok";
    }
    
    // 인증메일 발송
    @PostMapping("/send-verify-email")
    @ResponseBody
    public String sendVerifyEmail(@RequestParam("email") String email, HttpSession session) {
        try {
            if (memberService.isEmailExists(email)) {
                return "exist";
            }
            memberService.sendVerificationEmail(email, session);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // 인증코드 확인
    @PostMapping("/verify-email-code")
    @ResponseBody
    public String verifyEmailCode(@RequestParam("email") String email, 
                                 @RequestParam("verifyCode") String verifyCode,
                                 HttpSession session) {
        try {
            boolean isValid = memberService.verifyEmailCode(email, verifyCode, session);
            return isValid ? "success" : "invalid";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // 회원가입 처리
    @PostMapping("/signup")
    public String signupProcess(MemberDTO memberDTO, HttpSession session, Model model) {
        try {
            // 1. 필수 값 검증
            if (memberDTO.getEmail() == null || memberDTO.getEmail().trim().isEmpty()) {
                model.addAttribute("error", "이메일을 입력해주세요.");
                return "member/signup";
            }
            
            if (memberDTO.getPassword() == null || memberDTO.getPassword().length() < 8) {
                model.addAttribute("error", "비밀번호는 8자 이상이어야 합니다.");
                return "member/signup";
            }
            
            if (memberDTO.getNickname() == null || memberDTO.getNickname().trim().isEmpty()) {
                model.addAttribute("error", "닉네임을 입력해주세요.");
                return "member/signup";
            }
            
            // 2. 이메일 형식 검증
            if (!isValidEmail(memberDTO.getEmail())) {
                model.addAttribute("error", "올바른 이메일 형식이 아닙니다.");
                return "member/signup";
            }
            
            // 3. 약관 동의 확인
            if (memberDTO.getAgreeTerms() == null || !memberDTO.getAgreeTerms()) {
                model.addAttribute("error", "이용약관에 동의해주세요.");
                return "member/signup";
            }
            
            if (memberDTO.getAgreePrivacy() == null || !memberDTO.getAgreePrivacy()) {
                model.addAttribute("error", "개인정보처리방침에 동의해주세요.");
                return "member/signup";
            }
            
            // 4. 회원가입 처리
            memberService.registerMember(memberDTO, session);
            
            return "redirect:/member/login?success=true";
            
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "member/signup";
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "member/signup";
        }
    }
    
    // ========== 로그인 관련 ==========
    
    // 로그인 폼 페이지
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        
        if (success != null) {
            model.addAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
        }
        
        return "member/login";
    }
    
    // ========== 유틸리티 메서드 ==========
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}