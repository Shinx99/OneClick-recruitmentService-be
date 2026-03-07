# Flyway Migration Script Generator

This script automates the creation of Flyway SQL migration files within the Milk Tea E-commerce Spring Boot project. It standardizes migration file naming and template content while opening the new migration file directly in your preferred IDE (IntelliJ IDEA or VS Code).

----

## 1. Make the Script Executable
Set executable permissions to the script:

```bash
chmod +x scripts/create-migration.sh
```
---
## 2. Usage Guide


## Linux / macOS
- Run the script directly from the project root:

```bash
./scripts/create-migration.sh
```
---
## Windows
### Option 1: Using Git Bash (Recommended)

- Install Git for Windows from git-scm.com (includes Git Bash)
- Right-click on project folder → Git Bash Here

Run the script:

```bash
./scripts/create-migration.sh
```

### Option 2: Configure IntelliJ IDEA Terminal

- Open IntelliJ IDEA → File → Settings (or Ctrl+Alt+S)
- Navigate to Tools → Terminal

Set Shell path to:

```text
"C:\Program Files\Git\bin\bash.exe"
```
- Click OK and restart terminal tab
- Open terminal in IntelliJ (Alt+F12) and run:

```bash
./scripts/create-migration.sh
```

**Note:** This IntelliJ configuration applies to all projects globally.

#### Troubleshooting on Windows:
If you encounter "bad interpreter" error, fix line endings:

```bash
dos2unix scripts/create-migration.sh
# or
sed -i 's/\r$//' scripts/create-migration.sh
```
---
## 3. Usage Examples

- Run the script and follow the prompts to generate your migration files:

Example 1: Create Products Table

```bash
./scripts/create-migration.sh

# Input:
# 📦 Feature name: products
# 📝 Description: create_table
# Output: V20250924_013800__products__create_table.sql
```

Example 2: Add User Authentication Columns

```bash
./scripts/create-migration.sh

# Input:
# 📦 Feature name: users
# 📝 Description: add_auth_columns
# Output: V20250924_013845__users__add_auth_columns.sql
```
---
*End of migration Documentation!*
---
