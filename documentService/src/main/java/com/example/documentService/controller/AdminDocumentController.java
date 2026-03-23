package com.example.documentService.controller;

import com.example.documentService.dto.DocumentDto;
import com.example.documentService.entity.Document;
import com.example.documentService.service.DocumentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminDocumentController {
    private final DocumentService service;
    public AdminDocumentController (DocumentService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Document> allDocs() {
        return service.getAll();
    }

    @GetMapping("/byOwner/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DocumentDto> getByOwner(@PathVariable String username) {
        return service.getDocumentsByOwner(username);
    }
}
