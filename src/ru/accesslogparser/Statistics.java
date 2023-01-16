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

    private final HashMap<String, Integer> osMap;


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
      clean();
      existingPages = new HashSet<>();
      osMap = new HashMap<>();
    }

    public void clean() {
        totalTraffic = 0;
        minTime = LocalDateTime.MAX;
        maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        if (minTime.isAfter(entry.getTime()))
            minTime = entry.getTime();

        if (maxTime.isBefore(entry.getTime()))
            maxTime = entry.getTime();

        if (entry.getResponseCode() == 200 && entry.getReferer().length() > 1)
            existingPages.add(entry.getReferer());

        // Заполняем частоту использования ОС
        String os = entry.getUserAgent().getOs();
        Integer value = osMap.get(os);
        osMap.put(os, value == null ? 1 : value+1);
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

    public HashMap<String, Double> getOsRate() {
        int totalOsRecsCount = 0;

        for(Integer value : osMap.values())
            totalOsRecsCount += value;

        HashMap<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Integer> entry: osMap.entrySet())
            result.put(entry.getKey(), (double)entry.getValue() / totalOsRecsCount);

        return result;
    }

    public HashSet<String> getExistingPages() {
        return existingPages;
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
