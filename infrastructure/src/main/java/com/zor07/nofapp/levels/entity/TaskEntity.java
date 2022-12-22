package com.zor07.nofapp.levels.entity;


import javax.persistence.*;

@Entity
@Table(name = "task", schema = "public")
public class TaskEntity {

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
    private LevelEntity level;

    @OneToOne
    @JoinColumn(name = "task_content_id", referencedColumnName = "id")
    private TaskContentEntity taskContent;

    @Column(name="\"order\"")
    private Integer order;

    private String name;

    private String description;

    public TaskEntity() {
    }

    public TaskEntity(Long id, LevelEntity level, TaskContentEntity taskContent, Integer order, String name, String description) {
        this.id = id;
        this.level = level;
        this.taskContent = taskContent;
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

    public LevelEntity getLevel() {
        return level;
    }

    public void setLevel(LevelEntity level) {
        this.level = level;
    }

    public TaskContentEntity getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(TaskContentEntity taskContent) {
        this.taskContent = taskContent;
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
