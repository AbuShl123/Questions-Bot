package com.abu.PracticeBot.model;

import com.abu.PracticeBot.service.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Must be implemented by all telegram bot ObjectModels created under the model package
 */
public interface BotModel {
    void setConnection(TelegramBot bot, User user);
    void abortConnection();

    void startThread(Update update);
    void processTextUpdate(Update update);
    void processDocUpdate(Update update);
    void processImageUpdate(Update update);
    void processClickEvent(Update update);
}
