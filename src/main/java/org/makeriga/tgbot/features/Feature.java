package org.makeriga.tgbot.features;

import java.io.File;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.makeriga.tgbot.MakeRigaTgBot;
import org.makeriga.tgbot.Settings;
import org.makeriga.tgbot.helpers.TgbHelper;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class Feature {
    
    protected static final String MESSAGE__ANTISPAM_HIT = "Try again in a minute.";
    
    protected Settings settings = null;
    
    public void Init() { }
    
    private final List<Map.Entry<String, String>> publicCommands = new ArrayList<>();
    
    public List<Map.Entry<String, String>> getPublicCommands() {
        return this.publicCommands;
    }
    
    protected void AddPublicCommandDescription(String command, String description) {
        assert (command != null || description != null);
        publicCommands.add(new AbstractMap.SimpleEntry<>(command, description));
    }
    
    public Feature(MakeRigaTgBot bot, Settings settings) {
        this.bot = bot;
        this.settings = settings;
    }
    public abstract String GetId();
    public abstract boolean Execute(Update update, boolean isCallback, String text, boolean isPrivateMessage, Integer senderId, String senderTitle, Integer messageId, String chatId);
    
    private MakeRigaTgBot bot = null;
    
    protected MakeRigaTgBot getBot() {
        return this.bot;
    }
    
    protected void sendMessage(String chatId, String text, Integer replyTo) {
        sendMessage(chatId, text, replyTo, null);
    }
    
    protected void sendMessage(String chatId, String text, Integer replyTo, ReplyKeyboard replyMarkup) {
        this.bot.SendMessage(chatId, text, replyTo, replyMarkup);
    }
    
    protected boolean sendAntispamMessage(String chatId, String text, Integer replyTo, String antispamPreffix, Integer senderUserId) {
        return this.bot.SendAntispamMessage(chatId, text, replyTo, antispamPreffix, senderUserId);
    }
    
    protected void sendPublicMessage(String text) {
        sendPublicMessage(text, null);
    }
    
    protected void sendPublicMessage(String text, Integer replyTo) {
        this.bot.SendPublicMessage(text, replyTo);
    }
    
    protected boolean sendPublicAntispamMessage(String text, String preffix, Integer senderId) {
        return sendPublicAntispamMessage(text, null, preffix, senderId);
    }
    
    protected boolean sendPublicAntispamMessage(String text, Integer replyTo, String preffix, Integer senderId) {
        return this.sendAntispamMessage(settings.getChatId(), text, replyTo, preffix, senderId);
    }
    
    protected void sendSticker(String chatId, Integer replyTo, File stickerFile) {
        this.bot.SendSticker(chatId, replyTo, stickerFile);
    }
    
    protected void sendPhoto(String chatId, Integer replyTo, InputStream is, String fileName) throws TelegramApiException {
        this.bot.SendPhoto(chatId, replyTo, is, fileName);
    }
    
    protected String getWrappedCommand(String command) {
        return command + "@" + settings.getBotUsername();
    }
    
    protected boolean testCommand(String cmd, String text) {
        return testCommandWithoutArguments(cmd, text) || testCommandWithArguments(cmd, text);
    }
    
    protected boolean testCommandWithoutArguments(String cmd, String text) {
        if (cmd == null || text == null)
            return false;
        return text.equals(cmd) || text.equals(getWrappedCommand(cmd));
    }
    
    protected boolean testCommandWithArguments(String cmd, String text) {
        if (cmd == null || text == null)
            return false;
        for (String c : new String[] { cmd, getWrappedCommand(cmd)})
            if (text.startsWith(c + " ") && text.length() > c.length() + 1)
                return true;
        return false;
    }
    
    protected String prepareCallbackData(String data) {
        return TgbHelper.encodeCallbackData(GetId(), data, false, false);
    }
    
    protected String prepareCallbackData(String data, boolean autodeleteMessage, boolean autodeleteInlineKeyboard) {
        return TgbHelper.encodeCallbackData(GetId(), data, autodeleteMessage, autodeleteInlineKeyboard);
    }
}
