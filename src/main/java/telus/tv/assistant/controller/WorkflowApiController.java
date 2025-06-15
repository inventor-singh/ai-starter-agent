package telus.tv.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telus.tv.assistant.service.WorkflowService;
import telus.tv.assistant.model.WorkflowRequest;
import telus.tv.assistant.model.WorkflowResponse;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for Agentic AI Workflow demonstrations
 * Provides endpoints for testing and learning different workflow patterns
 */
@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowApiController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * Chain Workflow - Sequential processing where each step builds on the previous
     */
    @PostMapping("/chain")
    public ResponseEntity<WorkflowResponse> chainWorkflow(@RequestBody WorkflowRequest request) {
        try {
            String result = workflowService.executeChainWorkflow(request.getInput());
            return ResponseEntity.ok(new WorkflowResponse(result, "chain", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new WorkflowResponse("Error: " + e.getMessage(), "chain", false));
        }
    }

    /**
     * Parallelization Workflow - Concurrent processing with result aggregation
     */
    @PostMapping("/parallel")
    public ResponseEntity<WorkflowResponse> parallelWorkflow(@RequestBody WorkflowRequest request) {
        try {
            List<String> results = workflowService.executeParallelWorkflow(request.getInput());
            return ResponseEntity.ok(new WorkflowResponse(results, "parallel", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new WorkflowResponse("Error: " + e.getMessage(), "parallel", false));
        }
    }

    /**
     * Routing Workflow - Intelligent task distribution to specialized handlers
     */
    @PostMapping("/routing")
    public ResponseEntity<WorkflowResponse> routingWorkflow(@RequestBody WorkflowRequest request) {
        try {
            String result = workflowService.executeRoutingWorkflow(request.getInput());
            return ResponseEntity.ok(new WorkflowResponse(result, "routing", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new WorkflowResponse("Error: " + e.getMessage(), "routing", false));
        }
    }

    /**
     * Orchestrator-Workers Workflow - Central coordination with specialized workers
     */
    @PostMapping("/orchestrator-workers")
    public ResponseEntity<WorkflowResponse> orchestratorWorkersWorkflow(@RequestBody WorkflowRequest request) {
        try {
            Map<String, Object> result = workflowService.executeOrchestratorWorkersWorkflow(request.getInput());
            return ResponseEntity.ok(new WorkflowResponse(result, "orchestrator-workers", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new WorkflowResponse("Error: " + e.getMessage(), "orchestrator-workers", false));
        }
    }

    /**
     * Evaluator-Optimizer Workflow - Iterative refinement through evaluation
     */
    @PostMapping("/evaluator-optimizer")
    public ResponseEntity<WorkflowResponse> evaluatorOptimizerWorkflow(@RequestBody WorkflowRequest request) {
        try {
            Map<String, Object> result = workflowService.executeEvaluatorOptimizerWorkflow(request.getInput());
            return ResponseEntity.ok(new WorkflowResponse(result, "evaluator-optimizer", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new WorkflowResponse("Error: " + e.getMessage(), "evaluator-optimizer", false));
        }
    }

    /**
     * Get information about all available workflow patterns
     */
    @GetMapping("/patterns")
    public ResponseEntity<List<Map<String, Object>>> getWorkflowPatterns() {
        List<Map<String, Object>> patterns = workflowService.getWorkflowPatterns();
        return ResponseEntity.ok(patterns);
    }

    /**
     * Get detailed information about a specific workflow pattern
     */
    @GetMapping("/patterns/{patternName}")
    public ResponseEntity<Map<String, Object>> getWorkflowPattern(@PathVariable String patternName) {
        try {
            Map<String, Object> pattern = workflowService.getWorkflowPattern(patternName);
            return ResponseEntity.ok(pattern);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
