package com.abu.PracticeBot.model;

import com.abu.PracticeBot.service.TelegramBot;
import com.abu.PracticeBot.utils.InlineButtonsBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static io.restassured.RestAssured.given;

@Component
public class NewsModel implements BotModel {

    private static final String URI = "https://newsapi.org/v2/everything";
    private static final String apiKey = "61d6cc9e13cc4626afbd54bd01fe7123";

    private TelegramBot bot;
    private Response news;
    private User user;
    private int ind;
    private String keyword;
    private boolean inProcess;

    public void setConnection(TelegramBot bot, User user) {
        this.inProcess = true;
        this.bot = bot;
        this.user = user;
    }

    public void abortConnection() {
        this.inProcess = false;
        this.keyword = null;
        this.news = null;
        this.ind = 0;
        this.user = null;
    }

    public void startThread(Update update) {
        if (keyword == null) {
            askForKeyword(update);
            return;
        }

        setNews(keyword);
        sendNews(ind);
    }

    @Override
    public void processTextUpdate(Update update) {
        keyword = update.getMessage().getText();
        startThread(update);
    }

    @Override
    public void processDocUpdate(Update update) {

    }

    @Override
    public void processImageUpdate(Update update) {

    }

    public boolean isInProcess() {
        return inProcess;
    }

    public void processClickEvent(Update update) {
        if (update.getCallbackQuery().getData().equals("more_news"))
            sendNews(++ind);
    }

    private void sendNews(int i) {
        if (Integer.parseInt(news.jsonPath().getString("totalResults")) <= i) {
            if (i == 0)
                bot.sendMessage(user, "Простите, к сожалению по вашему запросу новостей не нашлось \uD83D\uDE14 \nПопробуйте упростить ваш запрос, сделать его короче.");
            return;
        }

        String author = news.jsonPath().getString("articles[" + i + "].author");
        String title = news.jsonPath().getString("articles[" + i + "].title");
        String description = news.jsonPath().getString("articles[" + i + "].description");
        String urlToArticle = news.jsonPath().getString("articles[" + i + "].url");

        if (author == null) {
            author = "Неизвестный";
        }

        String aNews = """
                _Автор: %s_
                                    
                *%s*
                                    
                %s
                                    
                Читать здесь: %s             \s
                """;
        aNews = String.format(aNews, author, title, description, urlToArticle);

        var msg = bot.generateReply(user, aNews);
        var button = InlineButtonsBuilder.builder()
                        .button("▼", "more_news")
                        .build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(button);
        msg.setReplyMarkup(markup);
        msg.enableMarkdown(true);

        bot.sendMessage(msg);
    }

    private void setNews(String keyword) {
        news =
                given()
                        .accept(ContentType.JSON)
                        .queryParam("apiKey", apiKey)
                        .queryParam("q", keyword)
                .when()
                        .get(URI)
                .then()
                        .extract().response();
    }

    private void askForKeyword(Update update) {
        bot.sendMessage(update, "Какие новости ищете?");
    }
}
