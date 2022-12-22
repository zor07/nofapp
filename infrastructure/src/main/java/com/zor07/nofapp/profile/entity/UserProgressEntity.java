package com.zor07.nofapp.profile.entity;

import com.zor07.nofapp.levels.entity.TaskEntity;
import com.zor07.nofapp.user.entity.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_progress", schema = "public")
public class UserProgressEntity {


    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_progress_id_seq"
    )
    @SequenceGenerator(
            name = "user_progress_id_seq",
            sequenceName = "user_progress_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "current_task_id", referencedColumnName = "id")
    private TaskEntity currentTask;

    public UserProgressEntity(Long id, UserEntity user, TaskEntity currentTask) {
        this.id = id;
        this.user = user;
        this.currentTask = currentTask;
    }

    public UserProgressEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TaskEntity getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(TaskEntity currentTask) {
        this.currentTask = currentTask;
    }
}
