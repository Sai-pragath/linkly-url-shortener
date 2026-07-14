package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.model.Url;
import com.example.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<?> shortenUrl(@RequestBody UrlRequest request, HttpServletRequest httpRequest) {
        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("URL cannot be empty");
        }
        
        try {
            Url url = urlService.shortenUrl(request.getUrl(), request.getCustomAlias());
            String baseUrl = httpRequest.getRequestURL().toString().replace(httpRequest.getRequestURI(), "");
            String shortUrl = baseUrl + "/" + url.getShortCode();
            
            return ResponseEntity.ok(new UrlResponse(url.getOriginalUrl(), shortUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/api/v1/urls/recent")
    public ResponseEntity<List<Url>> getRecentUrls() {
        return ResponseEntity.ok(urlService.getRecentUrls());
    }

    @GetMapping("/{shortCode:[a-zA-Z0-9-]+}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getOriginalUrl(shortCode);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            urlService.incrementClickCount(url);
            
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .location(java.net.URI.create(url.getOriginalUrl()))
                                 .build();
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
    }

    @DeleteMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortCode) {
        boolean deleted = urlService.deleteUrl(shortCode);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
    }
}
