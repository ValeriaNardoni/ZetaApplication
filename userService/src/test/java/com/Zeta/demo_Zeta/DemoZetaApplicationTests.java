package com.Zeta.demo_Zeta;

import com.Zeta.demo_Zeta.entity.User;
import com.Zeta.demo_Zeta.entity.UserRole;
import com.Zeta.demo_Zeta.repository.UserRepository;
import com.Zeta.demo_Zeta.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DemoZetaApplicationTests {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		when(passwordEncoder.encode(anyString()))
				.thenAnswer(inv -> "ENC-" + inv.getArgument(0));
	}

	@Test
	void register_shouldSaveUser() {
		User user = new User();
		user.setUsername("mario");
		user.setEmail("mario@example.com");
		user.setPassword("pwd");

		when(userRepository.findByUsername("mario")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("mario@example.com")).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenAnswer(inv -> {
			User u = inv.getArgument(0);
			u.setId(1L);
			return u;
		});

		User result = userService.register(user);

		assertEquals("mario", result.getUsername());
		assertEquals("ENC-pwd", result.getPassword());
		assertEquals(1L, result.getId());
	}

	@Test
	void register_shouldThrowIfUsernameExists() {
		User user = new User();
		user.setUsername("mario");
		user.setEmail("email");

		when(userRepository.findByUsername("mario"))
				.thenReturn(Optional.of(new User()));

		assertThrows(ResponseStatusException.class, () ->
				userService.register(user));
	}

	@Test
	void register_shouldThrowIfEmailExists() {
		User user = new User();
		user.setUsername("uname");
		user.setEmail("mario@example.com");

		when(userRepository.findByUsername("uname")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("mario@example.com"))
				.thenReturn(Optional.of(new User()));

		assertThrows(ResponseStatusException.class, () ->
				userService.register(user));
	}

	@Test
	void findByUsername_shouldReturnOptionalUser() {
		User user = new User();
		user.setUsername("mario");

		when(userRepository.findByUsername("mario"))
				.thenReturn(Optional.of(user));

		Optional<User> result = userService.findByUsername("mario");

		assertTrue(result.isPresent());
		assertEquals("mario", result.get().getUsername());
	}

	@Test
	void getAllUsers_shouldReturnList() {
		when(userRepository.findAll())
				.thenReturn(List.of(new User()));

		List<User> list = userService.getAllUsers();

		assertEquals(1, list.size());
	}

	@Test
	void deleteUser_shouldRemoveUser() {
		when(userRepository.existsById(1L)).thenReturn(true);

		userService.deleteUser(1L);

		verify(userRepository).deleteById(1L);
	}

	@Test
	void deleteUser_shouldThrowIfNotFound() {
		when(userRepository.existsById(2L)).thenReturn(false);

		assertThrows(ResponseStatusException.class, () ->
				userService.deleteUser(2L));
	}

	@Test
	void updateUser_shouldModifyUserData() {
		User existing = new User();
		existing.setId(1L);
		existing.setUsername("old");

		when(userRepository.findById(1L))
				.thenReturn(Optional.of(existing));
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		User newUser = new User();
		newUser.setUsername("new");
		newUser.setEmail("new@mail");

		User result = userService.updateUser(1L, newUser);

		assertEquals("new", result.getUsername());
		assertEquals("new@mail", result.getEmail());
	}

	@Test
	void updateUser_shouldThrowIfNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResponseStatusException.class, () ->
				userService.updateUser(1L, new User()));
	}

	@Test
	void promoteToAdmin_shouldSetRoleAdmin() {
		User user = new User();
		user.setRole(UserRole.USER);
		user.setId(1L);

		when(userRepository.findById(1L))
				.thenReturn(Optional.of(user));
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		User result = userService.promoteToAdmin(1L);

		assertEquals(UserRole.ADMIN, result.getRole());
	}

	@Test
	void changePassword_shouldEncodeAndSave() {
		User user = new User();
		user.setUsername("mario");
		user.setPassword("old");

		when(userRepository.findByUsername("mario"))
				.thenReturn(Optional.of(user));

		userService.changePassword("mario", "newpass");

		assertEquals("ENC-newpass", user.getPassword());
		verify(userRepository).save(user);
	}

	@Test
	void changePassword_shouldThrowIfUserNotFound() {
		when(userRepository.findByUsername("mario"))
				.thenReturn(Optional.empty());

		assertThrows(ResponseStatusException.class, () ->
				userService.changePassword("mario", "newpass"));
	}
	@Test
	void createInitialAdmin_shouldSetAdminRoleAndEncodePassword() {
		User user = new User();
		user.setPassword("pwd");

		when(passwordEncoder.encode("pwd")).thenReturn("ENC-pwd");
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		User result = userService.createInitialAdmin(user);

		assertEquals(UserRole.ADMIN, result.getRole());
		assertEquals("ENC-pwd", result.getPassword());
	}

	@Test
	void getUsersByRole_shouldReturnFilteredUsers() {
		when(userRepository.findByRole(UserRole.ADMIN))
				.thenReturn(List.of(new User()));

		List<User> result = userService.getUsersByRole(UserRole.ADMIN);

		assertEquals(1, result.size());
	}

	@Test
	void getUsersByRole_shouldReturnAllIfNull() {
		when(userRepository.findAll())
				.thenReturn(List.of(new User(), new User()));

		List<User> result = userService.getUsersByRole(null);

		assertEquals(2, result.size());
	}

	@Test
	void updateUser_shouldEncodePasswordIfProvided() {
		User existing = new User();
		existing.setId(1L);
		existing.setPassword("old");

		when(userRepository.findById(1L))
				.thenReturn(Optional.of(existing));
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		when(passwordEncoder.encode("newpass"))
				.thenReturn("ENC-newpass");

		User newUser = new User();
		newUser.setPassword("newpass");

		User result = userService.updateUser(1L, newUser);

		assertEquals("ENC-newpass", result.getPassword());
	}
}