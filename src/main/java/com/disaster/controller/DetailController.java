package com.disaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/detail")
public class DetailController {

    // 상세 정보 페이지
	@GetMapping
	public String detail() {
	    return "detail/detail";
	}

    
    
}