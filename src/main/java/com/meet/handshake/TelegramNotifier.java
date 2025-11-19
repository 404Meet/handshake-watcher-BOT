package com.meet.handshake;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramNotifier {

    // TODO: fill these in after you create the bot
    private static final String BOT_TOKEN = "8572311801:AAE7tw3TbQoHY8gwXMLXTF4FK4HmvXTiutw";
    private static final String CHAT_ID = "-5002745954";

    public static void sendMessage(String text) {

        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);

            String data = "chat_id=" + URLEncoder.encode(CHAT_ID, StandardCharsets.UTF_8)
                    + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                System.out.println("Telegram response code: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
