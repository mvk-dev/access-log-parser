import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogFile {
    private File file;

    private Checkable check;

    private int rowsCount = 0;

    private enum WebCrawlers {YANDEXBOT, GOOGLEBOT}

    private HashMap<String, Integer> webCrawlers1 = new HashMap<>();
    private HashMap<String, Integer> webCrawlers2 = new HashMap<>();

    public LogFile(File file) {
        this(file, null);
    }

    public LogFile(File file, Checkable checkString) {
        this.file = file;
        this.check = checkString;

        for (WebCrawlers crawler: WebCrawlers.values()) {
            webCrawlers1.put(crawler.name(), 0);
            webCrawlers2.put(crawler.name(), 0);
        }

    }

    public int getRowsCount() {
        return rowsCount;
    }

    public HashMap<String, Integer> getWebCrawlers1() {
        return new HashMap<>(webCrawlers1);
    }
    public HashMap<String, Integer> getWebCrawlers2() {
        return new HashMap<>(webCrawlers2);
    }

    public void read() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String text;

            Pattern pattern = Pattern.compile("\"([^\"]*?\\(([^)]*)\\).*)\"$");
            Pattern botPattern = Pattern.compile("(\\w+BOT)/");
            Matcher matcher;

            while ((text = reader.readLine()) != null) {
                rowsCount++;

                if (!check.checkString(text))
                    continue;

                matcher = pattern.matcher(text);
                if (matcher.find())
                {
                    // Для выполнения требования №1 к задаче
                    String userAgent = matcher.group(1);
                    // В требовании указано брать содержимое в первых скобках, но в них не всегда есть имя бота
                    // Например, строка 64 в access.log
                    String firstBrackets = matcher.group(2);

                    if (firstBrackets != null) {
                        String[] parts = firstBrackets.split(";");
                        if (parts.length > 1 && parts[1].indexOf("/") > 0) {
                            String crawler = parts[1].trim().substring(0, parts[1].trim().indexOf("/")).toUpperCase();

                            for (WebCrawlers en: WebCrawlers.values()) {
                                if (en.name().equals(crawler)) {
                                    webCrawlers1.put(crawler, webCrawlers1.get(crawler) + 1);
                                    break;
                                }
                            }
                        }
                    }

                    // Посчитаем по наличию подстроки BOT в User-Agent
                    Matcher botMatch = botPattern.matcher(userAgent.toUpperCase());
                    if (botMatch.find()) {
                        String crawler = botMatch.group(1);

                        for (WebCrawlers en: WebCrawlers.values()) {
                            if (en.name().equals(crawler)) {
                                webCrawlers2.put(crawler, webCrawlers2.get(crawler) + 1);
                                break;
                            }
                        }
                    }
                }
            }

        } catch (StringIsTooLongException ex) {
            throw ex;   // Просто для информации
        }
    }

}