```bash

1. CLIENT gửi request
   POST http://gateway:8080/recruitment/jobs
   Headers: 
     Authorization: Bearer eyJhbGciOiJIUzI1Ni... (JWT từ authservice)
     Content-Type: application/json
   
2. GATEWAY (8080)
   ✓ Route /recruitment/** → lb://recruitmentservice (Eureka)
   ✓ Forward Authorization header (KHÔNG verify)
   ✓ Rate limit, CORS
   
3. EUREKA (8761) 
   Lookup "recruitmentservice" → IP:8082
   
4. RECRUITMENT SERVICE nhận request (Container 8082)
   
5. SPRING FILTER CHAIN
   JwtAuthenticationFilter → 
     oauth2ResourceServer.md. Extract Bearer token
     b. jwtDecoder.verify(signature với JWT_SECRET từ env)
     c. Decode claims: {accountId:123, roles:["HR"], exp:..., iat:...}
     d. Check exp/not expired
     e. Spring SecurityContextHolder = Authentication(accountId=123, ROLE_HR)
   
6. DISPATCHER SERVLET → @PostMapping("/api/recruitment/jobs")
   CreateJobController.createJob()
   
7. METHOD SECURITY (@EnableMethodSecurity)
   @PreAuthorize("hasRole('HR')") → check SecurityContext → OK
   
8. CONTROLLER inject @CurrentUser Long accountId = 123
   
9. HANDLER EXECUTE BUSINESS LOGIC
   CreateJobHandler.execute(accountId, request)
   oauth2ResourceServer.md. Validate @Valid CreateJobRequest
   b. Check companyId=req.companyId belongs to accountId=123 (ownership)
   c. Job job = new Job(req.title, req.companyId, status=DRAFT)
   d. jobRepository.save(job)
   e. JobCreatedEvent.publish() → async email
   
10. RESPONSE
    ApiResponse.success(new CreateJobResponse(job.id))
    
11. GLOBAL EXCEPTION HANDLER (nếu lỗi)
    UnauthorizedException → 401
    JobOwnershipException → 403
    
12. GATEWAY forward response → CLIENT
```

```bash
Client ──POST /api/recruitment/profile───> RecruitmentService
                  │
                  ▼
1. Filter Chain: BearerTokenResolver extract "Authorization: Bearer eyJ..."
                  │
                  ▼
2. NimbusJwtDecoder: Parse + Validate JWT
   ├── Signature: Fetch JWKS → Verify kid/RS256
   ├── Claims: iss=localhost:9000, exp, nbf, aud
   └── Custom: roles → ROLE_ADMIN (qua converter)
                  │
                  ▼
3. JwtAuthenticationConverter: 
   ├── Principal: accountId="user123"
   └── Authorities: ["ROLE_ADMIN"]
                  │
                  ▼
4. SecurityContext: Set Authentication
                  │
                  ▼
5. @PreAuthorize("hasRole('ADMIN')"): PASS ✓
                  │
                  ▼
6. Controller: Response data
```

## 🔐 5 Solutions JWT Microservices (Từ simple → enterprise)
Solution	Coupling	Complexity	Scale	Enterprise Usage
```bash
1. Shared JWT Secret (Current ✅)	Low	Low	Good	Netflix, Grab
2. JWT with Public Key	Low	Medium	Excellent	Google, AWS
3. Opaque Token + Introspection	Medium	Medium	Good	Okta, Auth0
4. gRPC + Mutual TLS	High	High	Excellent	Uber, Google
5. API Gateway + Centralized Auth	Low	High	Excellent	AWS API GW, Kong
```

```bash
HS256: 1 secret chia đều → Dễ leak
RS256: 1 private (auth) + N public (services) → An toàn
```