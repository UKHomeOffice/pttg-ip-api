package uk.gov.digital.ho.proving.income.alert.sysdig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message {
    private final Event event;
}
