package telus.tv.mcp.mcp_schedular_server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class McpSchedularServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpSchedularServerApplication.class, args);
	}

	@Bean
	MethodToolCallbackProvider methodToolCallbackProvider(SchedularService schedularService) {
		return MethodToolCallbackProvider.builder()
		.toolObjects(schedularService)
		.build();
	}

}


@Component
class SchedularService {

	@Tool(description = "schedule an appointment with TELUS")
	String scheduleAppointment(
			@ToolParam(description = "Preferred date and time for the appointment (ISO 8601 format)") String dateTime,
			@ToolParam(description = "Reason for the appointment") String reason) {
		System.out.println("Scheduling appointment on: " + dateTime + " for reason: " + reason);
		// Simulate scheduling logic
		return "Your appointment with TELUS is scheduled for " + dateTime + " regarding: " + reason + ".";
	}
}