package com.leong.marketplacelistingmanager.services;

import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;


@Service
public class PhotoStorageService {
    private final ItemRepository itemRepository;
    private final Path rootPath;

    public PhotoStorageService(ItemRepository itemRepository,
                               @Value("${app.photos.root}") String photosRoot) {
        this.itemRepository = itemRepository;
        this.rootPath = Path.of(photosRoot);
    }

    private String cleanInputPath(String inputPath){
        return inputPath.replaceAll("[\\\\/:*?\"<>|]", "").trim();
    }

    private Path getOrCreateNewFolder(ItemEntity item) throws IOException{
        if(item.getPhotoFolderPath() != null){
            // Item already has a folder
            Path existingItem = Path.of(item.getPhotoFolderPath());
            return Files.createDirectories(existingItem);
        } else {
            // Item doesn't have a folder yet
            String baseName = cleanInputPath(item.getCategory().getName()) + "_" +
                    cleanInputPath(item.getTitle()) + "_" +
                    LocalDate.now();
            Path folder = rootPath.resolve(baseName);
            Files.createDirectories(folder);
            item.setPhotoFolderPath(folder.toString());
            itemRepository.save(item);
            return folder;
        }
    }

    @Transactional
    public void savePhoto(Long itemId, String originalFileName, InputStream fileContent) throws IOException {
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item does not exist"));
        Path folder = getOrCreateNewFolder(item);
        long existingCount;
        try (var stream = Files.list(folder)) {
            existingCount = stream.count();
        }
        long nextNumber = existingCount + 1;
        int dotIndex = originalFileName.lastIndexOf('.');
        String extension = dotIndex >= 0 ? originalFileName.substring(dotIndex).toLowerCase() : ".jpg";
        String newFileName = String.format("%03d%s", nextNumber, extension);
        Files.copy(fileContent, folder.resolve(newFileName));
    }


}
