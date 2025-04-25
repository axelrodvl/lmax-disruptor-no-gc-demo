package co.axelrod.websocket.client.logging;

import java.nio.ByteBuffer;

public class ConsoleWriter {
    public static void write(Object... parts) {
        for (Object part : parts) {
            switch (part) {
                case ByteBuffer byteBuffer -> {
                    int length = byteBuffer.remaining();
                    for (int i = 0; i < length; i++) {
                        System.out.write(byteBuffer.get(i));
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
                case String string -> {
                    for (byte b : string.getBytes()) {
                        System.out.write(b);
                    }
                }
                case Long l -> System.out.print(l);
                default -> System.out.print(part);
            }
        }
        System.out.write('\n');
    }
}
