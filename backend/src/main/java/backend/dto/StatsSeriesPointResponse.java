// backend/dto/StatsSeriesPointResponse.java
package backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsSeriesPointResponse(
    LocalDate date,
    BigDecimal value
) {}
