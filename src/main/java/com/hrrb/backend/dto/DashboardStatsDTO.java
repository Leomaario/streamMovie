package com.hrrb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalCursos;
    private long totalUsuarios;
    private long totalRelatorios; // Mantendo para o futuro
}