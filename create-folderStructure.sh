#!/bin/bash

# Chạy script này từ thư mục gốc project (có .idea, src, pom.xml, ...)

PROJECT_ROOT="."
MAIN_SRC="$PROJECT_ROOT/src/main/java/com/onceClick/recruitmentService"
TEST_SRC="$PROJECT_ROOT/src/test/java/com/onceClick/recruitmentService"
RESOURCES="$PROJECT_ROOT/src/main/resources"

echo "🚀 Tạo cấu trúc folder + .gitkeep (không tạo file .java)..."

# MAIN
mkdir -p "$MAIN_SRC"

# SHARED
SHARED="$MAIN_SRC/shared"
mkdir -p "$SHARED/config"
mkdir -p "$SHARED/constant"
mkdir -p "$SHARED/dto"
mkdir -p "$SHARED/exception"
mkdir -p "$SHARED/persistence/entity"
mkdir -p "$SHARED/persistence/repository"
mkdir -p "$SHARED/util"

# FEATURES
FEATURES="$MAIN_SRC/features"

mkdir -p "$FEATURES/employer/dto"
mkdir -p "$FEATURES/employer/validator"

mkdir -p "$FEATURES/company/dto"
mkdir -p "$FEATURES/company/event"

mkdir -p "$FEATURES/job/dto"
mkdir -p "$FEATURES/job/specification"

mkdir -p "$FEATURES/candidate/dto"
mkdir -p "$FEATURES/candidate/event"

mkdir -p "$FEATURES/resume/dto"
mkdir -p "$FEATURES/resume/service"

mkdir -p "$FEATURES/statistic/candidate/dto"
mkdir -p "$FEATURES/statistic/job/dto"
mkdir -p "$FEATURES/statistic/resume/dto"

# INFRASTRUCTURE
INFRA="$MAIN_SRC/infrastructure"
mkdir -p "$INFRA/client/auth/dto"
mkdir -p "$INFRA/client/notification/dto"
mkdir -p "$INFRA/messaging/producer"
mkdir -p "$INFRA/messaging/consumer"
mkdir -p "$INFRA/security"

# RESOURCES
mkdir -p "$RESOURCES/db/migration"
mkdir -p "$RESOURCES/static"

# TEST
mkdir -p "$TEST_SRC/features/employer"
mkdir -p "$TEST_SRC/features/job"
mkdir -p "$TEST_SRC/features/statistic"
mkdir -p "$TEST_SRC/shared"

# Tạo .gitkeep cho mọi thư mục trống
find "$PROJECT_ROOT/src" -type d -empty -exec touch {}/.gitkeep \;

echo "✅ Done. Folder structure + .gitkeep đã được tạo."
