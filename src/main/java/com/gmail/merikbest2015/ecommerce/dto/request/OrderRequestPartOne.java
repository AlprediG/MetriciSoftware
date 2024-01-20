package com.gmail.merikbest2015.ecommerce.dto.request;

import com.gmail.merikbest2015.ecommerce.constants.ErrorMessage;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class OrderRequestPartOne {
    private Long id;
    private Double totalPrice;
    private LocalDateTime date = LocalDateTime.now();

    @NotBlank(message = ErrorMessage.FILL_IN_THE_INPUT_FIELD)
    private String firstName;

    @NotBlank(message = ErrorMessage.FILL_IN_THE_INPUT_FIELD)
    private String lastName;
}
