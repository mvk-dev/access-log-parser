package ru.accesslogparser;

public class UserAgent {
    final String os;
    final String browser;

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
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    @Override
    public String toString() {
        return "{" +
                "os='" + os + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
}
