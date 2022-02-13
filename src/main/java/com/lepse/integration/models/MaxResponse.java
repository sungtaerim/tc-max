package com.lepse.integration.models;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.lepse.integrations.response.BaseResponse;

/**
 * Max response model
 */
@JsonRootName("response")
public class MaxResponse extends BaseResponse {

    public MaxResponse(Code code, Status status) {
        super(code, status);
    }
}
