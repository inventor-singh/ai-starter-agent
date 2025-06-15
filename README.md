# 🚀 AI Starter Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0-blue.svg)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Spring Boot starter project for learning **Agentic AI Workflows**. Features 5 fundamental patterns with interactive examples and working code implementations.

## 🚀 Quick Start

1. **Clone and run:**
   ```bash
   git clone <repository-url>
   cd ai-starter
   export OPENAI_API_KEY=your-key-here
   mvn spring-boot:run
   ```

2. **Open your browser:**
   - Main app: http://localhost:8080/chat
   - Learn workflows: http://localhost:8080/agentic-workflows

3. **Login:** `admin` / `password`

## 🧠 The 5 Agentic AI Patterns

This project teaches you the fundamental patterns for building AI systems:

### 1. 🔗 Chain Workflow
Sequential processing where each step builds on the previous output.
```java
public String chain(String input) {
    String result = input;
    for (String prompt : prompts) {
        result = chatClient.prompt(prompt + result).call().content();
    }
    return result;
}
```

### 2. ⚡ Parallelization
Process multiple tasks concurrently and aggregate results.
```java
public List<String> parallel(List<String> inputs) {
    return inputs.parallelStream()
        .map(input -> chatClient.prompt(input).call().content())
        .collect(Collectors.toList());
}
```

### 3. 🎯 Routing
Route different inputs to specialized handlers.
```java
public String route(String input, Map<String, String> routes) {
    String category = classify(input);
    String specializedPrompt = routes.get(category);
    return chatClient.prompt(specializedPrompt + input).call().content();
}
```

### 4. 🎭 Orchestrator-Workers
Central coordinator manages specialized workers.
```java
public WorkerResponse orchestrate(String task) {
    List<String> subtasks = orchestrator.breakDown(task);
    List<String> results = workers.processInParallel(subtasks);
    return combine(results);
}
```

### 5. 🔄 Evaluator-Optimizer
Iterative improvement through evaluation feedback.
```java
public String optimize(String task) {
    String solution = generate(task);
    for (int i = 0; i < maxIterations; i++) {
        Evaluation eval = evaluate(solution);
        if (eval.isGood()) break;
        solution = refine(solution, eval.feedback());
    }
    return solution;
}
```
