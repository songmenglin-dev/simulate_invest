# CLAUDE.md

## Behavioral Guidelines

These guidelines bias toward caution over speed. For trivial tasks, use judgment.

### 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

### 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

### 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

### 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

## 项目技术栈

- **后端**: Spring Boot 2.7.x + MyBatis-Plus + Nacos + MySQL + Redis
- **前端**: Vue 3 + TypeScript + Vite + Tailwind CSS
- **数据库**: MySQL `stock_investment` @ 172.20.10.6:3306
- **测试**: JUnit 5 + Mockito + MockitoExtension
- **API 代理**: 前端通过 Vite proxy（`vite.config.ts`）直连后端服务，不依赖 Spring Cloud Gateway

## 提交规范

- **小步提交**: 每完成一个小功能并验证通过后，立即 `git commit`，不要攒很多功能一次提交。
- **Conventional Commits**: 使用 `feat:` / `fix:` / `refactor:` / `test:` / `docs:` 前缀。
- **提交前验证**: 确保前后端服务都在运行，并通过 agent-browser 对前端页面截图验证后再提交。

## 服务管理

- 后端服务用 Maven 启动：`mvn spring-boot:run -pl <service> -DskipTests`
- 前端用 Vite 启动：`npx vite --host 0.0.0.0`
- **每次后端代码改动后，必须重启对应服务**，然后通过 curl 或前端页面验证 API 是否正常。

## UI 验证

- **必须用 `agent-browser` CLI 验证前端页面**，不能只看代码。
- 验证流程：打开页面 → `snapshot` 确认元素 → 点击交互 → `screenshot` 保存到 `/tmp/`。
- 涉及多个页面时，每个页面都要截图验证。

## 代码质量

- **TDD**: 新功能先写测试（JUnit 5 + Mockito），再写实现，确保测试通过。
- **Code Review**: 写完代码后使用 code-reviewer agent 审查。
- 禁止硬编码密钥，敏感配置走环境变量或配置文件。
