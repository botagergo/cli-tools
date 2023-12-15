package task_manager.util;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
public class RoundRobinUUIDGenerator implements UUIDGenerator {

    public RoundRobinUUIDGenerator(int number) {
        ArrayList<UUID> uuidList = new ArrayList<>();
        for (byte i = 0; i < number; i++) {
            uuidList.add(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(i).array()));
        }
        this.uuidList = uuidList;
    }

    @Inject
    public RoundRobinUUIDGenerator() {
        this(10);
    }

    @Override
    public UUID getUUID() {
        currInd = (currInd+1)%uuidList.size();
        return uuidList.get(currInd);
    }

    public UUID getUUID(int index) {
        return uuidList.get(index%uuidList.size());
    }

    @NonNull
    private final List<UUID> uuidList;
    private int currInd = -1;

}
