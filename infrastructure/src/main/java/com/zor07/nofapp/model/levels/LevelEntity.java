package com.zor07.nofapp.model.levels;

import javax.persistence.*;

@Entity
@Table(name = "level", schema = "public")
public class LevelEntity {

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

    @Column(name="\"order\"")
    private Integer order;

    public LevelEntity(Long id, Integer order, String name) {
        this.id = id;
        this.order = order;
        this.name = name;
    }

    public LevelEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
