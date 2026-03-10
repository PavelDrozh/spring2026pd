package org.example.actuators;

import lombok.AllArgsConstructor;
import org.example.model.Author;
import org.example.repository.AuthorsRepo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthorsHealthIndicator implements HealthIndicator {

    private final AuthorsRepo authorsRepo;

    @Override
    public Health health() {
        Optional<Author> author = authorsRepo.findFirstByIdAfter(0L);
        if (author.isEmpty()) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Нет авторов! Сервис не сможет нормально работать без администратора!")
                    .build();
        } else {
            return Health.up().withDetail("message", "Авторы на месте!").build();
        }
    }
}

