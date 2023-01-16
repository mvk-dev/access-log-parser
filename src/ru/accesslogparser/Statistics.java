package ru.accesslogparser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    private final HashSet<String> existingPages;
    private final HashSet<String> nonExistingPages;

    private final HashMap<String, Integer> osMap;
    private final HashMap<String, Integer> browserMap;


    public enum TimeIntervals {
         SECOND {
             protected double getTrafficRate(Statistics obj) {
                return (double) obj.totalTraffic / Duration.between(obj.minTime, obj.maxTime).toSeconds();
            }
        }
        , MINUTE {
            protected double getTrafficRate(Statistics obj) {
                return obj.totalTraffic / (Duration.between(obj.minTime, obj.maxTime).toSeconds() / 60.0);
            }
        }
        , HOUR {
            protected double getTrafficRate(Statistics obj) {
                return obj.totalTraffic / (Duration.between(obj.minTime, obj.maxTime).toSeconds() / 3600.0);
            }
        }
        , DAY {
            protected double getTrafficRate(Statistics obj) {
                return obj.totalTraffic / (Duration.between(obj.minTime, obj.maxTime).toSeconds() / 86400.0);
            }
        };

        protected double getTrafficRate(Statistics obj) {
            return 0;
        }
    }

    public Statistics() {
      existingPages = new HashSet<>();
      nonExistingPages = new HashSet<>();
      osMap = new HashMap<>();
      browserMap = new HashMap<>();
      clean();
    }

    public void clean() {
        totalTraffic = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
        existingPages.clear();
        nonExistingPages.clear();
        osMap.clear();
        browserMap.clear();
    }

    public void addEntry(LogEntry entry) {
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
    }

    /**
     * Метод без параметров, по умолчанию возвращающий долю в час согласно требованию в задаче
     */
    public double getTrafficRate() {
        return getTrafficRate(TimeIntervals.HOUR);
    }

    public double getTrafficRate(TimeIntervals interval) {
        return interval.getTrafficRate(this);
    }

    private HashMap<String, Double> getMapRates(HashMap<String, Integer> srcMap) {
        int totalRecsCount = 0;

        for(Integer value : srcMap.values())
            totalRecsCount += value;

        HashMap<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Integer> entry: srcMap.entrySet())
            result.put(entry.getKey(), (double)entry.getValue() / totalRecsCount);

        return result;
    }

    public HashMap<String, Double> getOsRate() {
        return getMapRates(osMap);
    }

    public HashMap<String, Double> getBrowserRate() {
        return getMapRates(browserMap);
    }

    public HashSet<String> getExistingPages() {
        return existingPages;
    }

    public HashSet<String> getNonExistingPages() {
        return nonExistingPages;
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
