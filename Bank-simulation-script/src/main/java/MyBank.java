import okhttp3.*;

import java.util.Random;
import java.util.Timer;
import java.util.concurrent.*;

public class MyBank {
    private static int id = 0, userid = 0, typeid = 0, account = 0;

    public static void main(String args[]) {
        System.out.println("Starting...");
        String myuser[] = {"Garvey", "Spencer", "Liz", "Felinae", "Userone", "Usertwo", "Userthree", "Userfour", "Userfive", "Usersix"};
        String mypassword[] = {"GarveyPassword", "GarveyPassword", "GarveyPassword", "GarveyPassword", "666", "666", "666", "666", "666", "666"};
        String mytype[] = {"query", "withdrawal", "deposit"};
        int num = myuser.length;

        Random random = new Random();

        for (int i = 0; i < 3; i++) {  // i < num of transaction

            userid = random.nextInt(num);
            typeid = random.nextInt(3);
            account = random.nextInt(20000);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FutureTask<String> future = new FutureTask<String>(new Callable<String>() {

                @Override
                public String call() throws Exception {
//                    Thread.sleep(10000);
                    if (typeid == 0) {
                        QueryTransaction querytransaction = new QueryTransaction(id, myuser[userid], mypassword[userid], "query");
                        querytransaction.start();
                    }
                    else {
                        Transaction transaction = new Transaction(id, myuser[userid], mypassword[userid], mytype[typeid], account);
                        transaction.start();
                    }
                    return "start";
                }
            });
            executor.execute(future);
            try {
                String result = future.get(5000, TimeUnit.MILLISECONDS);
                System.out.println(result);
            } catch (InterruptedException e) {
                System.out.println("Error!\n");
//                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("Error!\n");
//                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("Time out!\n");
//                e.printStackTrace();
            } finally {
                future.cancel(true);
                executor.shutdown();
            }
            id++;
        }

    }
}
