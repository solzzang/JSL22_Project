package com.disaster.service;

import org.springframework.stereotype.Service;
import com.disaster.domain.MemberAddressDTO;
import com.disaster.mapper.indexMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class indexService {

    private final indexMapper indexMapper; // MemberMapper 대신 indexMapper 사용

    public MemberAddressDTO getPrimaryAddress(String username) {
        return indexMapper.findPrimaryAddressByEmail(username);
    }
}
