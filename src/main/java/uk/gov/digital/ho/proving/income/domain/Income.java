package uk.gov.digital.ho.proving.income.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class Income {
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private LocalDate payDate;
    private String employer;
    private String income;
}
