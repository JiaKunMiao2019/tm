package com.szdq.tm.serverpurchase.po;

import com.szdq.tm.serverpurchase.enummeration.DeliverymanStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DeliverymanPO {
    private Integer id;
    private String name;
    private String district;
    private DeliverymanStatus status;
    private Date date;
}
