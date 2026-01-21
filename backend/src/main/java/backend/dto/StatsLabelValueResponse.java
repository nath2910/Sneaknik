// backend/dto/StatsLabelValueResponse.java
package backend.dto;

import java.math.BigDecimal;

public record StatsLabelValueResponse(
    String label,
    BigDecimal value
) {}
