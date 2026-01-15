// backend/dto/StatsSummaryResponse.java
package backend.dto;

import java.math.BigDecimal;

public record StatsSummaryResponse(
    BigDecimal ca,
    BigDecimal profit,
    BigDecimal profitMargin, // 0..1
    long itemsVendues,
    long itemsEnStock,
    BigDecimal valeurStock
) {}
