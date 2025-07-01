package com.hrrb.backend.dto;

import com.hrrb.backend.model.Certificado;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CertificadoDTO {
    private Long id;
    private String tituloCurso;
    private LocalDateTime dataEmissao;
    private String codigoValidacao;

    public CertificadoDTO(Certificado certificado) {
        this.id = certificado.getId();
        this.tituloCurso = certificado.getTituloCurso();
        this.dataEmissao = certificado.getDataEmissao();
        this.codigoValidacao = certificado.getCodigoValidacao();
    }
}