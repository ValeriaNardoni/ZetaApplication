package com.example.documentService;
import com.example.documentService.entity.Document;
import com.example.documentService.repository.DocumentRepository;
import com.example.documentService.dto.DocumentDto;
import com.example.documentService.service.DocumentService;
import com.example.documentService.service.MockArubaService;
import com.example.documentService.service.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentServiceApplicationTests {
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private UserClient userClient;
	@Mock
	private DocumentRepository repository;

	@Mock
	private MockArubaService mockArubaService;

	@InjectMocks
	private DocumentService service;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}
	@Test
	void getUsers_shouldCallUserService() {
		ResponseEntity<Object> response =
				new ResponseEntity<>("OK", HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				any(),
				any(),
				eq(Object.class)
		)).thenReturn(response);

		Object result = userClient.getUsers("Bearer token");

		assertEquals("OK", result);
	}
	@Test
	void create_shouldSignAndSaveDocument() {
		Document doc = new Document();
		doc.setContent("ciao");

		when(mockArubaService.sign("ciao")).thenReturn("SIGNED-123");
		when(repository.save(any(Document.class))).thenAnswer(inv -> {
			Document d = inv.getArgument(0);
			d.setId(1L); // simula l'ID assegnato dal DB
			return d;
		});

		DocumentDto result = service.create(doc, "mario");

		assertEquals("ciao", result.getOriginalContent());
		assertEquals("SIGNED-123", result.getSignedContent());
		assertEquals("mario", result.getOwnerUsername());
		assertEquals(1L, result.getId());
	}

	@Test
	void getMyDocuments_shouldReturnUserDocs() {
		Document doc = new Document();
		doc.setId(1L);
		doc.setOwnerUsername("mario");
		doc.setOriginalContent("orig");
		doc.setContent("signed");

		when(repository.findByOwnerUsername("mario")).thenReturn(List.of(doc));

		List<DocumentDto> result = service.getMyDocuments("mario");

		assertEquals(1, result.size());
		assertEquals("orig", result.get(0).getOriginalContent());
	}

	@Test
	void delete_shouldThrowIfNotOwner() {
		Document doc = new Document();
		doc.setOwnerUsername("mario");

		when(repository.findById(1L)).thenReturn(Optional.of(doc));

		assertThrows(RuntimeException.class, () ->
				service.deleteDocument(1L, "luigi", false)
		);
	}

	@Test
	void delete_shouldAllowAdmin() {
		Document doc = new Document();
		doc.setOwnerUsername("mario");

		when(repository.findById(1L)).thenReturn(Optional.of(doc));

		service.deleteDocument(1L, "luigi", true);

		verify(repository).delete(doc);
	}

	@Test
	void update_shouldFailIfNotOwner() {
		Document doc = new Document();
		doc.setOwnerUsername("mario");

		when(repository.findById(1L)).thenReturn(Optional.of(doc));

		Document updated = new Document();
		updated.setContent("new");

		assertThrows(RuntimeException.class, () ->
				service.updateDocument(1L, updated, "luigi")
		);
	}
	@Test
	void update_shouldUpdateIfOwner() {
		Document doc = new Document();
		doc.setId(1L);
		doc.setOwnerUsername("mario");

		when(repository.findById(1L)).thenReturn(Optional.of(doc));
		when(mockArubaService.sign("new")).thenReturn("SIGNED-NEW");
		when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Document updated = new Document();
		updated.setContent("new");

		DocumentDto result = service.updateDocument(1L, updated, "mario");

		assertEquals("new", result.getOriginalContent());
		assertEquals("SIGNED-NEW", result.getSignedContent());
	}
	@Test
	void delete_shouldThrowIfNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () ->
				service.deleteDocument(1L, "mario", false)
		);
	}

	@Test
	void getAll_shouldReturnAllDocuments() {
		when(repository.findAll()).thenReturn(List.of(new Document()));

		List<Document> result = service.getAll();

		assertEquals(1, result.size());
	}







}
