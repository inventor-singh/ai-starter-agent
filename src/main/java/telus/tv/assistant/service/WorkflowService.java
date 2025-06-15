package telus.tv.assistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service implementing the 5 Agentic AI Workflow patterns from Anthropic's research
 * Each pattern demonstrates different approaches to orchestrating LLM interactions
 */
@Service
public class WorkflowService {

    private final ChatClient chatClient;
    private final ExecutorService executorService;

    public WorkflowService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.executorService = Executors.newFixedThreadPool(8);
    }

    /**
     * Chain Workflow - Sequential processing where each step builds on the previous
     * Perfect for tasks requiring clear sequential steps with dependencies
     */
    public String executeChainWorkflow(String input) {
        String[] prompts = {
            "Analyze the main themes and key points in this content:",
            "Based on the analysis above, identify the target audience and their needs:",
            "Considering the themes and audience, suggest three specific improvements:",
            "Create a final executive summary with actionable recommendations:"
        };

        String response = input;
        for (String prompt : prompts) {
            String fullPrompt = prompt + "\n\nContent to process:\n" + response;
            response = chatClient.prompt(fullPrompt).call().content();
        }
        return response;
    }

    /**
     * Parallelization Workflow - Concurrent processing with result aggregation
     * Useful for processing large volumes of independent items or getting multiple perspectives
     */
    public List<String> executeParallelWorkflow(String input) {
        List<String> perspectives = Arrays.asList(
            "From a technical perspective, analyze this topic:",
            "From a business perspective, analyze this topic:",
            "From a user experience perspective, analyze this topic:",
            "From a risk management perspective, analyze this topic:"
        );

        // Process all perspectives in parallel
        List<CompletableFuture<String>> futures = perspectives.stream()
            .map(perspective -> CompletableFuture.supplyAsync(() -> {
                String fullPrompt = perspective + "\n\nTopic: " + input;
                return chatClient.prompt(fullPrompt).call().content();
            }, executorService))
            .collect(Collectors.toList());

        // Wait for all results and collect them
        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }

    /**
     * Routing Workflow - Intelligent task distribution to specialized handlers
     * Uses classification to route inputs to the most appropriate specialized processor
     */
    public String executeRoutingWorkflow(String input) {
        // Define specialized routes
        Map<String, String> routes = Map.of(
            "technical", "You are a technical expert. Provide detailed technical analysis and solutions for:",
            "business", "You are a business analyst. Provide strategic business insights and recommendations for:",
            "creative", "You are a creative professional. Provide innovative and creative solutions for:",
            "general", "You are a knowledgeable assistant. Provide comprehensive analysis for:"
        );

        // First, classify the input
        String classificationPrompt = String.format(
            "Classify the following input into one of these categories: %s\n\n" +
            "Input: %s\n\n" +
            "Respond with only the category name (one word):",
            String.join(", ", routes.keySet()), input
        );

        String category = chatClient.prompt(classificationPrompt).call().content().toLowerCase().trim();

        // Route to the appropriate specialized handler
        String specializedPrompt = routes.getOrDefault(category, routes.get("general"));
        return chatClient.prompt(specializedPrompt + "\n\n" + input).call().content();
    }

    /**
     * Orchestrator-Workers Workflow - Central coordination with specialized workers
     * Demonstrates more complex agent-like behavior while maintaining control
     */
    public Map<String, Object> executeOrchestratorWorkersWorkflow(String input) {
        // Step 1: Orchestrator analyzes the task and determines subtasks
        String orchestratorPrompt = String.format(
            "You are a project orchestrator. Break down this complex task into 3-4 specific, actionable subtasks:\n\n" +
            "Task: %s\n\n" +
            "For each subtask, provide:\n" +
            "1. A clear description\n" +
            "2. The type of expertise needed\n" +
            "3. Expected deliverable\n\n" +
            "Format as a numbered list.", input
        );

        String orchestratorAnalysis = chatClient.prompt(orchestratorPrompt).call().content();

        // Step 2: Extract subtasks (simplified parsing)
        List<String> subtasks = Arrays.stream(orchestratorAnalysis.split("\n"))
            .filter(line -> line.matches("^\\d+\\..*"))
            .map(line -> line.replaceFirst("^\\d+\\.", "").trim())
            .collect(Collectors.toList());

        // Step 3: Workers process subtasks in parallel
        List<CompletableFuture<String>> workerTasks = subtasks.stream()
            .map(subtask -> CompletableFuture.supplyAsync(() -> {
                String workerPrompt = "You are a specialized worker. Complete this specific subtask with detailed output:\n\n" + subtask;
                return chatClient.prompt(workerPrompt).call().content();
            }, executorService))
            .collect(Collectors.toList());

        List<String> workerResults = workerTasks.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        // Step 4: Combine results
        Map<String, Object> result = new HashMap<>();
        result.put("orchestratorAnalysis", orchestratorAnalysis);
        result.put("subtasks", subtasks);
        result.put("workerResults", workerResults);
        result.put("totalWorkers", workerResults.size());

        return result;
    }

    /**
     * Evaluator-Optimizer Workflow - Iterative refinement through evaluation
     * Implements dual-LLM process for continuous improvement
     */
    public Map<String, Object> executeEvaluatorOptimizerWorkflow(String input) {
        List<String> chainOfThought = new ArrayList<>();
        int maxIterations = 3;

        // Generate initial solution
        String generatorPrompt = "Generate a comprehensive solution for this task:\n\n" + input;
        String solution = chatClient.prompt(generatorPrompt).call().content();
        chainOfThought.add("Initial solution generated");

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            // Evaluate the current solution
            String evaluatorPrompt = String.format(
                "Evaluate this solution for the given task. Provide:\n" +
                "1. Score (1-10)\n" +
                "2. Strengths\n" +
                "3. Areas for improvement\n" +
                "4. Specific suggestions\n\n" +
                "Task: %s\n\n" +
                "Solution: %s", input, solution
            );

            String evaluation = chatClient.prompt(evaluatorPrompt).call().content();
            chainOfThought.add(String.format("Iteration %d evaluation: %s", iteration, evaluation));

            // Check if solution is good enough (simplified scoring)
            if (evaluation.contains("10/10") || evaluation.contains("Score: 10") || 
                evaluation.toLowerCase().contains("excellent") && iteration > 1) {
                chainOfThought.add("Solution deemed satisfactory, stopping iterations");
                break;
            }

            // Refine the solution based on feedback
            if (iteration < maxIterations) {
                String refinerPrompt = String.format(
                    "Improve this solution based on the evaluation feedback:\n\n" +
                    "Original task: %s\n\n" +
                    "Current solution: %s\n\n" +
                    "Evaluation feedback: %s\n\n" +
                    "Provide an improved version:", input, solution, evaluation
                );

                solution = chatClient.prompt(refinerPrompt).call().content();
                chainOfThought.add(String.format("Solution refined in iteration %d", iteration));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("finalSolution", solution);
        result.put("chainOfThought", chainOfThought);
        result.put("iterations", Math.min(chainOfThought.size() / 2, maxIterations));

        return result;
    }

    /**
     * Get information about all available workflow patterns
     */
    public List<Map<String, Object>> getWorkflowPatterns() {
        return Arrays.asList(
            createPatternInfo("chain", "Chain Workflow", "Sequential processing with step dependencies", "Simple", 
                Arrays.asList("Sequential tasks", "Higher accuracy", "Step dependencies")),
            createPatternInfo("parallelization", "Parallelization Workflow", "Concurrent processing with aggregation", "Medium",
                Arrays.asList("Independent tasks", "Performance", "Multiple perspectives")),
            createPatternInfo("routing", "Routing Workflow", "Intelligent task distribution", "Medium",
                Arrays.asList("Input classification", "Specialized handling", "Scalable")),
            createPatternInfo("orchestrator-workers", "Orchestrator-Workers", "Central coordination with workers", "Complex",
                Arrays.asList("Complex tasks", "Coordination", "Adaptive problem solving")),
            createPatternInfo("evaluator-optimizer", "Evaluator-Optimizer", "Iterative refinement", "Complex",
                Arrays.asList("Quality improvement", "Feedback loops", "Iterative refinement"))
        );
    }

    /**
     * Get detailed information about a specific workflow pattern
     */
    public Map<String, Object> getWorkflowPattern(String patternName) {
        Map<String, Map<String, Object>> patterns = Map.of(
            "chain", createDetailedPatternInfo("chain", "Chain Workflow", 
                "Break down complex tasks into sequential steps where each step builds on the previous output",
                "Perfect for tasks requiring clear sequential processing with dependencies between steps"),
            "parallelization", createDetailedPatternInfo("parallelization", "Parallelization Workflow",
                "Process multiple independent tasks concurrently and aggregate results for efficiency",
                "Ideal for processing large volumes of similar items or getting multiple perspectives"),
            "routing", createDetailedPatternInfo("routing", "Routing Workflow",
                "Intelligently route different types of inputs to specialized handlers based on classification",
                "Best for complex systems with distinct categories requiring specialized processing"),
            "orchestrator-workers", createDetailedPatternInfo("orchestrator-workers", "Orchestrator-Workers",
                "Central orchestrator analyzes tasks and coordinates specialized workers for complex problems",
                "Suitable for adaptive problems requiring coordination of multiple specialized capabilities"),
            "evaluator-optimizer", createDetailedPatternInfo("evaluator-optimizer", "Evaluator-Optimizer",
                "Implement iterative refinement with dual LLMs for continuous improvement through evaluation",
                "Excellent for tasks requiring high quality output through multiple rounds of refinement")
        );

        if (!patterns.containsKey(patternName)) {
            throw new IllegalArgumentException("Unknown workflow pattern: " + patternName);
        }

        return patterns.get(patternName);
    }

    private Map<String, Object> createPatternInfo(String id, String name, String description, 
                                                 String complexity, List<String> useCases) {
        Map<String, Object> pattern = new HashMap<>();
        pattern.put("id", id);
        pattern.put("name", name);
        pattern.put("description", description);
        pattern.put("complexity", complexity);
        pattern.put("useCases", useCases);
        return pattern;
    }

    private Map<String, Object> createDetailedPatternInfo(String id, String name, String description, String whenToUse) {
        Map<String, Object> pattern = new HashMap<>();
        pattern.put("id", id);
        pattern.put("name", name);
        pattern.put("description", description);
        pattern.put("whenToUse", whenToUse);
        pattern.put("implemented", true);
        return pattern;
    }
}
