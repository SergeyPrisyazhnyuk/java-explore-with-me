package ru.practicum.ewm.model.enums;

import java.util.Optional;

public enum UserEventState {

    SEND_TO_REVIEW, CANCEL_REVIEW;

    public static Optional<UserEventState> getUserEventState(String stateAction) {

        for (UserEventState value : UserEventState.values()) {
            if (value.name().equalsIgnoreCase(stateAction)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}
