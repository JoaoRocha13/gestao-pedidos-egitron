package com.egitron.gestaopedidos.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class UpdateOrderDTO {

    @Size(max = 120)
    private String clientName;

    @Email
    @Size(max = 180)
    private String clientEmail;


    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    /** State transition is explicit here */
    @Size(max = 30)
    private String status; // PENDING/APPROVED/REJECTED

    public UpdateOrderDTO() {}

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
