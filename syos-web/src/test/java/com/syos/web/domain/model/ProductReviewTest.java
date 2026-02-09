package com.syos.web.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ProductReviewTest {

    private ProductReview review;

    @BeforeEach
    public void setUp() {
        review = new ProductReview();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(review);
    }

    @Test
    public void testConstructorWithoutId() {
        ProductReview r = new ProductReview("P001", "U001", 5, "Great product!", true);
        assertEquals("P001", r.getProductCode());
        assertEquals("U001", r.getUserId());
        assertEquals(5, r.getRating());
        assertEquals("Great product!", r.getReviewText());
        assertTrue(r.isVerifiedPurchase());
    }

    @Test
    public void testFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ProductReview r = new ProductReview(1, "P001", "U001", 5, "Great!", now, now, true);
        assertEquals(1, r.getReviewId());
        assertEquals("P001", r.getProductCode());
        assertEquals("U001", r.getUserId());
        assertEquals(5, r.getRating());
    }

    @Test
    public void testSetAndGetReviewId() {
        review.setReviewId(1);
        assertEquals(1, review.getReviewId());
    }

    @Test
    public void testSetAndGetProductCode() {
        review.setProductCode("P001");
        assertEquals("P001", review.getProductCode());
    }

    @Test
    public void testSetAndGetUserId() {
        review.setUserId("U001");
        assertEquals("U001", review.getUserId());
    }

    @Test
    public void testSetAndGetRating() {
        review.setRating(5);
        assertEquals(5, review.getRating());
    }

    @Test
    public void testSetAndGetReviewText() {
        review.setReviewText("Excellent!");
        assertEquals("Excellent!", review.getReviewText());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        review.setCreatedAt(now);
        assertEquals(now, review.getCreatedAt());
    }

    @Test
    public void testSetAndGetUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        review.setUpdatedAt(now);
        assertEquals(now, review.getUpdatedAt());
    }

    @Test
    public void testSetAndGetVerifiedPurchase() {
        review.setVerifiedPurchase(true);
        assertTrue(review.isVerifiedPurchase());
    }

    @Test
    public void testValidateSuccess() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(5);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        review.setProductCode(null);
        review.setUserId("U001");
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        review.setProductCode("");
        review.setUserId("U001");
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        review.setProductCode("   ");
        review.setUserId("U001");
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateNullUserId() {
        review.setProductCode("P001");
        review.setUserId(null);
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateEmptyUserId() {
        review.setProductCode("P001");
        review.setUserId("");
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateWhitespaceUserId() {
        review.setProductCode("P001");
        review.setUserId("   ");
        review.setRating(5);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateRatingZero() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(0);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateRatingSix() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(6);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateNegativeRating() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(-1);
        assertThrows(IllegalArgumentException.class, () -> review.validate());
    }

    @Test
    public void testValidateRatingOne() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(1);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testValidateRatingFive() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(5);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testAllRatingsValid() {
        for (int rating = 1; rating <= 5; rating++) {
            review.setProductCode("P001");
            review.setUserId("U001");
            review.setRating(rating);
            assertDoesNotThrow(() -> review.validate());
        }
    }

    @Test
    public void testNullReviewText() {
        review.setReviewText(null);
        assertNull(review.getReviewText());
    }

    @Test
    public void testEmptyReviewText() {
        review.setReviewText("");
        assertEquals("", review.getReviewText());
    }

    @Test
    public void testLongReviewText() {
        String longText = "A".repeat(5000);
        review.setReviewText(longText);
        assertEquals(5000, review.getReviewText().length());
    }

    @Test
    public void testReviewTextWithSpecialChars() {
        review.setReviewText("Great! @#$%");
        assertEquals("Great! @#$%", review.getReviewText());
    }

    @Test
    public void testReviewTextWithUnicode() {
        review.setReviewText("优秀的产品 Отлично!");
        assertEquals("优秀的产品 Отлично!", review.getReviewText());
    }

    @Test
    public void testReviewTextWithNewlines() {
        review.setReviewText("Good product.\nVery satisfied.");
        assertTrue(review.getReviewText().contains("\n"));
    }

    @Test
    public void testVerifiedPurchaseTrue() {
        review.setVerifiedPurchase(true);
        assertTrue(review.isVerifiedPurchase());
    }

    @Test
    public void testVerifiedPurchaseFalse() {
        review.setVerifiedPurchase(false);
        assertFalse(review.isVerifiedPurchase());
    }

    @Test
    public void testTimestamps() {
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        LocalDateTime updated = LocalDateTime.now();

        review.setCreatedAt(created);
        review.setUpdatedAt(updated);

        assertTrue(review.getCreatedAt().isBefore(review.getUpdatedAt()));
    }

    @Test
    public void testCompleteReview() {
        LocalDateTime now = LocalDateTime.now();
        review.setReviewId(1);
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(5);
        review.setReviewText("Excellent product!");
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        review.setVerifiedPurchase(true);

        assertNotNull(review.getReviewId());
        assertNotNull(review.getProductCode());
        assertNotNull(review.getUserId());
        assertTrue(review.getRating() >= 1 && review.getRating() <= 5);
        assertNotNull(review.getReviewText());
        assertTrue(review.isVerifiedPurchase());
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testNullReviewId() {
        review.setReviewId(null);
        assertNull(review.getReviewId());
    }

    @Test
    public void testLargeReviewId() {
        review.setReviewId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, review.getReviewId());
    }

    @Test
    public void testNullTimestamps() {
        review.setCreatedAt(null);
        review.setUpdatedAt(null);
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }

    @Test
    public void testMultipleValidations() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(5);

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> review.validate());
        }
    }

    @Test
    public void testRatingTwo() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(2);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testRatingThree() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(3);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testRatingFour() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(4);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testShortReviewText() {
        review.setReviewText("OK");
        assertEquals("OK", review.getReviewText());
    }

    @Test
    public void testSingleCharReviewText() {
        review.setReviewText("A");
        assertEquals("A", review.getReviewText());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        review.setProductCode(longCode);
        review.setUserId("U001");
        review.setRating(5);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testLongUserId() {
        String longUserId = "U" + "0".repeat(100);
        review.setProductCode("P001");
        review.setUserId(longUserId);
        review.setRating(5);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testPastTimestamp() {
        LocalDateTime past = LocalDateTime.now().minusYears(1);
        review.setCreatedAt(past);
        assertEquals(past, review.getCreatedAt());
    }

    @Test
    public void testFutureTimestamp() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        review.setUpdatedAt(future);
        assertEquals(future, review.getUpdatedAt());
    }

    @Test
    public void testSameTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }

    @Test
    public void testVerifiedPurchaseWithLowRating() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(1);
        review.setVerifiedPurchase(true);
        assertDoesNotThrow(() -> review.validate());
    }

    @Test
    public void testUnverifiedPurchaseWithHighRating() {
        review.setProductCode("P001");
        review.setUserId("U001");
        review.setRating(5);
        review.setVerifiedPurchase(false);
        assertDoesNotThrow(() -> review.validate());
    }
}

