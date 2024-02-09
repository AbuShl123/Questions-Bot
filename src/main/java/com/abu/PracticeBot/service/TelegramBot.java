package com.abu.PracticeBot.service;

import com.abu.PracticeBot.config.BotConfig;
import com.abu.PracticeBot.model.User;
import com.abu.PracticeBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    public TelegramBot() {
        super(BotConfig.TOKEN);
        this.initCommands();

    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasEntities()) {
            handleCommandMessage(update.getMessage());
        }
    }

    private void initCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/test", "Starts testing"));

        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (Exception e) {
            log.error("Failed to initialize bot commands", e);
            e.printStackTrace();
        }
    }

    private void handleCommandMessage(Message message) {
        Optional<MessageEntity> entity = message.getEntities()
                .stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst();

        if (entity.isEmpty()) return;

        String command = message.getText().substring(entity.get().getOffset(), entity.get().getLength());

        switch (command) {
            case "/start":
            case "/start@AbuShl123Bot":
                registerUser(message);
                startCommandReceived(message.getChatId(), message.getChat().getFirstName());
                break;

            case "/test":
            case "/test@AbuShl123Bot":

        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isPresent()) return;

        var chatId = msg.getChatId();
        var chat = msg.getChat();

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

    private void startCommandReceived(long chatId, String name) {
        String text = "Здравствуйте, " + name + "! Рады, что выбрали нас, давайте начнем опрос. \ud83d\ude00";

        sendMessage(chatId, text);
    }

    private void sendMessage(long chatId, String message) {
        SendMessage chat = new SendMessage();

        chat.setChatId(chatId);
        chat.setText(message);

        try {
            execute(chat);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to user", e);
            e.printStackTrace();
        }
    }
}
