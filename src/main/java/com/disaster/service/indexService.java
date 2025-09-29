package com.disaster.service;

import java.util.List;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import com.disaster.domain.MemberDTO;
import com.disaster.mapper.indexMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class indexService {

    private final indexMapper indexMapper;

    public MemberDTO getPrimaryAddress(String username) {
        List<MemberDTO> addresses = indexMapper.selectByUseraddreess(username);
        return addresses.isEmpty() ? null : addresses.get(0);
    }
}
=======
import com.disaster.domain.MemberAddressDTO;
import com.disaster.mapper.MemberMapper;
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
>>>>>>> refs/remotes/origin/킹줴지우지
