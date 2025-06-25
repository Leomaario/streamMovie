package com.hrrb.backend.service;

import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileStorageService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    public String storeFile(MultipartFile file, Long catalogoId) {
        Optional<Catalogo> catalogoOpt = catalogoRepository.findById(catalogoId);
        if (catalogoOpt.isEmpty() || catalogoOpt.get().getCaminhoPasta() == null || catalogoOpt.get().getCaminhoPasta().isBlank()) {
            throw new RuntimeException("Catálogo não encontrado ou não possui uma pasta de upload configurada.");
        }

        Path fileStorageLocation = Paths.get(catalogoOpt.get().getCaminhoPasta()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório do catálogo.", ex);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Nome de arquivo inválido: " + fileName);
            }
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível salvar o arquivo " + "Veja se ja tem video com esse nome, se sim, altere e tente novamente." + fileName, ex);
        }
    }
}