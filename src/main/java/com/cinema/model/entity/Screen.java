package com.cinema.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "screens")
@SQLDelete(sql = "UPDATE screens SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "seat_layout", columnDefinition = "jsonb")
    private String seatLayout; // JSON string representing seat layout

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Showtime> showtimes;

    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        active = true;
        deleted = false;
    }
} 