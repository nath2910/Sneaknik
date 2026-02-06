// backend/dto/StatsDateBoundsResponse.java
package backend.dto;

import java.time.LocalDate;

public record StatsDateBoundsResponse(
    LocalDate minDate,
    LocalDate maxDate
) {}
