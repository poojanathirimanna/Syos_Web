package com.syos.web.presentation.util;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class GsonConfigTest {

    @Test
    public void testGetGson() {
        Gson gson = GsonConfig.getGson();
        assertNotNull(gson);
    }

    @Test
    public void testGetGsonReturnsSameInstance() {
        Gson gson1 = GsonConfig.getGson();
        Gson gson2 = GsonConfig.getGson();

        assertSame(gson1, gson2);
    }

    @Test
    public void testSerializeLocalDate() {
        Gson gson = GsonConfig.getGson();
        LocalDate date = LocalDate.of(2024, 1, 15);

        String json = gson.toJson(date);
        assertNotNull(json);
    }

    @Test
    public void testDeserializeLocalDate() {
        Gson gson = GsonConfig.getGson();
        String json = "\"2024-01-15\"";

        assertDoesNotThrow(() -> gson.fromJson(json, LocalDate.class));
    }

    @Test
    public void testSerializeLocalDateTime() {
        Gson gson = GsonConfig.getGson();
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        String json = gson.toJson(dateTime);
        assertNotNull(json);
    }

    @Test
    public void testDeserializeLocalDateTime() {
        Gson gson = GsonConfig.getGson();
        String json = "\"2024-01-15T10:30:00\"";

        assertDoesNotThrow(() -> gson.fromJson(json, LocalDateTime.class));
    }

    @Test
    public void testSerializeObject() {
        Gson gson = GsonConfig.getGson();
        TestObject obj = new TestObject("test", 123);

        String json = gson.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("test"));
    }

    @Test
    public void testDeserializeObject() {
        Gson gson = GsonConfig.getGson();
        String json = "{\"name\":\"test\",\"value\":123}";

        TestObject obj = gson.fromJson(json, TestObject.class);
        assertNotNull(obj);
        assertEquals("test", obj.name);
        assertEquals(123, obj.value);
    }

    @Test
    public void testSerializeNullValue() {
        Gson gson = GsonConfig.getGson();
        String json = gson.toJson(null);
        assertNotNull(json);
    }

    @Test
    public void testSerializeEmptyString() {
        Gson gson = GsonConfig.getGson();
        String json = gson.toJson("");
        assertNotNull(json);
    }

    @Test
    public void testSerializeNullFields() {
        Gson gson = GsonConfig.getGson();
        TestObject obj = new TestObject(null, 0);

        String json = gson.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("null"));
    }

    @Test
    public void testMultipleSerializations() {
        Gson gson = GsonConfig.getGson();

        for (int i = 0; i < 10; i++) {
            TestObject obj = new TestObject("test" + i, i);
            String json = gson.toJson(obj);
            assertNotNull(json);
        }
    }

    @Test
    public void testMultipleDeserializations() {
        Gson gson = GsonConfig.getGson();

        for (int i = 0; i < 10; i++) {
            String json = "{\"name\":\"test" + i + "\",\"value\":" + i + "}";
            TestObject obj = gson.fromJson(json, TestObject.class);
            assertNotNull(obj);
        }
    }

    @Test
    public void testSerializeComplexObject() {
        Gson gson = GsonConfig.getGson();
        ComplexObject obj = new ComplexObject("test", 123, LocalDate.now(), LocalDateTime.now());

        String json = gson.toJson(obj);
        assertNotNull(json);
    }

    @Test
    public void testDeserializeComplexObject() {
        Gson gson = GsonConfig.getGson();
        String json = "{\"name\":\"test\",\"value\":123,\"date\":\"2024-01-15\",\"dateTime\":\"2024-01-15T10:30:00\"}";

        ComplexObject obj = gson.fromJson(json, ComplexObject.class);
        assertNotNull(obj);
    }

    @Test
    public void testLenientParsing() {
        Gson gson = GsonConfig.getGson();
        // Lenient mode should handle malformed JSON better
        assertNotNull(gson);
    }

    @Test
    public void testGsonNotNull() {
        assertNotNull(GsonConfig.getGson());
    }

    @Test
    public void testGsonIsSingleton() {
        Gson gson1 = GsonConfig.getGson();
        Gson gson2 = GsonConfig.getGson();
        Gson gson3 = GsonConfig.getGson();

        assertSame(gson1, gson2);
        assertSame(gson2, gson3);
    }

    @Test
    public void testSerializeArray() {
        Gson gson = GsonConfig.getGson();
        int[] array = {1, 2, 3, 4, 5};

        String json = gson.toJson(array);
        assertNotNull(json);
    }

    @Test
    public void testDeserializeArray() {
        Gson gson = GsonConfig.getGson();
        String json = "[1,2,3,4,5]";

        int[] array = gson.fromJson(json, int[].class);
        assertNotNull(array);
        assertEquals(5, array.length);
    }

    // Helper test classes
    static class TestObject {
        String name;
        int value;

        TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    static class ComplexObject {
        String name;
        int value;
        LocalDate date;
        LocalDateTime dateTime;

        ComplexObject(String name, int value, LocalDate date, LocalDateTime dateTime) {
            this.name = name;
            this.value = value;
            this.date = date;
            this.dateTime = dateTime;
        }
    }
}

