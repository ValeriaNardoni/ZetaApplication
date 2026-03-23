package com.example.documentService.dto;

public class DocumentDto {
    private Long id;
    private String originalContent;
    private String signedContent;
    private String ownerUsername;

    public DocumentDto() {}

    public DocumentDto(Long id, String originalContent, String signedContent, String ownerUsername) {
        this.id = id;
        this.originalContent = originalContent;
        this.signedContent = signedContent;
        this.ownerUsername = ownerUsername;
    }

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getSignedContent() {
        return signedContent;
    }

    public void setSignedContent(String signedContent) {
        this.signedContent = signedContent;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}