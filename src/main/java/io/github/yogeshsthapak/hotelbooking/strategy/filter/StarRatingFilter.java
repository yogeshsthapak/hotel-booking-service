package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import org.springframework.stereotype.Component;

@Component
public class StarRatingFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.minStarRating() == null) {
            return true;
        }
        return property.getStarRating() >= criteria.minStarRating();
    }
}
