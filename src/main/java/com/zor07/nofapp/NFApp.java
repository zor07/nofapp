package com.zor07.nofapp;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NFApp implements CommandLineRunner {

    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private FileRepository fileRepository;

    public static void main(String[] args) {
        SpringApplication.run(NFApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        select tc.*
//        from task_content tc
//        where tc.task_id = (select t.id
//        from level l
//        join task t on l.id = t.level_id
//        join task_content tc on t.id = tc.task_id
//        where t.id > :tId
//        order by l."order", t."order", tc."order"
//        limit 1);
//
//        select tc.*
//        from task_content tc
//        where tc.task_id = (select t.id
//        from level l
//        join task t on l.id = t.level_id
//        join task_content tc on t.id = tc.task_id
//        order by l."order", t."order", tc."order"
//        limit 1);



        fileRepository.deleteAll();
        taskContentRepository.deleteAll();
        taskRepository.deleteAll();
        levelRepository.deleteAll();

        for (int levelIdx = 1; levelIdx <= 3; levelIdx++) {
            final var level = levelRepository.save(new Level(null, levelIdx * 10, String.format("Level %s", levelIdx)));
            for (int taskIdx = 1; taskIdx <= 3; taskIdx++) {
                final var task = taskRepository.save(new Task(
                                null,                             //id
                                level,                               //level
                                taskIdx * 10,                        //order
                                String.format("Level %s, Task %s", levelIdx, taskIdx), //name
                                "Description"                            //description
                ));
                for (int taskContentIdx = 1; taskContentIdx <= 3; taskContentIdx++) {
                    final var taskContent = taskContentRepository.save(new TaskContent(
                            null,                             //Long id
                            task,                                //Task task
                            taskContentIdx * 10,                 //Integer order
                            null,                                //File file
                            String.format("Level %s, Task %s, Task Content %s", levelIdx, taskIdx, taskContentIdx), //name
                            null                                 //String data
                    ));
                }
            }
        }
    }
}
