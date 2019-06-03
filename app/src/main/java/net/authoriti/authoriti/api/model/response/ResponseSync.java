package net.authoriti.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

import net.authoriti.authoriti.api.model.AccountID;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseSync {

    @SerializedName("updates")
    private List<Sync> updates;

    public List<Sync> getUpdates() {
        return updates;
    }

    public void setUpdates(List<Sync> updates) {
        this.updates = updates;
    }

    public class Sync {

        @SerializedName("customerName")
        private String customerName;

        @SerializedName("userId")
        private String userId;

        @SerializedName("callAuth")
        private boolean callAuth;

        @SerializedName("callAuthNumber")
        private String callAuthNumber;

        @SerializedName("accounts")
        private List<AccountID> accounts;

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public List<AccountID> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<AccountID> accounts) {
            this.accounts = accounts;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isCallAuth() {
            return callAuth;
        }

        public void setCallAuth(boolean callAuth) {
            this.callAuth = callAuth;
        }

        public String getCallAuthNumber() {
            return callAuthNumber;
        }

        public void setCallAuthNumber(String callAuthNumber) {
            this.callAuthNumber = callAuthNumber;
        }
    }
}
