package com.disaster.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.disaster.domain.userDto;
import com.disaster.mapper.indexMapper.MemberAddressRepository;

import lombok.RequiredArgsConstructor;

public class indexService {

	@Service
	@RequiredArgsConstructor
	public class MemberAddressService {

	    private final MemberAddressRepository addressRepo;

	    public userDto getPrimaryAddress(String username) {
	        List<userDto> addresses = addressRepo.findByMemberAddress(username);
	        return addresses.isEmpty() ? null : addresses.get(0);
	    }
	}
}
