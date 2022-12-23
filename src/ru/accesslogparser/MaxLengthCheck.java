package ru.accesslogparser;

public class MaxLengthCheck implements Checkable {
    private final int maxLength;

    public MaxLengthCheck(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean checkString(String str) {
        if (str.length() > maxLength)
            throw new StringIsTooLongException("Длина строки = " + str.length() + ", что превышает максимально допустимую длину = " + maxLength + " символов");

        return true;
    }
}
