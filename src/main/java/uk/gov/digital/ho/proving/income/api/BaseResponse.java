package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class BaseResponse {

    public BaseResponse(ResponseStatus status) {
        this.status = status;
    }

    public BaseResponse() {
        this.status = null;
    }

    @JsonInclude(Include.NON_NULL)
    private final ResponseStatus status;

    public ResponseStatus getStatus() {
        return status;
    }
}
