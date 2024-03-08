package com.abu.PracticeBot.model;

import com.abu.PracticeBot.service.TelegramBot;
import org.springframework.stereotype.Component;

@Component
public class BotMood {

    private BotModel currentModel;
    private TelegramBot bot;

    public <T extends BotModel> void setMood(Class<T> clazz, User user) {
        if (currentModel != null)
            currentModel.abortConnection();

        try {
            currentModel = (BotModel) clazz.getDeclaredConstructors()[0].newInstance();
            currentModel.setConnection(bot, user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BotModel getCurrentModule() {
        return currentModel;
    }

    public void registerBot(TelegramBot bot) {
        this.bot = bot;
    }
}
