package telus.tv.assistant;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.neo4j.Neo4jChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;


import io.modelcontextprotocol.client.McpSyncClient;
// import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
// import io.modelcontextprotocol.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import telus.tv.assistant.model.AssistantResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class AssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistantApplication.class, args);
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder aiBuilder, UserRepository userRepository, VectorStore vectorStore) {
		// Populate vector store with package data
		userRepository.findAll().forEach(p -> {
			var document = new Document(
					"id: %s, name: %s, description: %s".formatted(p.id(), p.name(), p.description()));
			vectorStore.add(List.of(document));
		});

		var system = """
				You are a helpful, knowledgeable, and friendly AI Assistant for learning and demonstration purposes.
				You help users with:
				1. General assistance and educational content
				2. Learning about Agentic AI Workflows - you can explain the 5 fundamental patterns:
				   - Chain Workflow: Sequential processing where each step builds on the previous
				   - Parallelization Workflow: Concurrent processing with result aggregation
				   - Routing Workflow: Intelligent task distribution to specialized handlers
				   - Orchestrator-Workers: Central coordination with specialized workers
				   - Evaluator-Optimizer: Iterative refinement through evaluation
				
				When users ask about AI workflows or patterns, explain them clearly and suggest they visit the 
				/agentic-workflows page to see interactive examples and code implementations.
				
				Always provide clear, concise, and accurate information.
				If you do not know the answer or the question is outside your scope, politely suggest seeking additional resources.
				""";
		
		return aiBuilder
				.defaultSystem(system)
				.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
				.build();
	}

	// @Bean
	// McpSyncClient mcpSyncClient() {
	// 	var mcp = McpClient.sync(HttpClientSseClientTransport.builder("http://localhost:8081").build())
	// 	.build();
	// 	mcp.initialize();
	// 	return mcp;
	// }
}

@Table(name = "packages")
record Package(@Id int id, String name, String description) {
}

interface UserRepository extends ListCrudRepository<Package, Integer> {
}

@Component
class SchedularService {

	@Tool(description = "schedule an appointment or demo")
	String scheduleAppointment(
			@ToolParam(description = "Preferred date and time for the appointment (ISO 8601 format)") String dateTime,
			@ToolParam(description = "Reason for the appointment") String reason) {
		System.out.println("Scheduling appointment on: " + dateTime + " for reason: " + reason);
		// Simulate scheduling logic
		return "Your appointment is scheduled for " + dateTime + " regarding: " + reason + ".";
	}
}

@Controller
@ResponseBody
class AssistantController {

	private final ChatClient ai;
	private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();
	private final Neo4jChatMemoryRepository chatMemoryRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	AssistantController(ChatClient chatClient, Neo4jChatMemoryRepository chatMemoryRepository) {
		this.ai = chatClient;
		this.chatMemoryRepository = chatMemoryRepository;
	}

	@GetMapping("/{user}/assistant")
	public String assistant(@PathVariable String user, @RequestParam String question) {
		try {
			// Use Neo4jChatMemoryRepository for persistent chat memory
			var advisor = this.memory.computeIfAbsent(user, u -> PromptChatMemoryAdvisor.builder(
					MessageWindowChatMemory.builder()
						.chatMemoryRepository(this.chatMemoryRepository) // Neo4j-backed
						.maxMessages(10)
						.build())
					.build());

			// Get AI response
			String aiContent = this.ai
					.prompt()
					.user(question)
					.advisors(advisor) // memory
					.call()
					.content();

			// Create structured response
			AssistantResponse response = createStructuredResponse(question, aiContent, user);
			
			// Return JSON
			return objectMapper.writeValueAsString(response);
			
		} catch (Exception e) {
			// Log the error
			System.err.println("Error in assistant: " + e.getMessage());
			e.printStackTrace();
			
			// Return structured error response
			try {
				AssistantResponse errorResponse = AssistantResponse.error(
					"Sorry, I'm having trouble right now. Please try again later."
				);
				return objectMapper.writeValueAsString(errorResponse);
			} catch (Exception jsonError) {
				// Fallback to plain text if JSON serialization fails
				return "Sorry, I'm having trouble right now. Please try again later.";
			}
		}
	}

	@GetMapping(value = "/{user}/assistant/stream", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<StreamingResponseBody> assistantStream(@PathVariable String user, @RequestParam String question) {
		try {
			// Use Neo4jChatMemoryRepository for persistent chat memory
			var advisor = this.memory.computeIfAbsent(user, u -> PromptChatMemoryAdvisor.builder(
					MessageWindowChatMemory.builder()
						.chatMemoryRepository(this.chatMemoryRepository) // Neo4j-backed
						.maxMessages(10)
						.build())
					.build());

			StreamingResponseBody stream = outputStream -> {
				try {
					// Stream the AI response chunk by chunk directly to client
					Flux<String> contentFlux = this.ai
							.prompt()
							.user(question)
							.advisors(advisor)
							.stream()
							.content();

					StringBuilder aggregatedContent = new StringBuilder();
					
					// Stream each chunk as it arrives
					contentFlux.doOnNext(chunk -> {
						try {
							// Send chunk immediately to client
							outputStream.write(chunk.getBytes("UTF-8"));
							outputStream.flush();
							
							// Aggregate for final structured response
							aggregatedContent.append(chunk);
							
							System.out.println("Streamed chunk: " + chunk); // Debug log
						} catch (Exception e) {
							System.err.println("Error streaming chunk: " + e.getMessage());
						}
					}).blockLast(); // Only block until the last element
					
					// Send structured response marker
					outputStream.write("\n\n---STRUCTURED_RESPONSE---\n".getBytes("UTF-8"));
					
					// Create and send final structured response
					AssistantResponse response = createStructuredResponse(question, aggregatedContent.toString(), user);
					String jsonResponse = objectMapper.writeValueAsString(response);
					outputStream.write(jsonResponse.getBytes("UTF-8"));
					outputStream.flush();
					outputStream.close();
					
					System.out.println("Stream completed"); // Debug log
					
				} catch (Exception e) {
					try {
						String errorMsg = "Error during streaming: " + e.getMessage();
						outputStream.write(errorMsg.getBytes("UTF-8"));
						outputStream.close();
					} catch (Exception ignored) {}
				}
			};
			
			return ResponseEntity.ok()
					.header("Content-Type", "text/plain; charset=utf-8")
					.header("Cache-Control", "no-cache")
					.header("Connection", "keep-alive")
					.body(stream);
			
		} catch (Exception e) {
			System.err.println("Error setting up stream: " + e.getMessage());
			e.printStackTrace();
			
			return ResponseEntity.status(500)
					.body(outputStream -> {
						try {
							outputStream.write("Error setting up streaming response".getBytes("UTF-8"));
							outputStream.close();
						} catch (Exception ignored) {}
					});
		}
	}

	/**
	 * Creates a structured response based on the question and AI content
	 * This method can be enhanced to detect different types of responses and add metadata/actions
	 */
	private AssistantResponse createStructuredResponse(String question, String aiContent, String user) {
		// Start with basic metadata
		Map<String, Object> metadata = Map.of(
			"user", user,
			"question_length", question.length(),
			"response_length", aiContent.length(),
			"has_memory", true
		);

		// Check if this should be an interactive response
		if (shouldBeInteractive(question, aiContent)) {
			List<AssistantResponse.Action> actions = generateActions(question, aiContent);
			return AssistantResponse.custom("interactive", aiContent, metadata, actions);
		}

		// Default to text response with metadata
		return AssistantResponse.withMetadata(aiContent, metadata);
	}

	/**
	 * Determines if a response should include interactive elements
	 */
	private boolean shouldBeInteractive(String question, String aiContent) {
		String lowerQuestion = question.toLowerCase();
		String lowerContent = aiContent.toLowerCase();
		
		// Add interactivity for certain keywords
		return lowerQuestion.contains("help") ||
			   lowerQuestion.contains("options") ||
			   lowerQuestion.contains("what can") ||
			   lowerContent.contains("would you like") ||
			   lowerContent.contains("choose") ||
			   lowerContent.contains("select");
	}

	/**
	 * Generates relevant actions based on the question and response
	 */
	private List<AssistantResponse.Action> generateActions(String question, String aiContent) {
		List<AssistantResponse.Action> actions = new java.util.ArrayList<>();
		
		// Always offer these basic actions for interactive responses
		actions.add(new AssistantResponse.Action(
			"get_help", 
			"button", 
			"📋 Get Help", 
			"Show available commands and features",
			Map.of("category", "help")
		));
		
		actions.add(new AssistantResponse.Action(
			"schedule_appointment", 
			"button", 
			"📅 Schedule Demo", 
			"Book a demo or consultation",
			Map.of("category", "service")
		));

		// Add context-specific actions
		if (question.toLowerCase().contains("example") || question.toLowerCase().contains("demo")) {
			actions.add(new AssistantResponse.Action(
				"view_examples", 
				"button", 
				"🎯 View Examples", 
				"See example workflows and demos",
				Map.of("category", "demo")
			));
		}

		if (question.toLowerCase().contains("troubleshoot") || question.toLowerCase().contains("problem")) {
			actions.add(new AssistantResponse.Action(
				"help_guide", 
				"button", 
				"🔧 Help Guide", 
				"Step-by-step assistance",
				Map.of("category", "support")
			));
		}

		return actions;
	}

}