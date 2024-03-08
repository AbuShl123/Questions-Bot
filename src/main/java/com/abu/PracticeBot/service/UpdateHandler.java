package com.abu.PracticeBot.service;

import com.abu.PracticeBot.database.DBHandler;
import com.abu.PracticeBot.model.BotMood;
import com.abu.PracticeBot.model.NewsModel;
import com.abu.PracticeBot.model.TestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class UpdateHandler {

    @Autowired
    private BotMood botMood;

    @Autowired
    private DBHandler database;

    private TelegramBot bot;

    public void registerBot(TelegramBot bot) {
        this.bot = bot;
        botMood.registerBot(bot);
    }

    public void processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasEntities()) {
            processCommand(update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            processText(update);
        } else if (update.hasCallbackQuery()) {
            processCallBackQuery(update);
        }
    }

    private void processCallBackQuery(Update update) {
        botMood.getCurrentModule().processClickEvent(update);
    }

    private void processText(Update update) {
        botMood.getCurrentModule().processTextUpdate(update);
    }

    private void processCommand(Update update) {
        var message = update.getMessage();

        Optional<MessageEntity> entity = message.getEntities()
                .stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst();

        if (entity.isEmpty()) return;

        String command = message.getText().substring(entity.get().getOffset(), entity.get().getLength());

        switch (command) {
            case "/start":
            case "/start@AbuShl123Bot":
                database.registerUser(update);
                startCommandReceived(update);
                break;

            case "/test":
            case "/test@AbuShl123Bot":
                botMood.setMood(TestModel.class, database.getUser(update));
                botMood.getCurrentModule().startThread(update);
                break;

            case "/news":
            case "/news@AbuShl123Bot":
                botMood.setMood(NewsModel.class, database.getUser(update));
                botMood.getCurrentModule().startThread(update);
                break;

            default:
                bot.sendMessage(update, "Такая команда не поддерживается!");
        }
    }

    private void startCommandReceived(Update update) {
        String name = update.getMessage().getChat().getFirstName();
        String text = "Здравствуйте, %s! Я очень рад что вы выбрали меня, можете спрашивать у меня о многих вещах \ud83d\ude00";

        bot.sendMessage(update, text, name);
    }
}
