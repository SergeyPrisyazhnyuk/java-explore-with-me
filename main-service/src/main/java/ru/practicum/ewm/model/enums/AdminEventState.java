package ru.practicum.ewm.model.enums;

import java.util.Optional;

public enum AdminEventState {

    PUBLISH_EVENT, REJECT_EVEN;

    public static Optional<AdminEventState> getAdminEventState(String stateAction) {

        for (AdminEventState value : AdminEventState.values()) {
            if (value.name().equalsIgnoreCase(stateAction)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}
