package com.disaster.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.disaster.domain.userDto;
import com.disaster.mapper.indexMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class indexService {

    private final indexMapper indexMapper;

    public userDto getPrimaryAddress(String username) {
        List<userDto> addresses = indexMapper.selectByUseraddreess(username);
        return addresses.isEmpty() ? null : addresses.get(0);
    }
}