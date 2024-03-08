package com.abu.PracticeBot.service;

import com.abu.PracticeBot.config.BotConfig;
import com.abu.PracticeBot.model.User;
import com.abu.PracticeBot.model.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private UpdateHandler updateHandler;

    public TelegramBot() {
        super(BotConfig.TOKEN);
        System.out.println("BOT STARTED");
    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.processUpdate(update);
    }

    @PostConstruct
    private void init() {
        updateHandler.registerBot(this);
    }

    public SendMessage generateReply(User user, String textToSend, Object... args) {
        return generateReply(user.getChatId(), textToSend, args);
    }

    public SendMessage generateReply(long chatId, String textToSend, Object... args) {
        var sendMessage = new SendMessage();

        if (args.length != 0) textToSend = String.format(textToSend, args);

        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);

        return sendMessage;
    }

    public void sendMessage(Update update, String textToSend, Object... args) {
        sendMessage(update.getMessage().getChatId(), textToSend, args);
    }

    public void sendMessage(User user, String textToSend, Object... args) {
        sendMessage(user.getChatId(), textToSend, args);
    }

    public void sendMessage(User user, String textToSend, Boolean enableMarkdown, Object... args) {
        sendMessage(user.getChatId(), textToSend, enableMarkdown, args);
    }

    public void sendMessage(long chatId, String textToSend, Object... args) {
        sendMessage(chatId, textToSend, true, args);
    }

    public void sendMessage(long chatId, String textToSend, boolean enableMarkdown, Object... args) {
        SendMessage message = generateReply(chatId, textToSend, args);
        message.enableMarkdown(enableMarkdown);
        sendMessage(message);
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Failed to send message to user", e);
            e.printStackTrace();
        }
    }
}
