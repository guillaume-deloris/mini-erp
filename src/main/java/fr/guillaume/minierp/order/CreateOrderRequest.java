package fr.guillaume.minierp.order;

import java.util.List;

/**
 * JSON body for an order creation:
 * {"reference": "CMD001", "lines": [{"productId": 1, "quantity": 10}]}
 */
public record CreateOrderRequest(String reference, List<LineRequest> lines) {

    public record LineRequest(Long productId, int quantity) {
    }
}