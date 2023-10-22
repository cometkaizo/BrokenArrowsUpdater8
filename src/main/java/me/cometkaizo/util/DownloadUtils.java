package me.cometkaizo.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DownloadUtils {

    public static byte[] download(URL url) throws IOException {
        URLConnection con;
        DataInputStream input;
        byte[] fileData;
        if (url != null) {
            con = url.openConnection(); // open the url connection
            input = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];
            for (int q = 0; q < fileData.length; q++) {
                fileData[q] = input.readByte();
            }
            input.close(); // close the data input stream
            return fileData;
        }
        return null;
    }

    public static String downloadStr(URL url) throws IOException {
        return new String(download(url), StandardCharsets.UTF_8);
    }
}
