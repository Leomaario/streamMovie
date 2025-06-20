// Crie um novo pacote chamado 'service' (ex: com.hrb.backend.service)
// e coloque este arquivo dentro dele.

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
    private CatalogoRepository catalogoRepository; // Precisamos disso para pegar o caminho da pasta

    /**
     * Armazena um arquivo na pasta correta, baseada no catálogo.
     * @param file O arquivo de vídeo enviado pelo usuário.
     * @param catalogoId O ID do catálogo ao qual o vídeo pertence.
     * @return O caminho completo onde o arquivo foi salvo.
     */
    public String storeFile(MultipartFile file, Long catalogoId) {
        // Busca o catálogo no banco para descobrir a pasta de destino
        Optional<Catalogo> catalogoOpt = catalogoRepository.findById(catalogoId);
        if (catalogoOpt.isEmpty() || catalogoOpt.get().getCaminhoPasta() == null || catalogoOpt.get().getCaminhoPasta().isBlank()) {
            throw new RuntimeException("Catálogo não encontrado ou não possui uma pasta de upload configurada.");
        }

        // Pega o caminho da pasta do catálogo (ex: "D:/videos/impressoras/")
        Path fileStorageLocation = Paths.get(catalogoOpt.get().getCaminhoPasta()).toAbsolutePath().normalize();

        try {
            // Cria a pasta do catálogo se ela não existir
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório do catálogo.", ex);
        }

        // Limpa e pega o nome original do arquivo
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Verifica se o nome do arquivo tem caracteres inválidos
            if (fileName.contains("..")) {
                throw new RuntimeException("Nome de arquivo inválido: " + fileName);
            }

            // Define o caminho completo do arquivo (pasta + nome do arquivo)
            Path targetLocation = fileStorageLocation.resolve(fileName);

            // Copia o arquivo para o destino, sobrescrevendo se já existir
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retorna o caminho completo como uma String para salvar no banco
            return targetLocation.toString();

        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível salvar o arquivo " + fileName, ex);
        }
    }
}
