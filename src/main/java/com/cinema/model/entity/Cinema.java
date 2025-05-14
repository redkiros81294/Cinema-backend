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
@Table(name = "cinemas")
@SQLDelete(sql = "UPDATE cinemas SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "jsonb")
    private String locations; // JSON array of branch locations

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Movie> movies;

    private boolean deleted = false;
} 