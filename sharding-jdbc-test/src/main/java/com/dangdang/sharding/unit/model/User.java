package com.dangdang.sharding.unit.model;

import java.util.Date;

public class User {
    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Long age;

    /**
     *
     */
    private String remark;

    /**
     *
     */
    private String companyCode;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    public Long getId() {
        return id;
    }

    /**
     *
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     *
     */
    public Long getAge() {
        return age;
    }

    /**
     *
     */
    public void setAge(Long age) {
        this.age = age;
    }

    /**
     *
     */
    public String getRemark() {
        return remark;
    }

    /**
     *
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     *
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     *
     */
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode == null ? null : companyCode.trim();
    }

    /**
     *
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     *
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}