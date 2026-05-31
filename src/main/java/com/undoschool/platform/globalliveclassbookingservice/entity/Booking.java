package com.undoschool.platform.globalliveclassbookingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User registrant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    @Column(name = "booked_at")
    @Builder.Default
    private ZonedDateTime bookedAt = ZonedDateTime.now(ZoneOffset.UTC);

    @Column(name = "booking_price")
    private BigDecimal bookingPrice;

    @Column(name = "currency")
    private String currency;
}
