package com.zoltan.calories.entry;

import com.zoltan.calories.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Entry {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime date;
    private String text;
    private Integer calories;
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    public Entry(LocalDateTime date, String text, Integer calories, User user) {
        this.date = date;
        this.text = text;
        this.calories = calories;
        this.user = user;
    }
}
