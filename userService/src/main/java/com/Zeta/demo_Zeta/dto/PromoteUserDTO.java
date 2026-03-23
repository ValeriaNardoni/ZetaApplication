package com.Zeta.demo_Zeta.dto;

public class PromoteUserDTO {
    private Long id;

    public PromoteUserDTO() {}

    public PromoteUserDTO(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}