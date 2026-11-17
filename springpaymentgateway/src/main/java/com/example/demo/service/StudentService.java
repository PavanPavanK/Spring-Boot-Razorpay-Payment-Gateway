
package com.example.demo.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.StudentOrder;
import com.example.demo.repo.StudentOrderRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class StudentService {

    private final StudentOrderRepo studentrepo;
    private final RazorpayClient client;
    private final String razorPaySecret;

    public StudentService(
            StudentOrderRepo studentrepo,
            @Value("${razorpay.key.id}") String razorPayKey,
            @Value("${razorpay.secret.key}") String razorPaySecret
    ) throws Exception {

        this.studentrepo = studentrepo;
        this.razorPaySecret = razorPaySecret;

        this.client = new RazorpayClient(
                razorPayKey,
                razorPaySecret
        );
    }

    // Create Razorpay Order
    public StudentOrder createOrder(StudentOrder stuOrder)
            throws Exception {

        if (stuOrder.getAmount() == null ||
                stuOrder.getAmount() <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }

        JSONObject orderReq = new JSONObject();

        orderReq.put(
                "amount",
                Math.multiplyExact(stuOrder.getAmount(), 100)
        );

        orderReq.put("currency", "INR");
        orderReq.put("receipt", stuOrder.getEmail());

        Order razorPayOrder = client.orders.create(orderReq);

        stuOrder.setRzorpayOrderId(
                razorPayOrder.get("id")
        );

        stuOrder.setOrderStatus(
                razorPayOrder.get("status")
        );

        studentrepo.save(stuOrder);

        return stuOrder;
    }

    // Verify Razorpay Payment
    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) throws Exception {

        JSONObject options = new JSONObject();

        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        return Utils.verifyPaymentSignature(
                options,
                razorPaySecret
        );
    }
}
