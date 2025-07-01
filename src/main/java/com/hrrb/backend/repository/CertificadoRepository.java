package com.hrrb.backend.repository;
import com.hrrb.backend.model.Certificado;
import com.hrrb.backend.model.Usuario; // Adicione este import
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // E este

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByUsuario(Usuario usuario);
}