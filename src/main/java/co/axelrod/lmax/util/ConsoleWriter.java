package co.axelrod.lmax.util;

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
            if (part instanceof byte[] partByte) {
                for (byte b : partByte) {
                    System.out.write(b);
                }
            } else if (part instanceof String string) {
                for (byte b : string.getBytes()) {
                    System.out.write(b);
                }
            } else if (part instanceof Long l) {
                System.out.print(l);
            }
            else {
                System.out.print(part);
            }
        }
        System.out.write('\n');
    }
}
