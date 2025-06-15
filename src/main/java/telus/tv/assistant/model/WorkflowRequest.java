package telus.tv.assistant.model;

/**
 * Request model for workflow API endpoints
 */
public class WorkflowRequest {
    private String input;
    private String context;
    private Integer maxSteps;
    private Integer maxWorkers;

    public WorkflowRequest() {}

    public WorkflowRequest(String input) {
        this.input = input;
    }

    public WorkflowRequest(String input, String context) {
        this.input = input;
        this.context = context;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(Integer maxSteps) {
        this.maxSteps = maxSteps;
    }

    public Integer getMaxWorkers() {
        return maxWorkers;
    }

    public void setMaxWorkers(Integer maxWorkers) {
        this.maxWorkers = maxWorkers;
    }
}
