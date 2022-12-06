package com.zor07.nofapp.entity.levels;

import javax.persistence.*;

@Entity
@Table(name = "level", schema = "public")
public class Level {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "level_id_seq"
    )
    @SequenceGenerator(
            name = "level_id_seq",
            sequenceName = "level_id_seq",
            allocationSize = 1
    )
    private Long id;

    private String name;

    public Level(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Level() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
