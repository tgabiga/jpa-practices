package org.gbg.tutorials.jpadissected;

import java.util.List;
import java.util.UUID;

public interface TestData {

    /**
     * Courts
     */
    UUID FIRST_COURT_ID = UUID.fromString("10969772-192d-40c1-b3a3-98f15db85d2e");
    UUID SECOND_COURT_ID = UUID.fromString("e63ee907-afb8-4b5c-be70-ce54994cb266");

    /**
     * Players
     */
    UUID FIRST_PLAYER_ID = UUID.fromString("11610ec3-9187-4bcc-a0f6-39fa8437677d");
    UUID SECOND_PLAYER_ID = UUID.fromString("f4f016e8-7402-4b40-95c1-fb4ffa798a59");
    UUID THIRD_PLAYER_ID = UUID.fromString("e97537d2-4104-4633-88b4-181624873f64");
    UUID FOURTH_PLAYER_ID = UUID.fromString("5e95f04b-187f-4023-9754-808d4252bca4");

    List<UUID> ALL_PLAYER_IDS = List.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID, THIRD_PLAYER_ID, FOURTH_PLAYER_ID);
}
