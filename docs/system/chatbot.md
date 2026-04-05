## REST API Specification

| API | Endpoint | Method | Mô tả ngắn | Nhiệm vụ chính |
|-----|----------|--------|------------|---------------|
| **User APIs** | | | | |
| Chat với AI | `/api/chatbot/me/messages` | `POST` | Tạo hoặc tiếp tục hội thoại với AI | Tạo/tìm hội thoại `open`, lưu message user, gọi AI, lưu message AI, trả `AiChatResponseDto` chứa 2 tin mới nhất |
| Lấy hội thoại mở hiện tại | `/api/chatbot/me/conversation` | `GET` | Lấy hội thoại đang mở của user hiện tại | Tìm hội thoại `open` mới nhất theo `userId`/`userType`, trả lịch sử toàn bộ tin nhắn |
| Lấy lịch sử hội thoại theo ID | `/api/chatbot/me/conversations/{conversationId}` | `GET` | Xem lịch sử một hội thoại cụ thể | Kiểm tra quyền (phải là chủ hội thoại), trả lịch sử toàn bộ tin nhắn |
| Yêu cầu gặp admin | `/api/chatbot/me/handoff` | `POST` | Chuyển hội thoại sang chờ admin hỗ trợ | Đổi status thành `handoff`, thêm system message, đánh dấu hội thoại vào hàng chờ admin |
| **Admin APIs** | | | | |
| Load danh sách chờ | `/api/chatbot/admin/conversations` | `GET` | Load danh sách conversation đang chờ admin | Lấy tất cả hội thoại `waiting_admin=true`, sắp xếp theo thời gian chờ |
| Xem chi tiết conversation | `/api/chatbot/admin/conversations/{conversationId}` | `GET` | Load lịch sử conversation khi admin mở chi tiết | Trả toàn bộ lịch sử tin nhắn của conversation |
| Claim conversation | `/api/chatbot/admin/conversations/{conversationId}/claim` | `POST` | Admin nhận xử lý conversation | Đánh dấu `claimed_by=adminId`, xóa khỏi hàng chờ |
| Đóng conversation | `/api/chatbot/admin/conversations/{conversationId}/close` | `POST` | Admin đóng conversation | Đổi status thành `closed`, lưu `closed_by`, `closed_at` |

## STOMP WebSocket Specification

### Client Publish Destinations (vào `/app/*`)

| Action | Endpoint | Payload | Server xử lý |
|--------|----------|---------|--------------|
| User gửi message | `/app/chat.send` | `{ "conversationId": "uuid", "message": "..." }` | `@MessageMapping("/chat.send")` → lưu DB → broadcast `/topic/chat/{conversationId}` |
| User yêu cầu handoff | `/app/chat.handoff` | `{ "conversationId": "uuid" }` | `@MessageMapping("/chat.handoff")` → đổi status `waiting_admin` → broadcast `/topic/admin/waiting-conversations` |
| Admin claim conversation | `/app/admin.chat.claim` | `{ "conversationId": "uuid" }` | `@MessageMapping("/admin.chat.claim")` → đánh dấu `claimed_by` → xóa khỏi waiting list |
| Admin gửi message | `/app/admin.chat.send` | `{ "conversationId": "uuid", "message": "..." }` | `@MessageMapping("/admin.chat.send")` → lưu DB → broadcast `/topic/chat/{conversationId}` |

### Client Subscribe Topics

| Topic | Mục đích | Ai subscribe |
|-------|----------|--------------|
| `/topic/chat/{conversationId}` | Nhận tin nhắn mới realtime của conversation cụ thể | User + Admin (khi mở conversation) |
| `/topic/admin/waiting-conversations` | Cập nhật realtime danh sách conversation chờ | Admin dashboard |


```bash
Flow đúng sẽ là:

User đang chat với AI.

User bấm yêu cầu gặp admin → frontend gửi /app/chat.handoff hoặc REST /api/chatbot/me/handoff.

Backend publish cập nhật cho admin qua /topic/admin/waiting-conversations.

Admin mở dashboard, thấy conversation mới.

Admin claim conversation bằng /app/admin.chat.claim hoặc REST /api/chatbot/admin/conversations/{conversationId}/claim.

Sau đó admin gửi tin nhắn bằng /app/admin.chat.send.

User đang subscribe /topic/chat/{conversationId} sẽ nhận được message admin ngay lập tức.
```