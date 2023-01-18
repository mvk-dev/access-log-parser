package ru.accesslogparser;

public class UserAgent {
    /**
     * Наименование операционной системы
     */
    private final String os;
    /**
     * Наименование браузера
     */
    private final String browser;
    /**
     * Признак поискового бота
     */
    private final boolean isBot;

    /**
     * Типы операционных систем и regex для их поиска в теле UserAgent
     */
    private enum OS {
          WINDOWS(".*\\bWINDOWS\\b.*")
        , ANDROID(".*\\bANDROID\\b.*")
        , IOS(".*\\b(IPHONE|IPAD)\\b.*")
        , MACOS(".*\\bMAC OS\\b.*")
        , LINUX(".*\\bLINUX\\b.*");

        private final String token;

        OS(String str) {
            this.token = str;
        }
    }

    /**
     * Типы браузеров и regex для их поиска в теле UserAgent
     */
    private enum Browsers {
          CHROME(".*\\bCHROME/.*")
        , FIREFOX(".*\\bFIREFOX/.*")
        , EDGE(".*\\bEDG/.*")
        , OPERA(".*\\bOPR/.*")
        , SAFARI(".*\\bMOBILE/.*")
        , IE(".*\\bIEMOBILE/.*")
        ;

        private final String token;

        Browsers(String str) {
            token = str;
        }
    }

    /**
     * Разбирает строку UserAgent лога
     * @param agent - строка с UserAgent
     */
    public UserAgent(String agent) {
        agent = agent.toUpperCase();
        String tmpBrowser = null;

        // Определение браузера
        for (Browsers value: Browsers.values()) {
            if (agent.matches(value.token)) {
                tmpBrowser = value.name();
                break;
            }
        }
        this.browser = (tmpBrowser == null) ? "UNKNOWN" : tmpBrowser;

        // Определение ОС
        String tmpOs = null;

        for (OS value: OS.values()) {
            if (agent.matches(value.token)) {
                tmpOs = value.name();
                break;
            }
        }
        this.os = (tmpOs == null) ? "UNKNOWN" : tmpOs;

        // Определение бота
        this.isBot = agent.matches(".*BOT\\b.*");
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        return isBot;
    }

    @Override
    public String toString() {
        return "{" +
                "os='" + os + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
}
