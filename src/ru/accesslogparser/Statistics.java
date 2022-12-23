package ru.accesslogparser;

import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

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

    @Override
    public String toString() {
        return "Statistics {" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
