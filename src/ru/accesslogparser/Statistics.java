package ru.accesslogparser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс подсчёта статистики по разобранному лог-файлу
 */
public class Statistics {
    private final List<LogEntry> logEntries;

    /**
     * Общий трафик
     */
    private long totalTraffic;
    /**
     * Минимальное время в логе
     */
    private LocalDateTime minTime;
    /**
     * Максимальное время в логе
     */
    private LocalDateTime maxTime;
    /**
     * Общее число пользовательских запросов (не от ботов)
     */
    private int totalUsersRequests;

    /**
     * Общее число ошибочных запросов (responseCode = 4хх, 5хх )
     */
    private int totalErrorRequests;

    /**
     * Существующие страницы (responseCode = 200)
     */
    private final HashSet<String> existingPages;
    /**
     * Несуществующие страницы (responseCode = 404)
     */
    private final HashSet<String> nonExistingPages;

    /**
     * Статистика используемых ОС (доля от 0 до 1)
     */
    private final HashMap<String, Integer> osMap;
    /**
     * Статистика используемых браузеров (доля от 0 до 1)
     */
    private final HashMap<String, Integer> browserMap;

    private final HashSet<String> domains;
    /**
     * Временные интервалы для подсчёта средних значений за единицу времени
     */
    public enum TimeIntervals {
         SECOND {
             protected double getSecondsCount() {
                 return 1.0;
             }
         }
        , MINUTE {
            protected double getSecondsCount() {
                return 60.0;
            }
        }
        , HOUR {

            protected double getSecondsCount() {
                return 3600.0;
            }
        }
        , DAY {
            protected double getSecondsCount() {
                return 86400.0;
            }
        };

        protected double getSecondsCount() {
            return 0;
        }
    }

    public Statistics() {
        existingPages = new HashSet<>();
        nonExistingPages = new HashSet<>();
        osMap = new HashMap<>();
        browserMap = new HashMap<>();
        logEntries = new ArrayList<>();
        domains = new HashSet<>();
        clean();
    }

    /**
     * Сброс подсчитанной статистики
     */
    public void clean() {
        totalTraffic = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
        totalUsersRequests = 0;
        totalErrorRequests = 0;
        logEntries.clear();
        existingPages.clear();
        nonExistingPages.clear();
        osMap.clear();
        browserMap.clear();
        domains.clear();
    }

    /**
     * Подсчитывает статистику по распарсенной строке лога
     * @param entry - предварительно распарсенная строка
     */
    public void addEntry(LogEntry entry) {
        logEntries.add(entry);

        totalTraffic += entry.getResponseSize();
        if (minTime.isAfter(entry.getTime()))
            minTime = entry.getTime();

        if (maxTime.isBefore(entry.getTime()))
            maxTime = entry.getTime();

        if (entry.getReferer().length() > 1) {
            switch (entry.getResponseCode()) {
                case 200 -> existingPages.add(entry.getReferer());
                case 404 -> nonExistingPages.add(entry.getReferer());
            }
        }

        // Заполняем частоту использования ОС
        String os = entry.getUserAgent().getOs();
        Integer value = osMap.get(os);
        osMap.put(os, value == null ? 1 : value+1);

        // Заполняем частоту использования браузеров
        String browser = entry.getUserAgent().getBrowser();
        value = browserMap.get(browser);
        browserMap.put(browser, value == null ? 1 : value+1);

        // Подсчёт не ботов
        if (!entry.getUserAgent().isBot())
            totalUsersRequests += 1;

        // Подсчёт ошибочных запросов
        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600)
            totalErrorRequests += 1;

        // Выделение доменного имени
        Pattern pattern = Pattern.compile("^(http(s)?://|)(www\\.|)(?<domain>([^/]+)(?<!www)\\.[^/.&]+)($|/.*)");
        Matcher matcher = pattern.matcher(entry.getReferer());
        if (matcher.find())
            domains.add(matcher.group("domain"));
    }

    /**
     * Метод без параметров, по умолчанию возвращающий долю в час согласно требованию в задаче
     */
    public double getTrafficRate() {
        return getTrafficRate(TimeIntervals.HOUR);
    }

    /**
     * Возвращает среднее значение трафика за интервал времени
     * @param interval - заданный интервал
     * @return - средний трафик за указанный интервал
     */
    public double getTrafficRate(TimeIntervals interval) {
        return totalTraffic / (Duration.between(minTime, maxTime).toSeconds() / interval.getSecondsCount());
    }

    /**
     * Метод без параметров, по умолчанию возвращающий долю в час согласно требованию в задаче
     */
    public double getUserRequestsRate() {
        return getUserRequestsRate(TimeIntervals.HOUR);
    }

    /**
     * Возвращает количество запросов от пользователей за интервал времени
     * @param interval - заданный интервал
     * @return - среднее количество запросов за интервал
     */
    public double getUserRequestsRate(TimeIntervals interval) {
        return totalUsersRequests / (Duration.between(minTime, maxTime).toSeconds() / interval.getSecondsCount());
    }

    public double getErrorRequestsRate() {
        return getErrorRequestsRate(TimeIntervals.HOUR);
    }

    /**
     * Возвращает количество ошибочных запросов за интервал времени
     * @param interval - заданный интервал
     * @return - среднее количество ошибочных запросов за интервал
     */
    public double getErrorRequestsRate(TimeIntervals interval) {
        return totalErrorRequests / (Duration.between(minTime, maxTime).toSeconds() / interval.getSecondsCount());
    }

    /**
     * Возвращает среднее количество запросов от одного пользователя (ip адреса)
     * @return - отношение общего числа запросов от пользователей к числу уникальных ip адресов
     */
    public double getUserAttendanceRate() {
        return (double) totalUsersRequests / logEntries.stream()
                .filter(e -> !e.getUserAgent().isBot())
                .map(LogEntry::getIpAddr)
                .distinct()
                .count();
    }

    /**
     * Возвращает рассчитанные доли относительно общего количества записией в логе
     * @param srcMap - HashMap, для значений в которой нужно подсчитать их доли
     * @return - HashMap c рассчитанными долями относительно общего количества элементов
     */
    private HashMap<String, Double> getMapRates(HashMap<String, Integer> srcMap) {
        int totalRecsCount = 0;

        for(Integer value : srcMap.values())
            totalRecsCount += value;

        HashMap<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Integer> entry: srcMap.entrySet())
            result.put(entry.getKey(), (double)entry.getValue() / totalRecsCount);

        return result;
    }

    /**
     * Возвращает долю (от 0 до 1) используемых Операционных систем в логе
     * @return - HashMap c key-именем ОС и value-значением её доли
     */
    public HashMap<String, Double> getOsRate() {
        return getMapRates(osMap);
    }

    /**
     * Возвращает долю (от 0 до 1) используемых браузеров в логе
     * @return - HashMap c key-именем браузера и value-значением его доли
     */
    public HashMap<String, Double> getBrowserRate() {
        return getMapRates(browserMap);
    }

    /**
     * Возвращает список существующих странниц (responseCode = 200)
     * @return - HashSet c адресами страниц
     */
    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    /**
     * Возвращает список несуществующих странниц (responseCode = 404)
     * @return - HashSet c адресами страниц
     */
    public HashSet<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
    }

    /**
     * Рассчитывает пиковые значения с точки зрения количества запросов в еденицу времени
     * @return - Map cо значением временной метки и количеством запросов в этот момент
     */
    public Map<LocalDateTime, Integer> getPeakTimeAttendance() {
        // Группируем по времени
        Map<LocalDateTime, Long> map = logEntries.stream()
                .filter(e -> !e.getUserAgent().isBot())
                .collect(Collectors.groupingBy(LogEntry::getTime, Collectors.counting()));

        // Считаем элементы с максимальным количеством запросов
        long max = map.values()
                .stream()
                .max(Long::compareTo).orElse((long)0);

        // Отбираем наиболее пиковые времена
        return map.entrySet()
                .stream()
                .filter(e -> e.getValue() == max)
                .collect(Collectors.toMap(Map.Entry::getKey, e-> e.getValue().intValue()));
    }

    /**
     * Рассчитывает ip адреса с наибольшим количеством запросов с них
     * @return - Map c ip пользователя и количеством запросов от него
     */
    public Map<String, Integer> getPeakUserAttendance() {
        Map<String, Long> map = logEntries.stream()
                .filter(e -> !e.getUserAgent().isBot())
                .collect(Collectors.groupingBy(LogEntry::getIpAddr, Collectors.counting()));

        long max = map.values()
                .stream()
                .max(Long::compareTo).orElse((long)0);

        return map.entrySet()
                .stream()
                .filter(e -> e.getValue() == max)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    /**
     * Возвращает список уникальных доменных имен из {@link LogEntry#getReferer()}
     * @return - HashSet уникальных доменных имен
     */
    public HashSet<String> getDomains() {
        return new HashSet<>(domains);
    }

    @Override
    public String toString() {
        return "Statistics {" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
