package telus.tv.assistant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Flexible response structure for the AI Assistant
 * Supports both simple text responses and complex structured responses
 */
public record AssistantResponse(
    @JsonProperty("type") String type,                    // "text", "interactive", "error"
    @JsonProperty("content") String content,              // Main response text
    @JsonProperty("metadata") Map<String, Object> metadata, // Extensible metadata
    @JsonProperty("actions") List<Action> actions,        // Interactive actions (buttons, etc.)
    @JsonProperty("timestamp") long timestamp             // Response timestamp
) {
    
    /**
     * Interactive action that users can take
     */
    public record Action(
        @JsonProperty("id") String id,                    // Unique action identifier
        @JsonProperty("type") String type,                // "button", "link", "input"
        @JsonProperty("label") String label,              // Display text
        @JsonProperty("description") String description,   // Tooltip/help text
        @JsonProperty("data") Map<String, Object> data    // Action-specific data
    ) {}
    
    // Static factory methods for common response types
    
    /**
     * Simple text response
     */
    public static AssistantResponse text(String content) {
        return new AssistantResponse(
            "text", 
            content, 
            Map.of(), 
            List.of(), 
            System.currentTimeMillis()
        );
    }
    
    /**
     * Interactive response with actions
     */
    public static AssistantResponse interactive(String content, List<Action> actions) {
        return new AssistantResponse(
            "interactive", 
            content, 
            Map.of(), 
            actions, 
            System.currentTimeMillis()
        );
    }
    
    /**
     * Response with metadata
     */
    public static AssistantResponse withMetadata(String content, Map<String, Object> metadata) {
        return new AssistantResponse(
            "text", 
            content, 
            metadata, 
            List.of(), 
            System.currentTimeMillis()
        );
    }
    
    /**
     * Error response
     */
    public static AssistantResponse error(String message) {
        return new AssistantResponse(
            "error", 
            message, 
            Map.of("error", true), 
            List.of(), 
            System.currentTimeMillis()
        );
    }
    
    /**
     * Full customizable response
     */
    public static AssistantResponse custom(String type, String content, Map<String, Object> metadata, List<Action> actions) {
        return new AssistantResponse(type, content, metadata, actions, System.currentTimeMillis());
    }
}