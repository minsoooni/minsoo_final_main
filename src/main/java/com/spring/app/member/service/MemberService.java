package com.spring.app.member.service;

import com.spring.app.member.domain.CompanyMemberDTO;
import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.domain.MemberRegisterRequest;

import jakarta.validation.Valid;

public interface MemberService {

    boolean isDuplicatedMemberId(String memberId);
    boolean isDuplicatedEmail(String email);

    // 구직자 회원가입
	void registerPersonal(@Valid MemberRegisterRequest req);
	
	// 기업 회원가입 
	void registerCompany(CompanyMemberDTO dto);
	
	// 사업자 번호 중복체크 
	boolean isDuplicatedBizNo(String bizNo);

	String findMemberId(MemberDTO dto);

	MemberDTO findMemberForPassword(MemberDTO dto);

	String findCompanyId(CompanyMemberDTO dto);

	CompanyMemberDTO findCompanyForPassword(CompanyMemberDTO dto);
	
	
}
