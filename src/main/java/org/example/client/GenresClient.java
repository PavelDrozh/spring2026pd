package org.example.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.dto.GenreDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GenresClient {

    private static final String GENRES_SERVICE_CB = "genresService";
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GenresClient(RestTemplate restTemplate, @Value("${genres-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Cacheable(cacheNames = "genresById", key = "#id")
    @CircuitBreaker(name = GENRES_SERVICE_CB, fallbackMethod = "getByIdFallback")
    public Optional<GenreDto> getById(long id) {
        try {
            ResponseEntity<GenreDto> resp = restTemplate.getForEntity(baseUrl + "/api/genres/" + id, GenreDto.class);
            return Optional.ofNullable(resp.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    private Optional<GenreDto> getByIdFallback(long id, Throwable ex) {
        return Optional.of(getUnknown(id));
    }

    private static GenreDto getUnknown(long id) {
        return GenreDto.builder()
                .id(id)
                .name("Unknown")
                .build();
    }

    @Cacheable(cacheNames = "genresAll")
    @CircuitBreaker(name = GENRES_SERVICE_CB, fallbackMethod = "getAllFallback")
    public List<GenreDto> getAll() {
        ResponseEntity<List<GenreDto>> resp = restTemplate.exchange(
                baseUrl + "/api/genres",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return resp.getBody() == null ? Collections.emptyList() : resp.getBody();
    }

    private List<GenreDto> getAllFallback(Throwable ex) {
        return List.of(getUnknown(0));
    }

}
