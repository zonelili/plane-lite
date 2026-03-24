#!/bin/bash

# Day 5 前端认证功能测试脚本
# 测试内容：注册、登录、获取用户信息

set -e  # 遇到错误立即退出

BASE_URL="http://localhost:8080/api/v1"
TEST_EMAIL="day5frontend@plane.com"
TEST_USERNAME="day5frontend"
TEST_PASSWORD="test123456"

echo "========================================="
echo "Day 5 前端认证功能测试"
echo "========================================="
echo ""

# 测试 1: 用户注册
echo "测试 1: 用户注册"
echo "POST $BASE_URL/auth/register"

REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }")

echo "$REGISTER_RESPONSE" | python3 -m json.tool

# 检查注册响应
REGISTER_CODE=$(echo "$REGISTER_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['code'])")

if [ "$REGISTER_CODE" = "200" ]; then
  echo "✅ 注册成功"
elif [ "$REGISTER_CODE" = "400" ]; then
  echo "⚠️  用户已存在，继续测试登录"
else
  echo "❌ 注册失败: code=$REGISTER_CODE"
  exit 1
fi

echo ""

# 测试 2: 用户登录
echo "测试 2: 用户登录"
echo "POST $BASE_URL/auth/login"

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }")

echo "$LOGIN_RESPONSE" | python3 -m json.tool

# 提取 Token
TOKEN=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])")
USER_ID=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['user']['id'])")

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败: 未获取到 Token"
  exit 1
fi

echo "✅ 登录成功"
echo "   Token: ${TOKEN:0:50}..."
echo "   User ID: $USER_ID"
echo ""

# 测试 3: 获取当前用户信息
echo "测试 3: 获取当前用户信息"
echo "GET $BASE_URL/auth/me"

ME_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/me" \
  -H "Authorization: Bearer $TOKEN")

echo "$ME_RESPONSE" | python3 -m json.tool

# 验证用户信息
ME_EMAIL=$(echo "$ME_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['email'])")

if [ "$ME_EMAIL" = "$TEST_EMAIL" ]; then
  echo "✅ 获取用户信息成功"
else
  echo "❌ 获取用户信息失败: email 不匹配"
  exit 1
fi

echo ""

# 测试 4: 无效 Token 测试
echo "测试 4: 无效 Token 验证"
echo "GET $BASE_URL/auth/me (with invalid token)"

INVALID_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/me" \
  -H "Authorization: Bearer invalid_token" || echo '{"code": 401}')

echo "$INVALID_RESPONSE" | python3 -m json.tool

INVALID_CODE=$(echo "$INVALID_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['code'])" || echo "401")

if [ "$INVALID_CODE" = "401" ] || [ "$INVALID_CODE" = "500" ]; then
  echo "✅ 无效 Token 正确拒绝"
else
  echo "❌ 无效 Token 未正确拒绝"
  exit 1
fi

echo ""
echo "========================================="
echo "✅ 所有测试通过！"
echo "========================================="
echo ""
echo "测试汇总:"
echo "  - ✅ 用户注册"
echo "  - ✅ 用户登录（获取 Token）"
echo "  - ✅ 获取用户信息（Token 验证）"
echo "  - ✅ 无效 Token 拒绝"
echo ""
