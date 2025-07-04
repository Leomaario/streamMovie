
package com.hrrb.backend.service;

import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.upload.default-path:uploads}")
    private String defaultUploadPath;

    @Autowired
    private CatalogoRepository catalogoRepository;

    public String storeFile(MultipartFile file, Long catalogoId) {
        Optional<Catalogo> catalogoOpt = catalogoRepository.findById(catalogoId);

        if (catalogoOpt.isEmpty()) {
            throw new RuntimeException("Catálogo não encontrado com o ID: " + catalogoId);
        }

        Catalogo catalogo = catalogoOpt.get();
        String uploadPath = catalogo.getCaminhoPasta();

        // Se o caminho não estiver configurado, usa o caminho padrão
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            uploadPath = defaultUploadPath + "/catalogo-" + catalogoId;
        }

        Path fileStorageLocation = Paths.get(uploadPath).toAbsolutePath().normalize();

        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível criar o diretório do catálogo: " + fileStorageLocation, ex);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Nome de arquivo inválido: " + fileName);
            }

            Path targetLocation = fileStorageLocation.resolve(fileName);
            if (Files.exists(targetLocation)) {
                fileName = System.currentTimeMillis() + "_" + fileName;
                targetLocation = fileStorageLocation.resolve(fileName);
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao salvar o arquivo " + fileName + ". Por favor, tente novamente.", ex);
        }
    }
}