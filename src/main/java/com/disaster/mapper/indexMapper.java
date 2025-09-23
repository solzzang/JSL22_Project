package com.disaster.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.disaster.domain.MemberAddressDTO;
import com.disaster.domain.UserDTO;

@Mapper
public interface indexMapper {
    
    @Select("""
        SELECT 
            a.address_id AS addressId,
            a.postal_code AS postalCode,
            a.pref_code AS prefCode,
            a.muni_code AS muniCode,
            a.addr_line1 AS addrLine1,
            a.addr_line2 AS addrLine2,
            a.lat AS lat,
            a.lon AS lon,
            a.is_primary AS isPrimary,
            a.disaster_status AS disasterStatus
        FROM member m
        JOIN member_address a ON a.member_id = m.member_id
        WHERE m.email = #{username}
        AND a.is_primary = TRUE
        LIMIT 1
    """)
    MemberAddressDTO findPrimaryAddressByEmail(@Param("username") String username);
}