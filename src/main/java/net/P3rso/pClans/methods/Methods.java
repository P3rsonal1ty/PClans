package net.P3rso.pClans.methods;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Methods {

    public static List<UUID> StringToUUIDs(String uuids){
        if (uuids == null || uuids.isEmpty()) return Collections.emptyList();

        return Arrays.stream(uuids.split(","))
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
    public static String listToString(List<UUID> list) {
        return list.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));
    }
}
