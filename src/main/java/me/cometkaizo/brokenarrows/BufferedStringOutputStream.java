package me.cometkaizo.brokenarrows;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class BufferedStringOutputStream extends BufferedOutputStream {
    private final Consumer<String> callback;

    public BufferedStringOutputStream(OutputStream out, Consumer<String> callback) {
        super(out);
        this.callback = callback;
    }

    private void flushBuffer() throws IOException {
        if (count > 0) {
            callback.accept(new String(buf, 0, count));
            out.write(buf, 0, count);
            count = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        synchronized (this) {
            implWrite(b);
        }
    }

    private void implWrite(int b) throws IOException {
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte)b;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        synchronized (this) {
            implWrite(b, off, len);
        }
    }

    private void implWrite(byte[] b, int off, int len) throws IOException {
        if (len >= buf.length) {
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        if (len > buf.length - count) {
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            implFlush();
        }
    }

    private void implFlush() throws IOException {
        flushBuffer();
        out.flush();
    }
}
