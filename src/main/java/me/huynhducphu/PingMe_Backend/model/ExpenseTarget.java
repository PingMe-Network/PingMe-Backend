package me.huynhducphu.PingMe_Backend.model;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

@Entity
@Table(name = "expense_targets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExpenseTarget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int month;
    private int year;

    private Double targetAmount;
}
