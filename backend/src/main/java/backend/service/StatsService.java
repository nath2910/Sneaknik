package backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import backend.dto.*;
import backend.dto.TopVenteProjection;
import backend.repository.SnkVenteRepository;

@Service
public class StatsService {

  private final SnkVenteRepository repo;

  public StatsService(SnkVenteRepository repo) {
    this.repo = repo;
  }

  public StatsSummaryResponse summary(Long userId, LocalDate from, LocalDate to) {
    BigDecimal ca = repo.caBetween(userId, from, to);
    BigDecimal profit = repo.profitBetween(userId, from, to);
    long sold = repo.countSoldBetween(userId, from, to);
    long stock = repo.countInStock(userId);
    BigDecimal stockValue = repo.stockValue(userId);

    BigDecimal margin = BigDecimal.ZERO;
    if (ca != null && ca.compareTo(BigDecimal.ZERO) > 0) {
      margin = profit.divide(ca, 4, RoundingMode.HALF_UP);
    }

    return new StatsSummaryResponse(
        ca == null ? BigDecimal.ZERO : ca,
        profit == null ? BigDecimal.ZERO : profit,
        margin,
        sold,
        stock,
        stockValue == null ? BigDecimal.ZERO : stockValue
    );
  }

  public List<StatsPointResponse> timeseries(Long userId, LocalDate from, LocalDate to, String granularity) {
    var rows = "week".equalsIgnoreCase(granularity)
        ? repo.timeseriesWeek(userId, from, to)
        : repo.timeseriesDay(userId, from, to);

    return rows.stream()
        .map(r -> new StatsPointResponse(r.getBucket(), r.getCa(), r.getProfit()))
        .toList();
  }

  public List<StatsBreakdownResponse> brandBreakdown(Long userId, LocalDate from, LocalDate to) {
    return repo.brandBreakdownSales(userId, from, to).stream()
        .map(r -> new StatsBreakdownResponse(r.getLabel(), r.getNb()))
        .toList();
  }

  public List<TopVenteProjection> topSales(Long userId, LocalDate from, LocalDate to, int limit) {
    int safe = Math.min(Math.max(limit, 1), 20);
    return repo.topVentesBetween(userId, from, to).stream().limit(safe).toList();
  }
}
