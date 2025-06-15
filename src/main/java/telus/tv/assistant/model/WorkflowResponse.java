package telus.tv.assistant.model;

/**
 * Response model for workflow API endpoints
 */
public class WorkflowResponse {
    private Object output;
    private String workflowType;
    private boolean success;
    private String processingTime;
    private int steps;
    private String[] chainOfThought;

    public WorkflowResponse() {}

    public WorkflowResponse(Object output, String workflowType, boolean success) {
        this.output = output;
        this.workflowType = workflowType;
        this.success = success;
    }

    public WorkflowResponse(Object output, String workflowType, boolean success, String processingTime) {
        this.output = output;
        this.workflowType = workflowType;
        this.success = success;
        this.processingTime = processingTime;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String[] getChainOfThought() {
        return chainOfThought;
    }

    public void setChainOfThought(String[] chainOfThought) {
        this.chainOfThought = chainOfThought;
    }
}
