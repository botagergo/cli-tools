package cli_tools.test_utils;

import cli_tools.common.util.UUIDGenerator;
import jakarta.inject.Inject;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.UUID;

public class RoundRobinUUIDGenerator implements UUIDGenerator {

    @Getter
    private UUID[] uuids;
    private final int number;
    private int currInd;

    public RoundRobinUUIDGenerator(int number) {
        this.number = number;
        reset();
    }

    @Inject
    public RoundRobinUUIDGenerator() {
        this(10);
    }

    @Override
    public UUID getUUID() {
        currInd = (currInd + 1) % uuids.length;
        return uuids[currInd];
    }

    public void reset() {
        uuids = new UUID[number];
        for (byte i = 0; i < number; i++) {
            uuids[i] = UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(i).array());
        }
        currInd = -1;
    }

}
