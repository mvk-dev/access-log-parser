import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class LogFile {
    private File file;
    private int rowsCount = 0;
    private int maxRowLength = 0;
    private int minRowLength = Integer.MAX_VALUE;

    private Checkable check;

    public LogFile(File file) {
        this(file, null);
    }

    public LogFile(File file, Checkable checkString) {
        this.file = file;
        this.check = checkString;
    }

    public void read() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String text;

            while ((text = reader.readLine()) != null) {
                rowsCount++;

                if (!check.checkString(text))
                    continue;

                maxRowLength = Math.max(maxRowLength, text.length());
                minRowLength = Math.min(minRowLength, text.length());
            }

        } catch (StringIsTooLongException ex) {
            throw ex;   // Просто для информации
        }
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getMaxRowLength() {
        return maxRowLength;
    }

    public int getMinRowLength() {
        return minRowLength;
    }
}
