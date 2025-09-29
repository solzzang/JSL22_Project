package com.disaster.controller;

import com.disaster.domain.MemberAddressDTO;
import com.disaster.service.indexService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final indexService indexService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/me/location")
    @ResponseBody
    public Map<String, Object> meLocation(Principal principal) {
        Map<String, Object> res = new HashMap<>();
        res.put("loggedIn", false);
        res.put("lat", 35.681236);
        res.put("lon", 139.767125);

        System.out.println("=== 디버그 시작 ===");
        System.out.println("Principal: " + principal);
        
        if (principal != null) {
            String username = principal.getName();
            System.out.println("Username: " + username);
            
            MemberAddressDTO addr = indexService.getPrimaryAddress(username);
            System.out.println("Address DTO: " + addr);
            
            if (addr != null) {
                System.out.println("Lat: " + addr.getLat() + ", Lon: " + addr.getLon());
                if (addr.getLat() != null && addr.getLon() != null) {
                    res.put("loggedIn", true);
                    res.put("lat", addr.getLat());
                    res.put("lon", addr.getLon());
                }
            }
        }
        System.out.println("=== 디버그 끝 ===");
        return res;
    }
}