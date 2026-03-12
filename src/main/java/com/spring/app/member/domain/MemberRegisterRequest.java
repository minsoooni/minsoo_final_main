package com.spring.app.member.domain;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


//구직자 회원가입 요청 정보를 담는 DTO

@Data
public class MemberRegisterRequest {

    // 회원 가입 시 입력받는 아이디 (4~20자의 영문, 숫자, _ 만 허용)
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문/숫자/_ 만 가능합니다.")
    private String memberId;

    // 회원 이메일 (중복 불가)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    // 로그인에 사용할 비밀번호 서버에서 암호화 후 저장됨
    @NotBlank(message="비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]|\\\\:;\"'<>,.?/]).{8,}$",
        message = "비밀번호는 최소 8자이며, 대문자 1개 이상 + 특수문자 1개 이상이 포함되어야 합니다."
    )
    private String password;

    // 비밀번호 확인용 필드 (DB에는 저장하지 않음)
    @NotBlank(message="비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    // 회원 이름 (한글 또는 영문만 허용)
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이내입니다.")
    @Pattern(
        regexp = "^[가-힣a-zA-Z]+$",
        message = "이름은 한글 또는 영문자만 입력 가능합니다."
    )
    private String name;

    // 휴대폰 번호 (010-0000-0000 형식)
    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$",
             message = "휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phone;

    // 입력 생년월일
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDay;

    // 입력 성별 (1:남, 2:여)
    @NotNull(message = "성별은 필수입니다.")
    @Min(value = 0, message = "성별 값이 올바르지 않습니다.")
    @Max(value = 2, message = "성별 값이 올바르지 않습니다.")
    private Integer gender;
}