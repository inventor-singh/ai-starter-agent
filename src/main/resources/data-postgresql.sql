INSERT INTO packages (id, name, description) VALUES 
(1, 'Basic Package', 'The TELUS Basic package includes local channels and essential programming for budget-conscious viewers.'),
(2, 'Standard Package', 'The TELUS Standard package includes popular entertainment, news, and sports channels for the whole family.'),
(3, 'Premium Package', 'The TELUS Premium package includes all Standard channels plus premium movie channels and specialty content.'),
(4, 'Sports Add-on', 'The TELUS Sports add-on includes additional sports channels covering regional and international sports events.'),
(5, 'Movies Add-on', 'The TELUS Movies add-on includes premium movie channels like HBO, Showtime, and Movie Central.');

-- Insert demo users (passwords are bcrypt encoded 'password')
INSERT INTO app_users (username, password, first_name, last_name, email, role, enabled) VALUES 
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 'admin@telus.com', 'ADMIN', true),
('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test', 'User', 'user@telus.com', 'USER', true);
