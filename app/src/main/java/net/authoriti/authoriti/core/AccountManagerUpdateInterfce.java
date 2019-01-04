package net.authoriti.authoriti.core;

public interface AccountManagerUpdateInterfce {
    void deleted(String accountId);

    void addSelfSigned();

    void syncId(String ID);
}
