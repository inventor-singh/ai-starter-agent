package telus.tv.assistant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("app_users")
public record User(
    @Id String username,
    String password,
    String firstName,
    String lastName,
    String email,
    String role,
    boolean enabled
) {
    // Factory method for creating a basic user
    public static User create(String username, String password, String firstName, String lastName, String email) {
        return new User(username, password, firstName, lastName, email, "USER", true);
    }
}