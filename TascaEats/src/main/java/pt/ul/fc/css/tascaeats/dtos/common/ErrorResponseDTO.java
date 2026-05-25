package pt.ul.fc.css.tascaeats.dtos.common;

import java.time.Instant;

public record ErrorResponseDTO(Instant timestamp, Integer status, String error) {}
