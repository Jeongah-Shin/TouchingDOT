package com.dotincorp.watchservice;

import java.util.HashMap;
import java.util.UUID;

/**
 * GATT Attributes of DOT Service
 */

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();

    public static final String SERVICE_DOT = "0000ff00-0000-1000-8000-00805f9b34fb";
    public static final String ATTR_INIT_NOTI_RECV = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String ATTR_INIT_NOTI_SEND = "0000ff02-0000-1000-8000-00805f9b34fb";
    public static final String ATTR_MSG_RECV = "0000ff03-0000-1000-8000-00805f9b34fb";
    public static final String ATTR_MSG_SEND = "0000ff04-0000-1000-8000-00805f9b34fb";

    // descriptor
    // 프로토콜에는 없지만 ATTR_INIT_NOTI_RECV 안의 descriptor를 확인했을 때
    // 이러한 값이 들어 있음을 확인함
    public static final String DESC_NOTI_DESC = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Dot service
        attributes.put(SERVICE_DOT, "Dot Service ID");

        // Dot
        attributes.put(ATTR_INIT_NOTI_SEND, "ATTR_INIT_NOTI_SEND");
        attributes.put(ATTR_INIT_NOTI_RECV, "ATTR_INIT_NOTI_RECV");
        attributes.put(ATTR_MSG_SEND, "ATTR_MSG_SEND");
        attributes.put(ATTR_MSG_RECV, "ATTR_MSG_RECV");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static UUID getUUID(String uuidString) {
        return UUID.fromString(uuidString);
    }
}
