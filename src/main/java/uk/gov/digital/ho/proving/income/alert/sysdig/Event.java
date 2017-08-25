package uk.gov.digital.ho.proving.income.alert.sysdig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Event {
    private final String name;
    private final String description;
    private final String severity;
    @JsonIgnore
    private final String event_filter;
    private final Map<String, Object> tags;
}
