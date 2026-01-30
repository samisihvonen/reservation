# 🤖 AI Development Rules for Reservation Project

## Mode: AUTO-MERGE & AUTO-DEPLOY (Free Tier - Ollama)

### AI Can Do
- ✅ Write code and tests
- ✅ Create branches and PRs
- ✅ AUTO-MERGE if tests pass
- ✅ AUTO-DEPLOY to staging
- ✅ Self-evaluate code

### AI Cannot Do
- ❌ Modify authentication
- ❌ Change JWT/secrets
- ❌ Deploy to production
- ❌ Merge if tests fail

## Auto-Merge Requirements
```
✅ All tests pass (100%)
✅ Code follows patterns
✅ No security issues
✅ AI confidence ≥ 0.8
```

## Technical Stack
- Backend: Spring Boot 3.2.6
- Build: Maven only
- Database: PostgreSQL
- Tests: Mandatory (0% tolerance)
- AI: Ollama + Mistral (free, offline)

## Security Rules
```
BLOCKED:
- Authentication changes
- Password modifications
- JWT token changes
- Hardcoded secrets
- DROP TABLE operations
- DELETE without WHERE
```

## Testing Rules
```
Every change MUST have:
- Unit tests
- Integration tests (if endpoint)
- Tests must pass 100%
- Code coverage ≥ 80%
```

## Git Workflow
```
1. Feature branches only (feature/*, fix/*, docs/*)
2. Never commit to main directly
3. PR requires:
   - ✅ All tests passing
   - ✅ AI review confidence ≥ 0.8
   - ✅ No breaking changes
4. Auto-merge if conditions met
5. Delete branch after merge
6. Squash commits on merge
```

## Version
- Last Updated: 2026-01-29
- Mode: AGGRESSIVE (auto-merge + auto-deploy)
- AI Provider: Ollama + Mistral
- Status: ACTIVE