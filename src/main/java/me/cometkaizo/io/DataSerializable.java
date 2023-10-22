package me.cometkaizo.io;

import me.cometkaizo.io.data.CompoundData;

public interface DataSerializable {
    CompoundData write();
    void read(CompoundData data);
}
