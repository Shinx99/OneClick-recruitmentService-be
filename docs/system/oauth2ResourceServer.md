```bash
oauth2ResourceServer đang làm gì?
Recruitment nhận token

Header: Authorization: Bearer <JWT>

Spring Security bắt token bằng BearerTokenAuthenticationFilter.
​

NimbusJwtDecoder tự gọi JWKS của authService

Dùng jwk-set-uri trong yml:
http://authService-be-app:8080/oauth2/jwks.
​

Gửi GET tới /oauth2/jwks, lấy về JSON:

json
{
  "keys": [{
    "kid": "auth-key",
    "kty": "RSA",
    "alg": "RS256",
    "n": "...",
    "e": "AQAB"
  }]
}
Match key + verify chữ ký JWT

Lấy kid + alg trong header token (auth-key, RS256).

Chọn đúng public key từ JWKS, dùng để verify signature của token.

Nếu chữ ký hợp lệ + exp chưa hết hạn → token valid, authentication pass.

Map claims thành SecurityContext

Dùng jwtAuthenticationConverter() của bạn:

Claim roles → ROLE_admin, ROLE_user, ...

accountId (nếu có) → principal.
​

Kết quả: SecurityContextHolder chứa JwtAuthenticationToken với authorities và principal.

Authorization trên endpoint

Rules trong SecurityConfig:

/api/recruitment/** → .authenticated()

/api/admin/** → .hasRole("ADMIN")

Spring dùng authorities từ bước 4 để quyết định pass/403.
​

Nói ngắn gọn:

Authservice: tạo token bằng private key.

Recruitment: dùng oauth2ResourceServer + JWKS để tự động fetch public key, verify token, rồi dùng roles/claims trong token để authorize endpoint.
```