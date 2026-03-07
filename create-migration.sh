#!/bin/bash
# create-migration.sh - Enhanced version

echo "🗄️  Flyway Migration Generator"
echo "================================"

# Check if migration directory exists
MIGRATION_DIR="src/main/resources/db/migration"
if [ ! -d "$MIGRATION_DIR" ]; then
    echo "❌ Migration directory not found: $MIGRATION_DIR"
    echo "Are you in the root of your Spring Boot project?"
    exit 1
fi

# Get user input
read -p "📦 Feature name (e.g. products, users): " feature
read -p "📝 Description (e.g. create_table, add_columns): " description

# Validate input
if [ -z "$feature" ] || [ -z "$description" ]; then
    echo "❌ Feature name and description are required!"
    exit 1
fi

# Generate filename
timestamp=$(date +"%Y%m%d_%H%M%S")
filename="V${timestamp}__${feature}__${description}.sql"
filepath="${MIGRATION_DIR}/${filename}"

# Create file with basic template
cat > "$filepath" << EOF
-- Migration: ${feature} - ${description}
-- Created: $(date)
-- Author: $(whoami)

-- Add your SQL statements below:

EOF

echo "✅ Created: ${filename}"
echo "📂 Location: ${filepath}"

# Open in editor (choose one)
if command -v idea &> /dev/null; then
    idea "$filepath"
    echo "Opened in IntelliJ"
elif command -v code &> /dev/null; then
    code "$filepath"
    echo "🚀 Opened in VS Code"
else
    echo "💡 Open manually: $filepath"
fi
