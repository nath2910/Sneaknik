// backend/dto/StatsPointResponse.java
package backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsPointResponse(
    LocalDate date,
    BigDecimal ca,
    BigDecimal profit
) {}
