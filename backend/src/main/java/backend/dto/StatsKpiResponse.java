// backend/dto/StatsKpiResponse.java
package backend.dto;

import java.math.BigDecimal;

public record StatsKpiResponse(
    BigDecimal value,
    BigDecimal deltaPct
) {}
