package edu.bit.felinae;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;

public class Session {
    public SessionStatus status = SessionStatus.Queueing;
    public TransactionType transaction_type;
    public String username;
    public String password;
    public Transaction transaction;
    public String res;
    public double amount;
    public double balance;
    public static Session getSession(String session_id){
        Jedis jedis = new Jedis("localhost");
        String json = jedis.get(session_id);
        if(json == null) return null;
        return JSON.parseObject(json, Session.class);
    }
    public static void saveSession(String session_id, Session session) {
        Jedis jedis = new Jedis("localhost");
        String json = JSON.toJSONString(session);
        jedis.set(session_id, json);
    }

    @Override
    public String toString() {
        return "transaction: " + transaction.toString() + " transaction_type: " + transaction_type.toString() +
                " username: " +username + " password: " + password + " amount: " + amount + " status: " + status.toString();
    }
}

enum SessionStatus{
    Queueing,
    inTransaction,
    TransactionDone
}
enum Transaction{
    Deposit, Withdrawal, Register, BalanceInquery, Delete
}
enum TransactionType{
    account,
    general
}