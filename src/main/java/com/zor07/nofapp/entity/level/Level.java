package com.zor07.nofapp.entity.level;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

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

    @Column(name="\"order\"")
    private Integer order;

    @OneToMany(mappedBy = "level")
    private List<Task> tasks;

    public Level(Long id, Integer order, String name) {
        this.id = id;
        this.order = order;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
