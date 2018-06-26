/**
 * @(#)FinanceException.java, 2016年1月20日.
 *
 * Copyright (c) 2015, Youjie8 and/or its affiliates. All rights reserved.
 * Youjie8 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dangdang.sharding.test.util;


public class BusException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -694489771576921331L;
    
    private String code;

    public BusException() {
    }

    public BusException(String msg) {
        super(msg);
    }

    public BusException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
