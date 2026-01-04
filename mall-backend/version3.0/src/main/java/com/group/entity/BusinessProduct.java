
package com.group.entity;

import lombok.Data;

@Data
public class BusinessProduct {
    private Integer bpid;    // 关联ID
    private Integer bid;     // 商家ID
    private Integer pid;     // 商品ID
}