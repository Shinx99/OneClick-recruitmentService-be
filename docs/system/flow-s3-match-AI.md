
1. Upload CV (Primary)
   ```text
   Candidate: POST /api/resumes/upload file=CV.pdf
   ↓
   S3: s3://resumes/candidate123/cv-uuid.pdf (resume.resume_upload_url)
   ↓ Save DB chỉ URL
   ```
2. AI Scan CV
   ```text
   Click "Scan CV" → GET s3://resumes/... → PDFBox extract → DeepSeek parse
   → resume.parsed_data = {"skills":["Java"]}
   ```
3. Job Matching
   ```text
   "Match với Job X" → parsed_data (DB) + job.desc → DeepSeek % match
   (S3 chỉ load lần đầu scan)
   ```
   ### 📂 2 Option bạn nói = PERFECT UX!
   FE Flow:
   ```text
   Job Detail Page:
   ┌─────────────────┐
   │ Job: Java Dev   │
   │                 │
   │ [Match CV của tôi] ← Latest CV (parsed_data ready)
   │ [Upload CV mới]  ← New CV → S3 → Scan → Match
   └─────────────────┘
   ```
   Backend 2 endpoints:
    ```bash
   java
   // Option 1: Existing CV (fast!)
   @GetMapping("/jobs/{jobId}/match-my-cv")
   MatchResult matchLatestCv(@PathVariable UUID jobId) {
   UUID candidateId = CurrentUser.id();
   Resume latest = resumeRepo.findLatestByCandidate(candidateId);
   if (latest.getParsedData() == null) {
   handler.scan(latest.getResumeId());  // Auto-scan!
   }
   return matchingHandler.match(latest, job);
   }

    // Option 2: New CV
    @PostMapping("/jobs/{jobId}/match-new-cv")
    MatchResult matchNewCv(@PathVariable UUID jobId, @RequestParam MultipartFile cv) {
    Resume newResume = resumeService.upload(cv);  // S3 + scan
    return matchingHandler.match(newResume, job);
    }
    ```

### 🏗️ S3 Storage Strategy
```text
File Type	Bucket	S3 Path	AI Role
CV	resumes	candidate123/cv-uuid.pdf	Scan → parsed_data
Company Logo	logos	company456/logo.png	Resize/optimize
Job Contract	contracts	job789/contract.pdf	Extract text
Reports	reports	employer123/report.pdf	Analytics
```