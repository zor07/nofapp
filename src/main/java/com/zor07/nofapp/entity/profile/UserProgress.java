package com.zor07.nofapp.entity.profile;

import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.levels.Task;

import javax.persistence.*;

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
    @JoinColumn(name = "current_task_id", referencedColumnName = "id")
    private Task currentTask;

    public UserProgress(Long id, User user, Task currentTask) {
        this.id = id;
        this.user = user;
        this.currentTask = currentTask;
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

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
}
