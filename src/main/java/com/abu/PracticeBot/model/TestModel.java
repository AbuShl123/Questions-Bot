package com.abu.PracticeBot.model;


import com.abu.PracticeBot.service.TelegramBot;
import com.abu.PracticeBot.utils.InlineButtonsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TestModel implements BotModel {

    private TelegramBot bot;
    private User user;

    private int currentQuestion = 1;
    private final Map<Integer, String> userAnswers = new HashMap<>();

    @Override
    public void setConnection(TelegramBot bot, User user) {
        this.bot = bot;
        this.user = user;
    }

    @Override
    public void abortConnection() {
        this.bot = null;
        this.user = null;
        this.currentQuestion = 1;
    }

    @Override
    public void startThread(Update update) {
        switch (currentQuestion) {
            case 1 -> { startTest(); question1(); }
            case 2 -> question2();
            case 3 -> question3();
            case 4 -> question4();
            case 5 -> question5();
            case 6 -> question6();
            case 7 -> question7();
            default -> { endTest(); sendAnswers();}
        }
    }

    @Override
    public void processTextUpdate(Update update) {
        userAnswers.put(currentQuestion, update.getMessage().getText());
        currentQuestion += 1;
        startThread(update);
    }

    @Override
    public void processClickEvent(Update update) {
        userAnswers.put(currentQuestion, update.getCallbackQuery().getData());
        currentQuestion += 1;
        startThread(update);
    }

    @Override
    public void processDocUpdate(Update update) {

    }

    @Override
    public void processImageUpdate(Update update) {

    }

    private void sendAnswers() {
        User admin = bot.userRepository.findById(5529950018L).orElseThrow();

        String reply = """
                Результаты от: %s
                Ник телеграм: @%s
                %s                \s
                """;

        String firstname = user.getFirstname();
        String username = user.getUsername();

        StringBuilder answers = new StringBuilder();

        for (Map.Entry<Integer, String> entry : this.userAnswers.entrySet()) {
            answers.append("\n")
                    .append(entry.getKey())
                    .append(". ")
                    .append(entry.getValue().endsWith("incorrect_callback") ? "WRONG - " : "")
                    .append(entry.getValue());
        }

        bot.sendMessage(admin, reply, false,firstname, username, answers);
    }

    private void startTest() {
        String s = "Здравствуйте, @" + user.getUsername() + ", мы рады что вы выбрали нашу компанию! Перед тем как мы назначим собеседование, ответьте пожалуйста на следующие вопросы, чтобы мы поближе познакомились друг с другом перед встречей. \uD83D\uDE0A";

        var msg = new SendMessage(String.valueOf(user.getChatId()), s);

        bot.sendMessage(msg);
    }

    private void question1() {
        String question = "Вопрос 1.\nКакой у вас опыт работы в сфере автоматизации тестов?";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        var answerOptions =
                InlineButtonsBuilder.builder()
                        .addRow()
                            .button("1-3 года", "1-3")
                            .button("3-6 лет", "3-6")
                            .button("6 лет и больше", "6+")
                        .addRow()
                            .button("У меня нет опыта", "0")
                        .build();

        markup.setKeyboard(answerOptions);

        SendMessage replyToUser = new SendMessage(String.valueOf(user.getChatId()), question);

        replyToUser.setReplyMarkup(markup);

        bot.sendMessage(replyToUser);
    }

    private void question2() {
        String q = "Вопрос 2.\nРегрессионное и функциональное тестирование, есть ли разница? Если да, то какая?";
        bot.sendMessage(user, q);
    }

    private void question3() {
        String q = "Вопрос 3. \nВыберите правильные вариант из перечисленного, как правильно создается объект chrome driver?";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        markup.setKeyboard(
                InlineButtonsBuilder.builder()
                        .button("Driver driver = new ChromeDriver();","driver_incorrect_callback")
                        .addRow()
                        .button("WebDriver driver = new ChromiumDriver();", "chromium_incorrect_callback")
                        .addRow()
                        .button("Chrome driver = new ChromeDriver();", "chrome_incorrect_callback")
                        .addRow()
                        .button("WebDriver driver = new ChromeDriver();", "webdriver_chrome")
                        .build()
        );

        SendMessage msg = new SendMessage(String.valueOf(user.getChatId()), q);
        msg.setReplyMarkup(markup);

        bot.sendMessage(msg);
    }

    private void question4() {
        String q = "Вопрос 4.\nВыберите ниже инструмент для тестирования, с помощью которого разрабатывается BDD фреймворк.";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                InlineButtonsBuilder.builder()
                        .button("Selenium Webdriver", "selenium_incorrect_callback")
                            .addRow()
                        .button("Rest Assured", "rest_incorrect_callback")
                            .addRow()
                        .button("TestNG", "testng_incorrect_callback")
                            .addRow()
                        .button("Cucumber", "cucumber")
                            .addRow()
                        .button("Feature files", "feature_incorrect_callback")
                            .addRow()
                        .button("JUnit", "junit_incorrect_callback")
                            .build()
        );

        var msg = new SendMessage(String.valueOf(user.getChatId()), q);
        msg.setReplyMarkup(markup);

        bot.sendMessage(msg);
    }

    private void question5() {
        String q = "Вопрос 5.\nГотовы ли вы на собеседование с лайф-кодингом?";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                InlineButtonsBuilder.builder()
                        .button("Да", "yes")
                        .button("Нет", "no")
                        .build()
        );

        SendMessage msg = new SendMessage(String.valueOf(user.getChatId()), q);
        msg.setReplyMarkup(markup);

        bot.sendMessage(msg);
    }

    private void question6() {
        String q = "Вопрос 6.\nНа какую минимальную зарплату расчитываете при работе 40 часов в неделю 5/7?";
        bot.sendMessage(user, q);
    }

    private void question7() {
        String q = "Вопрос 7.\nВ случае если всё пройдет успешно и мы понравимся друг другу, когда вы будете готовы выйти на работу?";
        bot.sendMessage(user, q);
    }

    private void endTest() {
        String q = "Спасибо вам большое, что уделили нам время и ответили на наши вопросы! Мы сохранили ваши ответы, и обязательно свяжемся с вами если всё пойдет хорошо.";
        bot.sendMessage(user, q);
    }
}
