package me.cometkaizo.brokenarrows;

import me.cometkaizo.io.DataSerializable;
import me.cometkaizo.io.PathSerializable;
import me.cometkaizo.io.data.CompoundData;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class BrokenArrowsInfo implements DataSerializable, PathSerializable {
    public static final String SAVE_NAME = "info.properties";

    public LocalDateTime lastAutoUpdateTime;

    @Override
    public CompoundData write() {
        CompoundData data = new CompoundData();

        if (lastAutoUpdateTime != null) {
            CompoundData lastAutoUpdateData = new CompoundData();
            lastAutoUpdateData.putInt("month", lastAutoUpdateTime.getMonthValue());
            lastAutoUpdateData.putInt("day", lastAutoUpdateTime.getDayOfMonth());
            lastAutoUpdateData.putInt("hour", lastAutoUpdateTime.getHour());
            lastAutoUpdateData.putInt("minute", lastAutoUpdateTime.getMinute());
            data.put("lastAutoUpdateTime", lastAutoUpdateData);
        }

        return data;
    }

    @Override
    public void read(CompoundData data) {
        CompoundData lastAutoUpdateData = data.getCompound("lastAutoUpdateTime").orElse(null);
        if (lastAutoUpdateData != null) {
            int month = lastAutoUpdateData.getInt("month").orElse(1),
                    day = lastAutoUpdateData.getInt("day").orElse(1),
                    hour = lastAutoUpdateData.getInt("hour").orElse(1),
                    minute = lastAutoUpdateData.getInt("minute").orElse(0);

            lastAutoUpdateTime = LocalDateTime.of(0, month, day, hour, minute);
        } else lastAutoUpdateTime = LocalDateTime.now();
    }

    @Override
    public void write(Path dataFolder) throws IOException {
        File file = dataFolder.resolve(SAVE_NAME).toFile();
        write().write(new DataOutputStream(new FileOutputStream(file)));
    }

    @Override
    public void read(Path dataFolder) throws IOException {
        Path file = dataFolder.resolve(SAVE_NAME);
        if (file.toFile().exists()) {
            read(CompoundData.of(file));
        } else {
            lastAutoUpdateTime = LocalDateTime.now();
        }
    }
}
