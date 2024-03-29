package ru.accesslogparser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Содержит данные из разобранной строки лог-файла
 */
public class LogEntry {
    /**
     * IP-адрес клиента
     */
    private String ipAddr;
    /**
     * Дата/время запроса
     */
    private LocalDateTime time;
    private HttpMethod method;
    private String path;
    private int responseCode;
    private int responseSize;
    /**
     * Адрес запрашиваемого страницы
     */
    private String referer;
    private UserAgent userAgent;

    /**
     * Разбирает строку лог-файла
     * @param row - строка лога
     * @throws LogEntryCreateException
     */
    public LogEntry(String row) throws LogEntryCreateException {
        int count = 1;
        Pattern pattern = Pattern.compile("([^ \"\\[]+)|(\\[(?<brackets>[^]]*)])|(\"(?<text>[^\"]*)\")");
        Matcher matcher = pattern.matcher(row);

        while (matcher.find()) {
            try {
                switch (count) {
                    case 1 -> this.ipAddr = matcher.group();
                    case 4 ->
                        // Теряем данные о TZ из-за LocalDateTime
                        this.time = LocalDateTime.parse(matcher.group("brackets"), DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US));
                    case 5 -> {
                        this.path = matcher.group("text");
                        for (HttpMethod meth : HttpMethod.values()) {
                            if (path.indexOf(" ") > 0 && meth.name().equals(path.substring(0, path.indexOf(" ")))) {
                                this.method = meth;
                                this.path = path.substring(path.indexOf(" ")+1);
                                break;
                            }
                        }
                    }
                    case 6 -> this.responseCode = Integer.parseInt(matcher.group());
                    case 7 -> this.responseSize = Integer.parseInt(matcher.group());
                    case 8 -> this.referer = matcher.group("text");
                    case 9 -> this.userAgent = new UserAgent(matcher.group("text"));
                }
            } catch (Exception ex) {
                throw new LogEntryCreateException("Ошибка при разборе строки: " + ex, ex);
            }
            count++;
        }
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    @Override
    public String toString() {
        return "LogEntry {" +
                "ip='" + ipAddr + '\'' +
                ", reqDate=" + time +
                ", reqType=" + method +
                ", reqPath='" + path + '\'' +
                ", respHttpCode=" + responseCode +
                ", respDataSize=" + responseSize +
                ", referer='" + referer + '\'' +
                ", userAgent=" + userAgent +
                '}';
    }
}
/**
 Используемые в рамках задачи методы
 */
enum HttpMethod {POST, GET, PUT, HEAD}