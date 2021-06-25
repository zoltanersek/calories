package com.zoltan.calories.setting;

import com.zoltan.calories.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
public class Setting {

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Setting(String name, String value, User user) {
        this.name = name;
        this.value = value;
        this.user = user;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String value;
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;
}
