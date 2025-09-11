# 🛠️ 트러블슈팅 #3 – 잘못된 토큰에 403 반환 → 401로 바로잡기

---

## ⚡ 문제 상황

- **증상**: Authorization 헤더에 **잘못된 토큰**이 들어오면 `401`이 아니라 `403`이 응답됨 ⚠️
- **부작용**:
    - 정상 **미로그인 요청**(헤더 없음)과 **잘못된 토큰 요청**(헤더 있음)을 구분하지 못함 🚫
    - 공통 응답 포맷(에러 표준화) 일관성 저하 🌀

---

## 🔍 원인 분석

### 🧑‍💻 기존 코드

```java
// (기존) 잘못된 토큰이어도 null 반환
private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null; // 헤더가 없거나 형식이 이상하면 모두 null
}

```

### 📌 핵심 원인

- 헤더가 **있는데 형식이 잘못된 경우**에도 `null` 반환
    
    → 이후 체인에서 **익명 인증(AnonymousAuthenticationFilter)** 가 주입되어 **인가 단계**로 넘어감
    
    → 보호 리소스 접근 시 `AccessDeniedHandler` → **403 Forbidden** 발생
    
- 반면, **헤더가 아예 없는 경우**는 미로그인으로 처리되어 `permitAll()` 리소스는 통과, 보호 리소스는 `401`

👉 결과적으로 **“잘못된 토큰”과 “토큰 없음”이 동일하게 처리**되어 보안/UX 모두 의도와 달라짐

---

## 🛠️ 해결 방법

### ✅ 개선 코드 – 케이스 분리

1. 헤더 없음 → `null` 반환 (미로그인)
2. 헤더 있음 + 형식 오류 → `CustomException(INVALID_TOKEN)` 발생 → **401**
3. 정상 `Bearer` 헤더 → 토큰 파싱

```java
private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    // 1) 헤더 없음 → 미로그인 요청
    if (!StringUtils.hasText(bearerToken)) {
        return null;
    }

    // 2) 형식 오류 → 인증 예외 발생(401)
    if (!bearerToken.startsWith("Bearer ")) {
        throw new CustomException(INVALID_TOKEN);
    }

    // 3) 정상 Bearer → 토큰 파싱
    return bearerToken.substring(7);
}

```

✨ 포인트

- **형식 오류를 인증 예외로 승격** → `AuthenticationEntryPoint` 경로를 타서 **401 Unauthorized** 반환
- “헤더 없음”과 “잘못된 토큰”을 명확히 구분

---

## 🎯 결과

- 잘못된 토큰 요청 → **즉시 401 Unauthorized** 반환
- **미로그인(null)** vs **잘못된 토큰(exception)** 구분 가능
- API 에러 응답의 **일관성/신뢰성 강화**

---

## 🧪 검증 시나리오 (체크리스트)

- [ ]  헤더 없음 + 공개 엔드포인트 → 200 ✅
- [ ]  헤더 없음 + 보호 엔드포인트 → 401 ❌
- [ ]  `Authorization: BearerXYZ` (공백/접두사 오류) → 401 ❌
- [ ]  `Authorization: Bearer abc` (정상 형식, 서명 불일치) → 401 ❌
- [ ]  정상 토큰 → 200 ✅

---

## 🔎 추가 고려 사항

- `CustomException(INVALID_TOKEN)` → 글로벌 예외 핸들러(`@RestControllerAdvice`) or `AuthenticationEntryPoint` 에서 **401 매핑**
- (선택) 오류 세분화
    - `INVALID_FORMAT`
    - `INVALID_SIGNATURE`
    - `TOKEN_EXPIRED`

  → 모니터링/디버깅 편의성 ↑
