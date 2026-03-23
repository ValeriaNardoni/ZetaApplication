package com.example.documentService.service;

import com.example.documentService.entity.Document;
import com.example.documentService.repository.DocumentRepository;
import com.example.documentService.dto.DocumentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final MockArubaService mockArubaService;
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    public DocumentService(DocumentRepository repository,
                           MockArubaService mockArubaService) {
        this.repository = repository;
        this.mockArubaService = mockArubaService;
    }

    public DocumentDto create(Document doc, String username) {
        log.info("Creazione documento per utente: {}", username);

        String original = doc.getContent();
        String signed = mockArubaService.sign(original);

        doc.setOriginalContent(original);
        doc.setContent(signed);
        doc.setOwnerUsername(username);

        Document saved = repository.save(doc); 

        return new DocumentDto(
                saved.getId(),
                original,
                signed,
                username
        );
    }

    public List<DocumentDto> getMyDocuments(String username) {
        log.info("Recupero documenti dell'utente {}", username);

        return repository.findByOwnerUsername(username)
                .stream()
                .map(doc -> new DocumentDto(
                        doc.getId(),
                        doc.getOriginalContent(),
                        doc.getContent(),
                        doc.getOwnerUsername()
                ))
                .toList();
    }

    public List<Document> getAll() {
        log.info("Recupero tutti i documenti");

        return repository.findAll();
    }
    public List<DocumentDto> getDocumentsByOwner(String username) {
        log.info("Recupero documenti dell'utente (ADMIN) {}", username);

        return repository.findByOwnerUsername(username)
                .stream()
                .map(doc -> new DocumentDto(
                        doc.getId(),
                        doc.getOriginalContent(),
                        doc.getContent(),
                        doc.getOwnerUsername()
                ))
                .toList();
    }
    public void deleteDocument(Long id, String username, boolean isAdmin) {
        log.info("Richiesta DELETE documento {} da utente {} (isAdmin={})", id, username, isAdmin);

        Document doc = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento {} non trovato per DELETE", id);
                    return new RuntimeException("Document not found");
                });

        if (!doc.getOwnerUsername().equals(username) && !isAdmin) {
            log.warn("Accesso negato per utente {} sul documento {}", username, id);
            throw new RuntimeException("Access denied");
        }

        repository.delete(doc);
        log.info("Documento {} cancellato da {}", id, username);

    }
    public DocumentDto updateDocument(Long id, Document updatedDoc, String username) {
        log.info("Richiesta UPDATE documento {} da utente {}", id, username);

        Document doc = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento {} non trovato per UPDATE", id);
                    return new RuntimeException("Document not found");
                });

        if (!doc.getOwnerUsername().equals(username)) {
            log.warn("Accesso negato per UPDATE documento {} da utente {}", id, username);
            throw new RuntimeException("Access denied");
        }

        String original = updatedDoc.getContent();
        String signed = mockArubaService.sign(original);

        doc.setOriginalContent(original);
        doc.setContent(signed);

        Document saved = repository.save(doc);

        log.info("Documento {} aggiornato da {} con contenuto firmato", id, username);

        return new DocumentDto(
                saved.getId(),
                saved.getOriginalContent(),
                saved.getContent(),
                saved.getOwnerUsername()
        );
    }

}