package com.poe2wiki.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResultTest {

    @Test
    void success_shouldHaveCode200() {
        ApiResult<String> result = ApiResult.success("hello");
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isEqualTo("hello");
    }

    @Test
    void error_shouldHaveGivenCodeAndMessage() {
        ApiResult<Void> result = ApiResult.error(400, "参数错误");
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("参数错误");
        assertThat(result.getData()).isNull();
    }

    @Test
    void fail_shouldHaveCode500() {
        ApiResult<Void> result = ApiResult.fail("服务器错误");
        assertThat(result.getCode()).isEqualTo(500);
    }
}
