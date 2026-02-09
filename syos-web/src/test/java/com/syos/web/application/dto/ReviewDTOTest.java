package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewDTOTest {

    private ReviewDTO reviewDTO;

    @BeforeEach
    public void setUp() {
        reviewDTO = new ReviewDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(reviewDTO);
    }

    @Test
    public void testSetAndGetReviewId() {
        reviewDTO.setReviewId(1);
        assertEquals(1, reviewDTO.getReviewId());
    }

    @Test
    public void testSetAndGetProductCode() {
        reviewDTO.setProductCode("P001");
        assertEquals("P001", reviewDTO.getProductCode());
    }

    @Test
    public void testSetAndGetProductName() {
        reviewDTO.setProductName("Product 1");
        assertEquals("Product 1", reviewDTO.getProductName());
    }

    @Test
    public void testSetAndGetUserId() {
        reviewDTO.setUserId("U001");
        assertEquals("U001", reviewDTO.getUserId());
    }

    @Test
    public void testSetAndGetUserName() {
        reviewDTO.setUserName("John Doe");
        assertEquals("John Doe", reviewDTO.getUserName());
    }

    @Test
    public void testSetAndGetRating() {
        reviewDTO.setRating(5);
        assertEquals(5, reviewDTO.getRating());
    }

    @Test
    public void testSetAndGetReviewText() {
        reviewDTO.setReviewText("Great product!");
        assertEquals("Great product!", reviewDTO.getReviewText());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        reviewDTO.setCreatedAt(now);
        assertEquals(now, reviewDTO.getCreatedAt());
    }

    @Test
    public void testSetAndGetUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        reviewDTO.setUpdatedAt(now);
        assertEquals(now, reviewDTO.getUpdatedAt());
    }

    @Test
    public void testSetAndGetIsVerifiedPurchase() {
        reviewDTO.setVerifiedPurchase(true);
        assertTrue(reviewDTO.isVerifiedPurchase());
    }

    @Test
    public void testNullValues() {
        reviewDTO.setReviewId(null);
        reviewDTO.setProductCode(null);
        reviewDTO.setUserId(null);

        assertNull(reviewDTO.getReviewId());
        assertNull(reviewDTO.getProductCode());
        assertNull(reviewDTO.getUserId());
    }

    @Test
    public void testMinRating() {
        reviewDTO.setRating(1);
        assertEquals(1, reviewDTO.getRating());
    }

    @Test
    public void testMaxRating() {
        reviewDTO.setRating(5);
        assertEquals(5, reviewDTO.getRating());
    }

    @Test
    public void testZeroRating() {
        reviewDTO.setRating(0);
        assertEquals(0, reviewDTO.getRating());
    }

    @Test
    public void testNegativeRating() {
        reviewDTO.setRating(-1);
        assertEquals(-1, reviewDTO.getRating());
    }

    @Test
    public void testRatingAboveMax() {
        reviewDTO.setRating(10);
        assertEquals(10, reviewDTO.getRating());
    }

    @Test
    public void testEmptyReviewText() {
        reviewDTO.setReviewText("");
        assertEquals("", reviewDTO.getReviewText());
    }

    @Test
    public void testLongReviewText() {
        String longText = "A".repeat(5000);
        reviewDTO.setReviewText(longText);
        assertEquals(5000, reviewDTO.getReviewText().length());
    }

    @Test
    public void testNullReviewText() {
        reviewDTO.setReviewText(null);
        assertNull(reviewDTO.getReviewText());
    }

    @Test
    public void testVerifiedPurchaseTrue() {
        reviewDTO.setVerifiedPurchase(true);
        assertTrue(reviewDTO.isVerifiedPurchase());
    }

    @Test
    public void testVerifiedPurchaseFalse() {
        reviewDTO.setVerifiedPurchase(false);
        assertFalse(reviewDTO.isVerifiedPurchase());
    }

    @Test
    public void testUpdatedAtAfterCreatedAt() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = created.plusHours(1);

        reviewDTO.setCreatedAt(created);
        reviewDTO.setUpdatedAt(updated);

        assertTrue(reviewDTO.getUpdatedAt().isAfter(reviewDTO.getCreatedAt()));
    }

    @Test
    public void testSpecialCharactersInReviewText() {
        reviewDTO.setReviewText("Great product! @#$%^&*()");
        assertEquals("Great product! @#$%^&*()", reviewDTO.getReviewText());
    }

    @Test
    public void testUnicodeCharactersInReviewText() {
        reviewDTO.setReviewText("Excelente producto! 很好的产品");
        assertEquals("Excelente producto! 很好的产品", reviewDTO.getReviewText());
    }

    @Test
    public void testCompleteReviewScenario() {
        LocalDateTime now = LocalDateTime.now();

        reviewDTO.setReviewId(1);
        reviewDTO.setProductCode("P001");
        reviewDTO.setProductName("Laptop");
        reviewDTO.setUserId("U001");
        reviewDTO.setUserName("John Doe");
        reviewDTO.setRating(5);
        reviewDTO.setReviewText("Excellent product!");
        reviewDTO.setCreatedAt(now);
        reviewDTO.setUpdatedAt(now);
        reviewDTO.setVerifiedPurchase(true);

        assertNotNull(reviewDTO.getReviewId());
        assertNotNull(reviewDTO.getProductCode());
        assertEquals(5, reviewDTO.getRating());
        assertTrue(reviewDTO.isVerifiedPurchase());
    }
}

