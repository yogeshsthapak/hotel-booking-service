package io.github.yogeshsthapak.hotelbooking.strategy.pricing;

import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculateTotalPrice(RoomType roomType, DateRange dateRange);
}
