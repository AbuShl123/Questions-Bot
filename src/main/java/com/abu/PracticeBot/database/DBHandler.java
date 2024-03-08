package com.abu.PracticeBot.database;


import com.abu.PracticeBot.model.User;
import com.abu.PracticeBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;

@Slf4j
@Component
public class DBHandler {

    @Autowired
    UserRepository userRepository;

    private boolean userExists(Update update) {
        return userRepository.findById(update.getMessage().getChatId()).isPresent();
    }

    public User getUser(Update update) {
        return userRepository.findById(update.getMessage().getChatId()).orElseThrow();
    }

    public void registerUser(Update update) {
        if (userExists(update)) return;

        var message = update.getMessage();
        var chatId = message.getChatId();
        var chat = message.getChat();

        User user = new User();

        user.setChatId(chatId);
        user.setFirstname(chat.getFirstName());
        user.setLastname(chat.getLastName());
        user.setUsername(chat.getUserName());
        user.setRegisterDate(new Timestamp(System.currentTimeMillis()));

        System.out.println(user);

        userRepository.save(user);

        log.info("Added user to DB: " + user);
    }
}
