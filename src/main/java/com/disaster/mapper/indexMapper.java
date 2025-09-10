package com.disaster.mapper;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.disaster.domain.userDto;

public interface indexMapper {

	public interface MemberAddressRepository extends JpaRepository<userDto, Long> {
	    List<userDto> findByMemberAddress(String username);
	}
}
