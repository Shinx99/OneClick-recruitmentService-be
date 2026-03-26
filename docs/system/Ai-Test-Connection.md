
```bash
curl -H "Authorization: Bearer sk-22436d003ab341e48019b1d26c70cb6b" \
-H "Content-Type: application/json" \
-d '{"model":"deepseek-chat","messages":[{"role":"user","content":"Hi"}]}' \
https://api.deepseek.com/v1/chat/completions | jq .
```