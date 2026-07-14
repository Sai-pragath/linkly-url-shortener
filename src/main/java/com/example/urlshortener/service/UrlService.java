package com.example.urlshortener.service;

import com.example.urlshortener.model.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url shortenUrl(String originalUrl, String customAlias) {
        if (customAlias != null && !customAlias.trim().isEmpty()) {
            // Check if custom alias is already taken
            if (urlRepository.findByShortCode(customAlias).isPresent()) {
                throw new IllegalArgumentException("Custom alias is already in use");
            }
            Url url = new Url(originalUrl, customAlias);
            return urlRepository.save(url);
        }

        // Return existing if already shortened and no custom alias was requested
        Optional<Url> existing = urlRepository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            return existing.get();
        }

        String shortCode = generateUniqueCode();
        Url url = new Url(originalUrl, shortCode);
        return urlRepository.save(url);
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public void incrementClickCount(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    public boolean deleteUrl(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        if (urlOptional.isPresent()) {
            urlRepository.delete(urlOptional.get());
            return true;
        }
        return false;
    }

    public List<Url> getRecentUrls() {
        return urlRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomString(SHORT_CODE_LENGTH);
        } while (urlRepository.findByShortCode(code).isPresent());
        return code;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
