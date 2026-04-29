package com.likelion.seorang.service;

import com.likelion.seorang.common.CustomUserDetails;
import com.likelion.seorang.dto.SignupFormDto;
import com.likelion.seorang.entity.Department;
import com.likelion.seorang.entity.User;
import com.likelion.seorang.repository.DepartmentRepository;
import com.likelion.seorang.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


// 회원가입, 로그인 서비스
@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;

    // 회원가입
    @Transactional
    public void signup(SignupFormDto signupFormDto) {

        // 전화번호에서 하이픈 제거
        String normalizedPhone = signupFormDto.getPhone().replaceAll("[^0-9]", "");

        // 학번 중복 검사
        if (usersRepository.existsByStudentId(signupFormDto.getStudentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "DUPLICATE_STUDENT_ID"
            );
        }

        // 전화번호 중복 검사
        if (usersRepository.existsByPhone(normalizedPhone)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "DUPLICATE_PHONE"
            );
        }

        Department department = departmentRepository.findByDepartName(signupFormDto.getDepartmentName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "DEPARTMENT_NOT_FOUND"
                ));

        User user = User.builder()
                .studentId(signupFormDto.getStudentId())
                .name(signupFormDto.getName())
                .phone(normalizedPhone)
                .department(department)
                .build();

        usersRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Long userId;

        try {
            userId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID입니다.");
        }

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return new CustomUserDetails(
                user.getId(),
                user.getStudentId(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "USER_NOT_FOUND"));
    }
}
