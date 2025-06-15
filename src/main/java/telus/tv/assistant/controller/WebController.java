package telus.tv.assistant.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "chat";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, Model model) {
        // Only allow admin users
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("username", authentication.getName());
            return "admin";
        } else {
            return "redirect:/chat";
        }
    }

    @GetMapping("/agentic-workflows")
    public String agenticWorkflows(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "agentic-workflows";
    }

    @GetMapping("/agentic-workflows/{pattern}")
    public String agenticWorkflowPattern(@PathVariable String pattern, Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("pattern", pattern);
        
        // Set pattern-specific data
        switch(pattern) {
            case "chain":
                model.addAttribute("title", "Chain Workflow");
                model.addAttribute("description", "Sequential processing where each step builds on the previous output");
                break;
            case "parallelization":
                model.addAttribute("title", "Parallelization Workflow");
                model.addAttribute("description", "Parallel processing for independent tasks with result aggregation");
                break;
            case "routing":
                model.addAttribute("title", "Routing Workflow");
                model.addAttribute("description", "Intelligent task distribution to specialized handlers");
                break;
            case "orchestrator-workers":
                model.addAttribute("title", "Orchestrator-Workers");
                model.addAttribute("description", "Central orchestration with specialized worker processes");
                break;
            case "evaluator-optimizer":
                model.addAttribute("title", "Evaluator-Optimizer");
                model.addAttribute("description", "Iterative refinement through evaluation and optimization");
                break;
            default:
                return "redirect:/agentic-workflows";
        }
        
        return "agentic-workflow-pattern";
    }
}