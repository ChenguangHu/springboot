package com.geb.ext;
//分页
import java.io.Serializable;

public class Pagination<D extends Object> implements Serializable {

    private int pageSize;
    private int pageNum;
    private int count;
    private int maxPageNum;
    private D data;

    public Pagination() {
    }

    public Pagination(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public Pagination(int pageSize, int pageNum,int count) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.count = count;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }

    public void setMaxPageNum(int maxPageNum) {
        this.maxPageNum = maxPageNum;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

}
