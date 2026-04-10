package org.ping_me.websocket;

/**
 * Chứa tên channel Redis và các destination WebSocket dùng chung.
 */
public final class WebSocketDestinations {

    // Channel Redis dùng để đồng bộ message WebSocket giữa các instance
    public static final String REDIS_SYNC_CHANNEL = "pingme-ws-sync";

    // Queue room event theo user: /user/{id}/queue/rooms
    public static final String USER_QUEUE_ROOMS_SUFFIX = "/queue/rooms";

    // Queue friendship event theo user: /user/{id}/queue/friendship
    public static final String USER_QUEUE_FRIENDSHIP_SUFFIX = "/queue/friendship";

    // Queue trạng thái online/offline theo user: /user/{id}/queue/status
    public static final String USER_QUEUE_STATUS_SUFFIX = "/queue/status";

    // Queue signaling call theo user: /user/{id}/queue/signaling
    public static final String USER_QUEUE_SIGNALING_SUFFIX = "/queue/signaling";

    // Queue update title AI chat theo user: /user/{id}/queue/title-update
    public static final String USER_QUEUE_TITLE_UPDATE_SUFFIX = "/queue/title-update";

    private WebSocketDestinations() {
    }

    // Topic message của room: /topic/rooms/{roomId}/messages
    public static String roomMessagesTopic(Long roomId) {
        return "/topic/rooms/" + roomId + "/messages";
    }

    // Topic typing của room: /topic/rooms/{roomId}/typing
    public static String roomTypingTopic(Long roomId) {
        return "/topic/rooms/" + roomId + "/typing";
    }

    // Build full queue room event theo user
    public static String userQueueRooms(Long userId) {
        return "/user/" + userId + USER_QUEUE_ROOMS_SUFFIX;
    }

    // Build full queue friendship event theo user
    public static String userQueueFriendship(Long userId) {
        return "/user/" + userId + USER_QUEUE_FRIENDSHIP_SUFFIX;
    }
}
