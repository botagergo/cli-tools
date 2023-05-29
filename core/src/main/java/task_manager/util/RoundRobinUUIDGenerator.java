package task_manager.util;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
public class RoundRobinUUIDGenerator implements UUIDGenerator {

    public RoundRobinUUIDGenerator() {
        this(List.of(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("44444444-4444-4444-4444-444444444444")
        ));
    }

    @Override
    public UUID getUUID() {
        currInd = (currInd+1)%uuidList.size();
        return uuidList.get(currInd);
    }

    @NonNull
    private final List<UUID> uuidList;
    private int currInd = -1;

}
