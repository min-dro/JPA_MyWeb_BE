package com.kh.myweb.common.template;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XssDefencePolicyTest {

    @Test
    void defence_shouldEscapeCommonHtmlCharacters() {
        String input = "<script>alert('x')</script> \"quoted\"";

        String result = XssDefencePolicy.defence(input);

        assertEquals("&lt;script&gt;alert(&apos;x&apos;)&lt;/script&gt; &quot;quoted&quot;", result);
    }

    @Test
    void defence_shouldLeaveSafeTextUnchanged() {
        String input = "Hello world";

        String result = XssDefencePolicy.defence(input);

        assertEquals("Hello world", result);
    }
}
