package io.github.yogeshsthapak.hotelbooking.strategy.pricing;

import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Primary
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateTotalPrice(RoomType roomType, DateRange dateRange) {
        return roomType.getBasePricePerNight()
                .multiply(BigDecimal.valueOf(dateRange.nights()));
    }
}
