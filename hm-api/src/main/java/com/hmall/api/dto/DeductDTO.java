package com.hmall.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "余额扣减请求DTO")
public class DeductDTO {
    @ApiModelProperty("支付密码")
    private String pw;
    @ApiModelProperty("支付金额")
    private Integer amount;
}
