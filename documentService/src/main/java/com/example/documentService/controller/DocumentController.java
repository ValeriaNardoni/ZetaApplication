package com.example.documentService.controller;
import com.example.documentService.dto.DocumentDto;
import org.springframework.security.core.Authentication;

import com.example.documentService.entity.Document;
import com.example.documentService.service.DocumentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    public DocumentDto create(@RequestBody Document doc, Authentication auth) {
        return service.create(doc, auth.getName());
    }

    @GetMapping("/me")
    public List<DocumentDto> myDocs(Authentication auth) {
        return service.getMyDocuments(auth.getName());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        service.deleteDocument(id, auth.getName(), isAdmin);
    }

    @PutMapping("/{id}")
    public DocumentDto update(@PathVariable Long id,
                              @RequestBody Document updatedDoc,
                              Authentication auth) {
        return service.updateDocument(id, updatedDoc, auth.getName());
    }

}