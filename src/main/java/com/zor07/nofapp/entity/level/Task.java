package com.zor07.nofapp.entity.level;


import javax.persistence.*;

@Entity
@Table(name = "task", schema = "public")
public class Task {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_id_seq"
    )
    @SequenceGenerator(
            name = "task_id_seq",
            sequenceName = "task_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;

    @Column(name="\"order\"")
    private Integer order;

    private String name;

    private String description;

    public Task() {
    }

    public Task(final Long id,
                final Level level,
                final Integer order,
                final String name,
                final String description) {
        this.id = id;
        this.level = level;
        this.order = order;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
