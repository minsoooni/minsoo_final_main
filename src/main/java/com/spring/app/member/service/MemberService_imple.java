package com.spring.app.member.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.member.domain.CompanyMemberDTO;
import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.domain.MemberRegisterRequest;
import com.spring.app.member.mapper.MemberMapper;

@Service
public class MemberService_imple implements MemberService {

    // 회원(공통) 관련 DB 접근 Mapper
    private final MemberMapper memberMapper;

    // 비밀번호 암호화/검증용 인코더(BCrypt)
    private final PasswordEncoder passwordEncoder;

    public MemberService_imple(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // ======== 공용 ========

    @Override
    public boolean isDuplicatedMemberId(String memberId) {
        // 동일 아이디가 1개 이상이면 중복
        return memberMapper.countByMemberId(memberId) > 0;
    }

    @Override
    public boolean isDuplicatedEmail(String email) {
        // 동일 이메일이 1개 이상이면 중복
        return memberMapper.countByEmail(email) > 0;
    }


    // ======== 개인회원 ========

    @Transactional
    @Override
    public void registerPersonal(MemberRegisterRequest req) {
        // 서버 최종 중복 체크
        if (isDuplicatedMemberId(req.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (isDuplicatedEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 가입 요청(req) -> 저장용 DTO(dto)로 변환
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(req.getMemberId());
        dto.setEmail(req.getEmail());
        dto.setName(req.getName());
        dto.setPhone(req.getPhone());
        dto.setBirthDate(req.getBirthDay());
        dto.setGender(req.getGender());

        // type(0=구직자, 1=기업, 2=관리자) status(0=정지, 1=활성, 2=탈퇴, 3=휴면)
        dto.setType(0);      // 구직자
        dto.setStatus(1);    // 활성
        dto.setMustChangePasswordYn(0);

        // 비밀번호는 반드시 암호화ss해서 저장
        dto.setPassword(passwordEncoder.encode(req.getPassword()));

        // MEMBER  테이블에  저장
        memberMapper.insertMember(dto);

        // 가입 직후 기본 권한 부여
        memberMapper.insertAuthority(dto.getMemberId(), "ROLE_MEMBER");
    }

    // 구직자 회원 아이디 찾기
    @Override
    public String findMemberId(MemberDTO dto) {
        return memberMapper.findMemberId(dto);
    }

    // 구직자 회원 비밀번호 찾기용 회원 조회
    @Override
    public MemberDTO findMemberForPassword(MemberDTO dto) {
        return memberMapper.findMemberForPassword(dto);
    }


    // ======== 기업회원 ========

    @Transactional
    @Override
    public void registerCompany(CompanyMemberDTO dto) {

        // 비밀번호/비밀번호확인 일치 검증(회사 가입 폼에서 넘어온 값 기준)
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 서버 최종 중복 체크
        if (isDuplicatedMemberId(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (isDuplicatedEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 사업자번호는 하이픈 제거 등 정규화 후 중복 체크
        String normalizedBizNo = dto.getBizNo().replaceAll("-", "");
        if (memberMapper.countCompanyByBizNo(normalizedBizNo) > 0) {
            throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
        }

        // MEMBER 테이블에 들어갈 공통 회원정보 생성
        MemberDTO member = new MemberDTO();
        member.setMemberId(dto.getMemberId());
        member.setEmail(dto.getEmail());
        member.setName(dto.getName());
        member.setPhone(dto.getPhone());
        
        // 회사회원은 생년월일/성별을 입력 안 받는다면 NOT NULL 컬럼 기본값 강제
        member.setBirthDate(LocalDate.of(1900, 1, 1));
        member.setGender(0);

        // type(0=구직자, 1=기업, 2=관리자) status(0=정지, 1=활성, 2=탈퇴, 3=휴면)
        member.setType(1);    // 기업
        member.setStatus(1);  // 활성
        member.setMustChangePasswordYn(0);

        // 비밀번호 암호화 저장
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        // MEMBER 테이블에 저장
        memberMapper.insertMember(member);

        // COMPANY 저장(정규화한 사업자번호로 세팅 후 저장)
        dto.setBizNo(normalizedBizNo);
        memberMapper.insertCompany(dto);

        // 기업 권한 부여
        memberMapper.insertAuthority(member.getMemberId(), "ROLE_COMPANY");
    }
    
    // 사업자번호 중복 여부 확인(기업)
    @Override
    public boolean isDuplicatedBizNo(String bizNo) {
        return memberMapper.countCompanyByBizNo(bizNo) > 0;
    }

    // 기업 회원 아이디 찾기
    @Override
    public String findCompanyId(CompanyMemberDTO dto) {
        return memberMapper.findCompanyId(dto);
    }

    // 기업 회원 비밀번호 찾기용 회원 조회
    @Override
    public CompanyMemberDTO findCompanyForPassword(CompanyMemberDTO dto) {
        return memberMapper.findCompanyForPassword(dto);
    }
}