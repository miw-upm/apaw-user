package es.upm.miw.apaw.resources.dtos;

import java.time.LocalDateTime;

public record ApplicationInfoDto(String version, LocalDateTime timestamp) {
}

