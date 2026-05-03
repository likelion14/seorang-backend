package com.likelion.seorang.common;

import com.likelion.seorang.exception.InvalidLikeException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

// 전체 예외 처리 클래스
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO @Valid 바디 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "요청 값이 올바르지 않습니다.";

        return ApiError.of(400, "VALIDATION_ERROR", msg);
    }

    // 폼/쿼리 파라미터 바인딩 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBind(BindException ex) {
        String msg = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "요청 값이 올바르지 않습니다.";

        return ApiError.of(400, "BIND_ERROR", msg);
    }

    // 단건 파라미터(@RequestParam, @PathVariable) 제약 위반
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex) {
        return ApiError.of(400, "CONSTRAINT_VIOLATION", ex.getMessage());
    }

    // JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(HttpMessageNotReadableException ex) {
        return ApiError.of(400, "INVALID_JSON", "요청 본문(JSON) 형식이 올바르지 않습니다.");
    }

    // 필수 파라미터 없음
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissing(MissingServletRequestParameterException ex) {
        return ApiError.of(400, "MISSING_PARAMETER", ex.getParameterName() + " 파라미터가 필요합니다.");
    }

    // HTTP 메서드 미지원
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethod(HttpRequestMethodNotSupportedException ex) {
        return ApiError.of(405, "METHOD_NOT_ALLOWED", "지원하지 않는 메서드입니다.");
    }

    // 지원하지 않는 Content-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ApiError.of(415, "UNSUPPORTED_MEDIA_TYPE", "요청의 Content-Type이 올바르지 않습니다.");
    }

    // 커스텀 에러 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleRSE(ResponseStatusException ex) {
        String code = ex.getReason();

        String msg = switch (code != null ? code : "ERROR") {
            case "NOT_FOUND" -> "존재하지 않는 페이지입니다.";
            case "FORBIDDEN" -> "권한이 없습니다.";
            case "UNAUTHORIZED" -> "인증되지 않은 사용자입니다.";
            case "USER_NOT_FOUND" -> "사용자를 찾을 수 없습니다.";
            case "DEPARTMENT_NOT_FOUND" -> "존재하지 않는 학과입니다.";
            case "DUPLICATE_STUDENT_ID" -> "이미 존재하는 학번입니다.";
            case "DUPLICATE_PHONE" -> "이미 사용 중인 전화번호입니다.";
            case "CONSTRAINT_VIOLATION" -> "데이터 무결성 위반";
            case "INVALID_DAY" -> "운영일은 1, 2, 3 중 하나여야 합니다.";
            case "BOOTH_NOT_FOUND" -> "부스 정보를 찾을 수 없습니다.";
            case "ALREADY_VISITED" -> "이미 방문 체크한 부스입니다.";
            default -> "요청을 처리할 수 없습니다.";
        };

        return ApiError.of(ex.getStatusCode().value(), code != null ? code : "ERROR", msg);
    }

    // 동시에 회원가입 시 DB unique 제약조건에서 터질 때 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        String text = String.valueOf(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());

        if (text.contains("student_id")) {
            return ApiError.of(409, "DUPLICATE_STUDENT_ID", "이미 존재하는 학번입니다.");
        }

        if (text.contains("phone")) {
            return ApiError.of(409, "DUPLICATE_PHONE", "이미 사용 중인 전화번호입니다.");
        }

        if (text.contains("depart_id")) {
            return ApiError.of(400, "DEPARTMENT_NOT_FOUND", "존재하지 않는 학과입니다.");
        }

        return ApiError.of(409, "CONSTRAINT_VIOLATION", "데이터 무결성 위반");
    }

    // JWT 예외 처리
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwt(JwtException ex) {
        String msg = ex.getMessage();

        if (ex instanceof ExpiredJwtException) {
            return ApiError.of(401, "EXPIRED_TOKEN", "토큰이 만료되었습니다.");
        }

        if (msg != null && msg.toLowerCase().contains("signature")) {
            return ApiError.of(401, "INVALID_SIGNATURE", "토큰 서명이 유효하지 않습니다.");
        }

        if (msg != null && msg.contains("JWT")) {
            return ApiError.of(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        }

        return ApiError.of(401, "INVALID_TOKEN", "인증에 실패했습니다.");
    }

    // 멀티파트: form 파트 누락
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingPart(MissingServletRequestPartException ex) {
        String part = ex.getRequestPartName();
        return ApiError.of(400, "MISSING_PART", part + " 파트가 필요합니다.");
    }

    // 파일 용량 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return ApiError.of(413, "FILE_TOO_LARGE", "파일 용량을 확인해주세요.");
    }

    // 멀티파트 요청 오류
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipart(MultipartException ex) {
        return ApiError.of(400, "MULTIPART_ERROR", "파일 업로드 요청 형식이 올바르지 않습니다.");
    }

    // ENUM 바인딩 실패
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            return ApiError.of(400, "INVALID_ENUM_VALUE", "요청 값을 확인해주세요.");
        }

        return ApiError.of(400, "BAD_REQUEST", "요청 파라미터가 올바르지 않습니다.");
    }

    // 그 외 예기치 못한 런타임 에러
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ApiError.of(400, "RUNTIME_ERROR", ex.getMessage());
    }

    // 좋아요 에러
    @ExceptionHandler(InvalidLikeException.class)
    public ResponseEntity<String> handleInvalidLike(InvalidLikeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

}