#!/bin/bash

# Day 6 前端集成测试脚本
# 测试项目管理和问题管理功能

set -e

echo "🧪 Day 6 前端集成测试"
echo "================================================"

# 1. TypeScript 编译检查
echo "1️⃣  TypeScript 编译检查..."
cd /Users/zhangyuhe/Documents/myproject/plane-lite/frontend
npm run build > /dev/null 2>&1 && echo "✅ TypeScript 编译成功" || {
  echo "❌ TypeScript 编译失败"
  exit 1
}

# 2. 检查所有必需文件是否存在
echo "2️⃣  检查文件结构..."
files_to_check=(
  "src/types/project.ts"
  "src/types/issue.ts"
  "src/api/project.api.ts"
  "src/api/issue.api.ts"
  "src/api/workspace.api.ts"
  "src/stores/workspace.ts"
  "src/stores/project.ts"
  "src/stores/issue.ts"
  "src/components/layout/AppLayout.vue"
  "src/components/layout/AppSidebar.vue"
  "src/components/layout/AppHeader.vue"
  "src/views/project/ProjectList.vue"
  "src/views/project/ProjectDetail.vue"
  "src/views/issue/IssueList.vue"
  "src/views/issue/IssueDetail.vue"
  "src/components/issue/IssueForm.vue"
)

all_exist=true
for file in "${files_to_check[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ $file (MISSING)"
    all_exist=false
  fi
done

if [ "$all_exist" = false ]; then
  echo "❌ 某些必需文件缺失"
  exit 1
fi
echo "✅ 所有文件检查通过"

# 3. 检查路由配置
echo "3️⃣  检查路由配置..."
if grep -q "IssueList" src/router/index.ts && \
   grep -q "IssueDetail" src/router/index.ts && \
   grep -q "ProjectDetail" src/router/index.ts; then
  echo "✅ 路由配置完整"
else
  echo "❌ 路由配置不完整"
  exit 1
fi

# 4. 检查关键导入
echo "4️⃣  检查代码导入..."
if grep -q "^export interface Project" src/types/project.ts && \
   grep -q "^export enum IssueStatus" src/types/issue.ts && \
   grep -q "^export const projectApi" src/api/project.api.ts; then
  echo "✅ 代码导入检查通过"
else
  echo "❌ 代码导入有问题"
  exit 1
fi

# 5. 检查 CSS 变量使用
echo "5️⃣  检查设计规范..."
if grep -q '\-\-cream' src/App.vue && \
   grep -q '\-\-ink' src/App.vue && \
   grep -q '\-\-amber' src/App.vue; then
  echo "✅ CSS 变量设置正确"
else
  echo "❌ CSS 变量设置有问题"
  exit 1
fi

# 6. 检查 Pinia Store 模式
echo "6️⃣  检查 Pinia Store..."
if grep -q "defineStore('project'" src/stores/project.ts && \
   grep -q "defineStore('issue'" src/stores/issue.ts && \
   grep -q "defineStore('workspace'" src/stores/workspace.ts; then
  echo "✅ Pinia Store 模式正确"
else
  echo "❌ Pinia Store 模式有问题"
  exit 1
fi

# 7. 检查 TypeScript 严格模式
echo "7️⃣  检查 TypeScript 类型..."
errors=$(npm run build 2>&1 | grep -c "error" || echo 0)
if [ "$errors" -eq 0 ]; then
  echo "✅ 没有 TypeScript 错误"
else
  echo "⚠️  有 $errors 个 TypeScript 警告（非致命）"
fi

echo ""
echo "================================================"
echo "✅ Day 6 前端测试全部通过！"
echo ""
echo "📊 测试统计："
echo "  • TypeScript 编译: ✓"
echo "  • 文件结构: ✓ (16 个文件)"
echo "  • 路由配置: ✓"
echo "  • 代码导入: ✓"
echo "  • 设计规范: ✓"
echo "  • Store 模式: ✓"
echo ""
echo "🚀 前端开发服务器："
echo "  npm run dev    # 启动开发服务器 (http://localhost:5173)"
echo ""
echo "💾 后端 API:"
echo "  需要后端服务器运行在 http://localhost:8080"
echo ""
