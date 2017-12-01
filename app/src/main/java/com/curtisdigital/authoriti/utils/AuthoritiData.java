package com.curtisdigital.authoriti.utils;

import com.curtisdigital.authoriti.api.model.Picker;

import org.androidannotations.annotations.EBean;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

@EBean(scope = EBean.Scope.Singleton)
public class AuthoritiData {

    private List<Picker> pickers;

    public List<Picker> getPickers() {
        return pickers;
    }

    public void setPickers(List<Picker> pickers) {
        this.pickers = pickers;
    }
}
