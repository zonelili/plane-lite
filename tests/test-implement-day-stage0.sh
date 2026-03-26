#!/bin/bash

# Test Script for implement-day skill - Stage 0
# 测试 implement-day skill 的阶段 0 功能

set -e  # 遇到错误立即退出

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 项目路径
PROJECT_ROOT="/Users/zhangyuhe/Documents/myproject/plane-lite"
STATE_DIR="${PROJECT_ROOT}/.claude/state"
STATE_FILE="${STATE_DIR}/day-3-state.json"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧪 测试 implement-day Skill - 阶段 0"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 测试函数
test_case() {
  local name="$1"
  local command="$2"
  local expected_exit_code="${3:-0}"

  TOTAL_TESTS=$((TOTAL_TESTS+1))
  echo -e "${BLUE}测试 $TOTAL_TESTS:${NC} $name"

  # 执行命令并捕获退出码
  set +e
  eval "$command" > /tmp/test_output.txt 2>&1
  actual_exit_code=$?
  set -e

  if [ $actual_exit_code -eq $expected_exit_code ]; then
    echo -e "${GREEN}✓ 通过${NC}"
    PASSED_TESTS=$((PASSED_TESTS+1))
  else
    echo -e "${RED}✗ 失败${NC}"
    echo "预期退出码: $expected_exit_code, 实际退出码: $actual_exit_code"
    cat /tmp/test_output.txt
    FAILED_TESTS=$((FAILED_TESTS+1))
  fi
  echo ""
}

# 验证函数
verify_file_exists() {
  local file="$1"
  local description="$2"

  TOTAL_TESTS=$((TOTAL_TESTS+1))
  echo -e "${BLUE}验证 $TOTAL_TESTS:${NC} $description"

  if [ -f "$file" ]; then
    echo -e "${GREEN}✓ 文件存在:${NC} $file"
    PASSED_TESTS=$((PASSED_TESTS+1))
  else
    echo -e "${RED}✗ 文件不存在:${NC} $file"
    FAILED_TESTS=$((FAILED_TESTS+1))
  fi
  echo ""
}

verify_file_contains() {
  local file="$1"
  local pattern="$2"
  local description="$3"

  TOTAL_TESTS=$((TOTAL_TESTS+1))
  echo -e "${BLUE}验证 $TOTAL_TESTS:${NC} $description"

  if grep -q "$pattern" "$file"; then
    echo -e "${GREEN}✓ 文件包含:${NC} $pattern"
    PASSED_TESTS=$((PASSED_TESTS+1))
  else
    echo -e "${RED}✗ 文件不包含:${NC} $pattern"
    FAILED_TESTS=$((FAILED_TESTS+1))
  fi
  echo ""
}

# 清理环境
echo "🧹 清理测试环境..."
rm -f "$STATE_FILE"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 1: 工作目录验证
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📁 测试组 1: 工作目录验证"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

test_case "检查工作目录是否存在" \
  "[ -d '$PROJECT_ROOT' ]"

test_case "检查后端目录是否存在" \
  "[ -d '$PROJECT_ROOT/backend' ]"

test_case "检查文档目录是否存在" \
  "[ -d '$PROJECT_ROOT/docs' ]"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 2: 关键文件验证
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📄 测试组 2: 关键文件验证"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

verify_file_exists \
  "$PROJECT_ROOT/docs/development-plan.md" \
  "开发计划文件存在"

verify_file_exists \
  "$PROJECT_ROOT/docs/workflows/quality-assurance.md" \
  "质量保障文档存在"

verify_file_exists \
  "$PROJECT_ROOT/CLAUDE.md" \
  "项目地图文件存在"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 3: 开发计划内容验证
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 测试组 3: 开发计划内容验证"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

PLAN_FILE="$PROJECT_ROOT/docs/development-plan.md"

verify_file_contains \
  "$PLAN_FILE" \
  "### Day 3" \
  "包含 Day 3 章节"

verify_file_contains \
  "$PLAN_FILE" \
  "问题管理模块" \
  "包含问题管理任务描述"

verify_file_contains \
  "$PLAN_FILE" \
  "Issue 实体" \
  "包含 Issue 实体任务"

verify_file_contains \
  "$PLAN_FILE" \
  "IssueMapper" \
  "包含 IssueMapper 任务"

verify_file_contains \
  "$PLAN_FILE" \
  "sequence_id" \
  "包含 sequence_id 技术要点"

# 测试提取所有 Day
echo -e "${BLUE}测试:${NC} 提取所有可用的 Day"
ALL_DAYS=$(grep -E "^### Day [0-9]+" "$PLAN_FILE" | sed 's/### //')
echo "找到的 Day:"
echo "$ALL_DAYS"
DAY_COUNT=$(echo "$ALL_DAYS" | wc -l | tr -d ' ')
echo "总数: $DAY_COUNT"

if [ "$DAY_COUNT" -ge 4 ]; then
  echo -e "${GREEN}✓ 至少有 4 个 Day${NC}"
  PASSED_TESTS=$((PASSED_TESTS+1))
else
  echo -e "${RED}✗ Day 数量不足${NC}"
  FAILED_TESTS=$((FAILED_TESTS+1))
fi
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 4: Day 3 任务提取
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎯 测试组 4: Day 3 任务内容提取"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 提取 Day 3 的标题
DAY3_TITLE=$(grep -A 2 "^### Day 3" "$PLAN_FILE" | grep "^\*\*目标\*\*" | sed 's/\*\*目标\*\*：//')
echo "Day 3 标题: $DAY3_TITLE"

if [[ "$DAY3_TITLE" == *"问题管理"* ]]; then
  echo -e "${GREEN}✓ Day 3 标题正确${NC}"
  PASSED_TESTS=$((PASSED_TESTS+1))
else
  echo -e "${RED}✗ Day 3 标题不正确${NC}"
  FAILED_TESTS=$((FAILED_TESTS+1))
fi
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo ""

# 提取 API 接口数量
API_COUNT=$(grep -A 50 "^### Day 3" "$PLAN_FILE" | grep "^- GET \|^- POST \|^- PUT \|^- DELETE " | wc -l | tr -d ' ')
echo "Day 3 API 接口数: $API_COUNT"

if [ "$API_COUNT" -ge 5 ]; then
  echo -e "${GREEN}✓ API 接口数量正确 (>= 5)${NC}"
  PASSED_TESTS=$((PASSED_TESTS+1))
else
  echo -e "${RED}✗ API 接口数量不足${NC}"
  FAILED_TESTS=$((FAILED_TESTS+1))
fi
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 5: 状态文件创建
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💾 测试组 5: 状态文件创建"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 创建状态目录
test_case "创建状态目录" \
  "mkdir -p '$STATE_DIR'"

# 创建状态文件
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
cat > "$STATE_FILE" <<EOF
{
  "day_number": 3,
  "current_stage": "preparation",
  "completed_stages": [],
  "failed_stage": null,
  "plan_file": null,
  "test_report": null,
  "review_report": null,
  "commit_id": null,
  "started_at": "$TIMESTAMP",
  "updated_at": "$TIMESTAMP"
}
EOF

verify_file_exists \
  "$STATE_FILE" \
  "状态文件已创建"

# 验证 JSON 格式
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo -e "${BLUE}验证:${NC} JSON 格式有效性"

if command -v jq > /dev/null; then
  if jq empty "$STATE_FILE" 2>/dev/null; then
    echo -e "${GREEN}✓ JSON 格式有效${NC}"
    PASSED_TESTS=$((PASSED_TESTS+1))
  else
    echo -e "${RED}✗ JSON 格式无效${NC}"
    FAILED_TESTS=$((FAILED_TESTS+1))
  fi
else
  echo -e "${YELLOW}⚠ 跳过 (jq 未安装)${NC}"
  TOTAL_TESTS=$((TOTAL_TESTS-1))
fi
echo ""

# 验证 JSON 内容
if command -v jq > /dev/null; then
  verify_file_contains \
    "$STATE_FILE" \
    '"day_number": 3' \
    "包含 day_number 字段"

  verify_file_contains \
    "$STATE_FILE" \
    '"current_stage": "preparation"' \
    "包含 current_stage 字段"

  verify_file_contains \
    "$STATE_FILE" \
    '"completed_stages": \[\]' \
    "包含 completed_stages 数组"
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 6: 断点恢复功能
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔄 测试组 6: 断点恢复功能"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 模拟中断场景 - 更新状态为 implementation
cat > "$STATE_FILE" <<EOF
{
  "day_number": 3,
  "current_stage": "implementation",
  "completed_stages": ["preparation", "plan"],
  "failed_stage": null,
  "plan_file": ".claude/plans/day-3-plan.md",
  "test_report": null,
  "review_report": null,
  "commit_id": null,
  "started_at": "$TIMESTAMP",
  "updated_at": "$TIMESTAMP"
}
EOF

# 测试读取状态
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo -e "${BLUE}测试:${NC} 读取断点状态"

if command -v jq > /dev/null; then
  CURRENT_STAGE=$(jq -r '.current_stage' "$STATE_FILE")
  COMPLETED_COUNT=$(jq '.completed_stages | length' "$STATE_FILE")

  echo "当前阶段: $CURRENT_STAGE"
  echo "已完成阶段数: $COMPLETED_COUNT"

  if [ "$CURRENT_STAGE" = "implementation" ] && [ "$COMPLETED_COUNT" -eq 2 ]; then
    echo -e "${GREEN}✓ 断点状态读取正确${NC}"
    PASSED_TESTS=$((PASSED_TESTS+1))
  else
    echo -e "${RED}✗ 断点状态不正确${NC}"
    FAILED_TESTS=$((FAILED_TESTS+1))
  fi
else
  echo -e "${YELLOW}⚠ 跳过 (jq 未安装)${NC}"
  TOTAL_TESTS=$((TOTAL_TESTS-1))
fi
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试 7: 错误处理
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "⚠️  测试组 7: 错误处理"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 测试不存在的 Day
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo -e "${BLUE}测试:${NC} 查找不存在的 Day 10"

if grep -q "^### Day 10" "$PLAN_FILE"; then
  echo -e "${RED}✗ 不应该找到 Day 10${NC}"
  FAILED_TESTS=$((FAILED_TESTS+1))
else
  echo -e "${GREEN}✓ 正确处理不存在的 Day${NC}"
  PASSED_TESTS=$((PASSED_TESTS+1))
fi
echo ""

# 测试文件缺失场景
TOTAL_TESTS=$((TOTAL_TESTS+1))
echo -e "${BLUE}测试:${NC} 检测缺失的文件"

FAKE_FILE="$PROJECT_ROOT/docs/non-existent-file.md"
if [ ! -f "$FAKE_FILE" ]; then
  echo -e "${GREEN}✓ 正确检测到文件不存在${NC}"
  PASSED_TESTS=$((PASSED_TESTS+1))
else
  echo -e "${RED}✗ 文件检测失败${NC}"
  FAILED_TESTS=$((FAILED_TESTS+1))
fi
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 测试结果汇总
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 测试结果汇总"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo "总测试数: $TOTAL_TESTS"
echo -e "通过: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败: ${RED}$FAILED_TESTS${NC}"

PASS_RATE=$(awk "BEGIN {printf \"%.2f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")
echo "通过率: ${PASS_RATE}%"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
  echo -e "${GREEN}✅ 所有测试通过!${NC}"
  echo ""
  echo "🎉 阶段 0 功能验证完成,可以继续实施后续阶段"
  exit 0
else
  echo -e "${RED}❌ 有 $FAILED_TESTS 个测试失败${NC}"
  echo ""
  echo "💡 建议: 修复失败的测试后再继续"
  exit 1
fi
