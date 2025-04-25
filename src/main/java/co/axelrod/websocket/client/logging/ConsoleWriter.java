package co.axelrod.websocket.client.logging;

import java.nio.ByteBuffer;

public class ConsoleWriter {
    public static void write(int value) {
        if (value == 0) {
            System.out.write('0');
            return;
        }

        if (value < 0) {
            System.out.write('-');
            value = -value;
        }

        int digits = 0;
        int temp = value;
        while (temp > 0) {
            temp /= 10;
            digits++;
        }

        for (int i = digits - 1; i >= 0; i--) {
            int pow = 1;
            for (int j = 0; j < i; j++) {
                pow *= 10;
            }
            int digit = (value / pow) % 10;
            System.out.write('0' + digit);
        }
    }

    public static void write(Object object) {
        switch (object) {
            case ByteBuffer byteBuffer -> {
                while (byteBuffer.hasRemaining()) {
                    System.out.write(byteBuffer.get());
                }
            }
            case Byte singleByte -> {
                System.out.write(singleByte);
            }
            case byte[] partByte -> {
                for (byte b : partByte) {
                    System.out.write(b);
                }
            }
            default -> System.out.print(object);
        }
    }

    public static void writeWithNewLine(Object object) {
        write(object);
        System.out.write('\n');
    }
}
