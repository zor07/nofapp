package com.zor07.nofapp.entity.userprogress;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_progress", schema = "public")
public class UserProgress {
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
    private User user;

    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    private Instant completedDatetime;

    public UserProgress(Long id,
                        User user,
                        Task task,
                        Instant completedDatetime) {
        this.id = id;
        this.user = user;
        this.task = task;
        this.completedDatetime = completedDatetime;
    }

    public UserProgress(User user, Task currentTask) {
        this.user = user;
        this.task = currentTask;
    }

    public UserProgress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task currentTask) {
        this.task = currentTask;
    }

    public Instant getCompletedDatetime() {
        return completedDatetime;
    }

    public void setCompletedDatetime(Instant completedDatetime) {
        this.completedDatetime = completedDatetime;
    }
}
