package co.axelrod.websocket.client.console;

import io.netty.buffer.ByteBuf;

public class ConsoleWriter {
    public static void write(ByteBuf buffer) {
        int len = buffer.readableBytes();
        try {
            int readerIndex = buffer.readerIndex();
            for (int i = 0; i < len; i++) {
                System.out.write(buffer.getByte(readerIndex + i));
            }
            System.out.write('\n');
        } finally {
            buffer.release();
        }
    }

    public static void write(Object... parts) {
        for (Object part : parts) {
            switch (part) {
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
