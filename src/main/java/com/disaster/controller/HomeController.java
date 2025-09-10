package com.disaster.controller;

import com.disaster.domain.MemberAddress;
import com.disaster.dto.MeLocationRes;
import com.disaster.repository.MemberAddressRepository;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    private final MemberAddressRepository addressRepo;

    @Autowired
    public HomeController(MemberAddressRepository addressRepo) {
        this.addressRepo = addressRepo;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/me/location")
    @ResponseBody
    public MeLocationRes meLocation(Principal principal) {
        MeLocationRes res = new MeLocationRes(false, 35.681236, 139.767125);

        if (principal == null) {
            return res; //
        }

        String username = principal.getName();
        List<MemberAddress> addrOpt = addressRepo.findPrimaryByMemberUsername(username);

        if (addrOpt.isPresent()) {
            MemberAddress addr = addrOpt.get();
            if (addr.getLat() != null && addr.getLon() != null) {
                res.setLoggedIn(true);
                res.setLat(addr.getLat());
                res.setLon(addr.getLon());
            } else {
                res.setLoggedIn(true);
            }
        } else {
            res.setLoggedIn(true);
        }

        return res;
    }
}