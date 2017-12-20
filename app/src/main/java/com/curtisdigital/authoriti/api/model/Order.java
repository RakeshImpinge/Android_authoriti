package com.curtisdigital.authoriti.api.model;

import java.util.List;

/**
 * Created by mac on 12/20/17.
 */

public class Order {

    private List<String> pickers;

    public List<String> getPickers() {
        return pickers;
    }

    public void setPickers(List<String> pickers) {
        this.pickers = pickers;
    }
}
