# Hotel Booking Service

A modular Spring Boot backend service for property onboarding, real-time availability discovery, booking lifecycle management, pluggable payments, and policy-driven cancellations.

## Tech Stack
- **Java 21** & **Spring Boot 4.2**
- **Gradle** (Groovy DSL)
- **JUnit 5** & **Spring Boot Test**

## How to Build & Run

```bash
# Run unit & integration tests
./gradlew test

# Start the application (runs on http://localhost:8080)
./gradlew bootRun
```

## Key Architectural Decisions & Design Patterns

1. **Domain-Centric Modularity (`domain/`)**
    - Core entities (`Owner`, `Property`, `RoomType`, `Booking`, `Payment`), value objects (`DateRange`, `Location`), and domain exceptions have **zero Spring or persistence dependencies**.
    - State transitions (`PENDING_PAYMENT -> CONFIRMED / PAYMENT_FAILED -> CANCELLED`) live inside `Booking` to prevent invalid state mutations.

2. **Unified Single & Multi-Property Owner Model**
    - `Owner` maintains a collection of `propertyIds`. A single-property host is naturally represented as an `Owner` with 1 property—avoiding special-case branching.

3. **Open/Closed Extensibility (`strategy/`)**
    - **Discovery Filters (`PropertyFilter`)**: Specification pattern auto-wired as `List<PropertyFilter>` in `DiscoveryService`. Adding a new filter requires adding a single `@Component` with zero changes to search orchestration.
    - **Payment Gateways (`PaymentProcessor`)**: Strategy pattern supporting `CARD`, `UPI`, and `WALLET` behind a common abstraction.
    - **Cancellation Policy (`CancellationPolicy`)**: Pluggable refund calculation (`TieredCancellationPolicy`: 100% >= 48h prior, 50% >= 24h prior, 0% same-day).
    - **Pricing Strategy (`PricingStrategy`)**: Pluggable total price calculator (`StandardPricingStrategy`), ready for weekend/seasonal pricing extensions.

4. **Dynamic Interval-Based Inventory & Concurrency Control**
    - Availability is calculated dynamically by counting active bookings (`PENDING_PAYMENT` or `CONFIRMED`) whose `DateRange` overlaps the requested dates (`startA < endB && startB < endA`). Same-day checkout and check-in do not conflict.
    - `BookingService` uses fine-grained `ReentrantLock`s keyed by `propertyId:roomTypeId` to prevent double-booking race conditions without blocking unrelated properties.

5. **Payment Idempotency**
    - `POST /api/v1/bookings/{id}/pay` accepts an optional `Idempotency-Key` header so safe client retries return the original payment outcome without double-charging.

## Key API Endpoints
- `POST /api/v1/owners` — Register an individual or multi-property owner
- `POST /api/v1/properties` — Onboard a property with room types, amenities, and pricing
- `GET /api/v1/properties/search` — Search available properties (`city`, `locality`, `checkIn`, `checkOut`, `guests`, `minPrice`, `maxPrice`, `amenities`, `minStarRating`)
- `POST /api/v1/bookings` — Reserve inventory for a room type and date range
- `POST /api/v1/bookings/{id}/pay` — Process payment (`CARD`, `UPI`, `WALLET`) with optional `Idempotency-Key`
- `POST /api/v1/bookings/{id}/cancel` — Cancel booking, compute refund, and release inventory

## Assumptions
- Currency is uniform across properties and bookings.
- `PENDING_PAYMENT` bookings hold inventory immediately; if payment fails (`PAYMENT_FAILED`) or is cancelled (`CANCELLED`), inventory is released immediately.
- Authentication/authorization and persistent SQL storage are intentionally excluded per scope, with repositories abstracted behind interfaces (`OwnerRepository`, `PropertyRepository`, `BookingRepository`, `PaymentRepository`).

## Future Improvements (With More Time)
- **Hold Expiration Scheduler**: Background sweeper to auto-expire abandoned `PENDING_PAYMENT` bookings after a TTL (e.g., 10 minutes).
- **Persistent Database**: Swap in-memory `ConcurrentHashMap` repositories for Spring Data JPA + PostgreSQL with optimistic/pessimistic row locking (`SELECT FOR UPDATE`).
- **Dynamic Pricing Rules**: Decorator/Composite `PricingStrategy` supporting weekend multipliers, seasonal surge pricing, and promo codes.
- **OpenAPI / Swagger UI**: Add `springdoc-openapi` for interactive endpoint documentation.
