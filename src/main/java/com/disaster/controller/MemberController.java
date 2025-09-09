package com.disaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @GetMapping("/login")
    public String login() {
        return "member/login"; // templates/member/login.html
    }
    
    @GetMapping("/signup")
    public String register() {
        return "member/signup"; // templates/member/register.html
    }
    
    @GetMapping("/mypage")
    public String mypage() {
        return "member/mypage"; // templates/member/mypage.html
    }
}