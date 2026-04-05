-- Migration: ai_chat - create_table
-- Created: Thu Apr  2 04:18:39 PM +07 2026
-- Author: mango

-- Add your SQL statements below:

-- =========================================================
-- MIGRATION: AI Chat System - Full Schema (Phase 1 + Realtime)
-- =========================================================

-- =========================================================
-- 1. AI_CHAT_CONVERSATION
-- =========================================================
CREATE TABLE ai_chat_conversation (
    conversation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- User info
    user_id UUID NOT NULL,
    user_type VARCHAR(20) NOT NULL,

    -- Conversation status
    status VARCHAR(20) NOT NULL DEFAULT 'open',
    assigned_admin_id UUID,
    channel VARCHAR(20) NOT NULL DEFAULT 'websocket',

    -- Realtime tracking
    user_last_seen_at TIMESTAMPTZ,
    admin_last_seen_at TIMESTAMPTZ,
    user_unread_count INTEGER NOT NULL DEFAULT 0,
    admin_unread_count INTEGER NOT NULL DEFAULT 0,

    -- Timestamps
    last_message_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT ai_chat_conversation_user_type_check
        CHECK (user_type IN ('candidate', 'recruiter', 'admin')),
    CONSTRAINT ai_chat_conversation_status_check
        CHECK (status IN ('open', 'handoff', 'in_progress', 'closed')),
    CONSTRAINT ai_chat_conversation_channel_check
        CHECK (channel IN ('websocket', 'http'))
);

-- =========================================================
-- 2. AI_CHAT_MESSAGE
-- =========================================================
CREATE TABLE ai_chat_message (
    message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Relationship
    conversation_id UUID NOT NULL,

    -- Sender info
    sender_id UUID,
    sender_type VARCHAR(20) NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'text',

    -- Content
    content TEXT,
    metadata JSONB,

    -- Realtime tracking
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,

    -- Timestamp
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign key
    CONSTRAINT fk_ai_chat_message_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES ai_chat_conversation(conversation_id)
        ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT ai_chat_message_sender_type_check
        CHECK (sender_type IN ('candidate', 'recruiter', 'ai', 'admin', 'system')),
    CONSTRAINT ai_chat_message_message_type_check
        CHECK (message_type IN ('text', 'image', 'file')),
    CONSTRAINT ai_chat_message_content_check
        CHECK (
            (message_type = 'text' AND content IS NOT NULL AND btrim(content) <> '')
            OR
            (message_type IN ('image', 'file'))
        )
);

-- =========================================================
-- 3. INDEXES for performance
-- =========================================================

-- Basic indexes for ai_chat_conversation
CREATE INDEX idx_ai_chat_conversation_user
    ON ai_chat_conversation(user_id, user_type);

CREATE INDEX idx_ai_chat_conversation_status
    ON ai_chat_conversation(status);

CREATE INDEX idx_ai_chat_conversation_last_message_at
    ON ai_chat_conversation(last_message_at DESC);

-- Realtime indexes for ai_chat_conversation
CREATE INDEX idx_ai_chat_conversation_admin_assigned
    ON ai_chat_conversation(assigned_admin_id, status, last_message_at DESC)
    WHERE status IN ('in_progress', 'handoff');

CREATE INDEX idx_ai_chat_conversation_user_realtime
    ON ai_chat_conversation(user_id, status, user_unread_count)
    WHERE user_unread_count > 0;

CREATE INDEX idx_ai_chat_conversation_admin_unread
    ON ai_chat_conversation(assigned_admin_id, admin_unread_count)
    WHERE admin_unread_count > 0;

-- Basic indexes for ai_chat_message
CREATE INDEX idx_ai_chat_message_conversation_created
    ON ai_chat_message(conversation_id, created_at);

CREATE INDEX idx_ai_chat_message_sender_type
    ON ai_chat_message(sender_type);

-- Realtime indexes for ai_chat_message
CREATE INDEX idx_ai_chat_message_unread
    ON ai_chat_message(conversation_id, is_read, sender_type)
    WHERE is_read = false;

CREATE INDEX idx_ai_chat_message_realtime_delivery
    ON ai_chat_message(conversation_id, created_at, delivered_at, is_read);

-- =========================================================
-- 4. COMMENTS for documentation
-- =========================================================
COMMENT ON TABLE ai_chat_conversation IS 'Chat conversations between users and AI/admin';
COMMENT ON COLUMN ai_chat_conversation.status IS 'open: AI chat, handoff: waiting admin, in_progress: admin assigned, closed: finished';
COMMENT ON COLUMN ai_chat_conversation.user_unread_count IS 'Number of unread messages for user';
COMMENT ON COLUMN ai_chat_conversation.admin_unread_count IS 'Number of unread messages for admin';

COMMENT ON TABLE ai_chat_message IS 'Individual messages in chat conversations';
COMMENT ON COLUMN ai_chat_message.sender_type IS 'candidate, recruiter, ai, admin, system';
COMMENT ON COLUMN ai_chat_message.message_type IS 'text, image, file';
COMMENT ON COLUMN ai_chat_message.is_read IS 'Whether message has been read by recipient';
COMMENT ON COLUMN ai_chat_message.delivered_at IS 'When message was delivered to recipient';

-- =========================================================
-- 5. Optional: Update existing rows (if migration from old schema)
-- =========================================================
-- Uncomment if migrating from existing data
-- UPDATE ai_chat_conversation SET user_unread_count = 0 WHERE user_unread_count IS NULL;
-- UPDATE ai_chat_conversation SET admin_unread_count = 0 WHERE admin_unread_count IS NULL;
-- UPDATE ai_chat_message SET is_read = false WHERE is_read IS NULL;