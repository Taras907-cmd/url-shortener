package com.example.demo;

import com.example.demo.model.Note;
import com.example.demo.service.NoteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    CommandLineRunner testRunner(NoteService noteService) {
//        return args -> {
//            Note n1 = noteService.add(new Note());
//            n1.setTitle("Перша нотатка");
//            n1.setContent("Текст 1");
//
//            System.out.println("Додано: " + n1);
//            System.out.println("Всі нотатки: " + noteService.listAll());
//
//            Note fetched = noteService.getById(n1.getId());
//            System.out.println("Отримано по id: " + fetched);
//
//            fetched.setTitle("Оновлена назва");
//            noteService.update(fetched);
//            System.out.println("Після оновлення: " + noteService.getById(n1.getId()));
//
//            noteService.deleteById(n1.getId());
//            System.out.println("Після видалення: " + noteService.listAll());
//
//            try {
//                noteService.getById(999_999_999L);
//                System.out.println("виключення");
//            } catch (Exception e) {
//                System.out.println("виключення при getById неіснуючого: " + e.getMessage());
//            }
//        };
//    }
}