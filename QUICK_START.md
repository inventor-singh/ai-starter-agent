# 🚀 AI Starter Platform - Quick Start Guide

## Getting Started in 5 Minutes

### 1. **Start the Application**
```bash
mvn spring-boot:run
```

### 2. **Open Your Browser**
Navigate to: `http://localhost:8080`

### 3. **Login**
- **Admin User**: `admin` / `password` (Full access + admin dashboard)
- **Regular User**: `user` / `password` (Chat access only)

### 4. **Try the Agentic Patterns**

#### 🔗 Chain Workflow (Sequential Processing)
```
/chain How do I set up my new development environment?
```
Perfect for step-by-step processes like troubleshooting, onboarding, or tutorials.

#### ⚡ Parallel Workflow (Concurrent Analysis)
```
/parallel Analyze customer satisfaction across different services
```
Great for market analysis, comparisons, or multi-aspect evaluations.

#### 🎯 Routing Workflow (Smart Classification)
```
/route I have a billing issue with my account
```
Automatically routes to billing, technical, sales, or account specialists.

#### 🎭 Orchestrator Workflow (Complex Coordination)
```
/orchestrate Plan a comprehensive customer retention strategy
```
Handles complex business processes with multiple specialized workers.

#### 🔄 Evaluator Workflow (Iterative Improvement)
```
/optimize Write a professional apology email for service outages
```
Refines and improves content through multiple optimization cycles.

### 5. **Natural Language Commands**
You don't need to use `/` commands! Just ask naturally:
- "How do I troubleshoot my internet?" → Routes to Chain workflow
- "Compare our TV packages" → Routes to Parallel workflow  
- "I have a technical problem" → Routes to Routing workflow

### 6. **Get Help Anytime**
```
/help
```
Shows all available patterns with examples.

```
/patterns
```
Lists all workflow patterns and descriptions.

## 🛠️ For Developers

### Architecture Overview
```
Web Interface → Agent Orchestrator → Workflow Patterns → AI Models
     ↓                 ↓                     ↓              ↓
 Thymeleaf        Spring Components     ChatClient    OpenAI API
     ↓                 ↓                     ↓              
  Security      → Neo4j (Memory)    → PGVector (Knowledge)
```

### Adding New Patterns
1. Implement `AgentWorkflow` interface
2. Add `@Component` annotation
3. The orchestrator automatically discovers and registers it

### Configuration
Edit `application.properties`:
```properties
# Workflow limits
agent.chain.max-steps=5
agent.parallel.max-workers=4
agent.evaluator.max-iterations=3
```

## 🎯 Production Checklist

- [ ] Set real OpenAI API key in `OPENAI_API_KEY` environment variable
- [ ] Configure PostgreSQL connection
- [ ] Set up Neo4j database  
- [ ] Update security credentials
- [ ] Configure logging levels
- [ ] Set up monitoring

## 📊 Admin Features (Admin Users Only)

Access the admin dashboard at: `http://localhost:8080/admin`

- View workflow analytics
- Monitor system status
- Test all patterns
- Export usage data

---

**Ready to build amazing AI agents? Start chatting! 🤖**