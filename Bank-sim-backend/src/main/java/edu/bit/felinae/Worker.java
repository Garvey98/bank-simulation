package edu.bit.felinae;


import java.util.Random;

public class Worker extends Thread{
    private int num;
    public Worker(){
        super();
        this.num = 1;
    }
    public Worker(int num){
        super();
        this.num = num;
    }
    @Override
    public void run() {
        MainQueue queue = MainQueue.getInstance();
        Database db = Database.getInstance();
        String session_id;
        while (true) {
            do {
                session_id = queue.poll();
            } while (!queue.getShutdown() && session_id == null);
            // if (session_id != null && queue.getShutdown()) break;
            Session session = Session.getSession(session_id);
            if(session == null) {
                System.out.println("fail to get session from redis");
            }
            System.out.println("[INFO] Worker " + num + " Get session from queue: " + session);
            session.status = SessionStatus.inTransaction;
            Session.saveSession(session_id, session);
            if (session.transaction_type == TransactionType.account) {
                switch (session.transaction) {
                    case Deposit:
                        break;
                    case Withdrawal:
                        break;
                    case Register:
                        if (db.register(session.username, session.password)) {
                            session.res = "success";
                        } else {
                            session.res = "fail";
                        }
                        break;
                    case Delete:
                        if (db.checkCreditial(session.username, session.password)) {
                            if(db.delete(session.username)){
                                session.res = "success";
                            } else {
                                session.res = "fail";
                            }
                        } else {
                            session.res = "password error";
                        }
                        break;
                }
            } else {
                switch (session.transaction) {
                    case BalanceInquery:
                        if (db.checkCreditial(session.username, session.password)) {
                            session.res = "success";
                            session.balance = db.checkBalance(session.username);
                        } else {
                            session.res = "password error";
                        }

                        break;
                    case Deposit:
                        if (db.checkCreditial(session.username, session.password)) {
                            if (db.deposit(session.username, session.amount)) {
                                session.res = "success";
                            } else {
                                session.res = "fail";
                            }
                            session.balance = db.checkBalance(session.username);
                        } else {
                            session.res = "password error";
                        }

                        break;
                    case Withdrawal:
                        if (db.checkCreditial(session.username, session.password)) {
                            if (db.withdrawal(session.username, session.amount)) {
                                session.res = "success";
                            } else {
                                session.res = "fail";
                            }
                            session.balance = db.checkBalance(session.username);
                        } else {
                            session.res = "password error";
                        }
                        break;
                }
            }
            System.out.println("[INFO] Worker " + num + " Process Done, wait random time");
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(2000) + 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            session.status = SessionStatus.TransactionDone;
            Session.saveSession(session_id, session);
            System.out.println("[INFO] Worker " + num + " Wait Done, save result");
            if (queue.getShutdown()) break;
        }
        System.out.println("worker died");
    }
}
