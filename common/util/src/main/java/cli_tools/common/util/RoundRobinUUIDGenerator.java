package cli_tools.common.util;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
public class RoundRobinUUIDGenerator implements UUIDGenerator {

    @Getter
    private final UUID[] uuids;
    private int currInd = -1;

    public RoundRobinUUIDGenerator(int number) {
        this.uuids = new UUID[number];
        for (byte i = 0; i < number; i++) {
            uuids[i] = UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(i).array());
        }
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

}
