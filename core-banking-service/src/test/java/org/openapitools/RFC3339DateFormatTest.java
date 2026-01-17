package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

class RFC3339DateFormatTest {

    @Test
    void parseAndFormatUseUtcTimezone() {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date instant = Date.from(Instant.parse("2024-05-01T12:34:56Z"));

        StringBuffer buffer = format.format(instant, new StringBuffer(), new FieldPosition(0));
        assertEquals("2024-05-01T12:34:56.000+00:00", buffer.toString());

        Date parsed = format.parse(buffer.toString(), new ParsePosition(0));
        assertEquals(instant, parsed);
    }

    @Test
    void cloneReturnsSameFormatterInstance() {
        RFC3339DateFormat format = new RFC3339DateFormat();
        assertSame(format, format.clone());
    }
}
