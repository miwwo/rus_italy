import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public class Translator {

    // Замените на данные вашей базы данных
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    private static final String API_KEY = "your_google_translate_api_key";

    public static void main(String[] args) {
        try {
            // Подключение к базе данных
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Получение записей из базы данных
            String selectQuery = "SELECT id, content FROM your_table";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String originalContent = resultSet.getString("content");

                // Перевод HTML-кода
                String translatedContent = translateHtml(originalContent);

                // Запись переведенного HTML-кода в базу данных
                String updateQuery = "UPDATE your_table SET content = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, translatedContent);
                updateStatement.setInt(2, id);
                updateStatement.executeUpdate();
            }

            // Закрытие ресурсов
            resultSet.close();
            selectStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String translateHtml(String html) {
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());

        // Перевод текста внутри тегов
        doc.traverse(new ElementTranslator());

        return doc.toString();
    }

    private static class ElementTranslator implements org.jsoup.select.NodeVisitor {
        @Override
        public void head(org.jsoup.nodes.Node node, int depth) {
            if (node instanceof Element) {
                Element element = (Element) node;
                // Перевод текста внутри тега
                String translatedText = translateText(element.text());
                element.text(translatedText);
            }
        }

        @Override
        public void tail(org.jsoup.nodes.Node node, int depth) {
            // Ничего не делаем на этом этапе
        }
    }

    private static String translateText(String text) {
            // Ваш API-ключ для Google Cloud Translate
            String apiKey = "ваш_ключ_api_от_google";

            // Создаем объект для работы с Google Cloud Translate
            Translate translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();

            // Выполняем перевод текста
            Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage("it"));

            // Возвращаем переведенный текст
            return translation.getTranslatedText();

    }
}
