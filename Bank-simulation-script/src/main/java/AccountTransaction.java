import okhttp3.*;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.*;

//register or delete
public class AccountTransaction {
    private static String myname = "", mypassword = "";
    private static String requestUrl = "http://bk.felinae98.cn:8001/";
    private static String myfile = "log.txt";
    private static int id = 0;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void Myregister(String name, String password, String type) {
        OkHttpClient okHttpClient = new OkHttpClient();
        int mystatus = 0;
        JSONObject requestData = new JSONObject()
                .put("transactionType", "account")
                .put("account", new JSONObject()
                        .put("username", name)
                        .put("password", password))
                .put("transactions", new JSONObject()
                          .put("type", type));

//        System.out.println(requestData.toString());

        RequestBody body = RequestBody.create(JSON, requestData.toString());

        Request request = new Request
                .Builder()
                .url(requestUrl + "queue")
                .post(body)
                .build();

        Response response = null;
        String mytoken = "";
        try {
            response = okHttpClient.newCall(request).execute();
            mystatus = 1;
            mytoken = response.body().string();
//            System.out.println("Success: \n" + mytoken);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        while (mystatus == 1) {
            HttpUrl.Builder urlbuilder = HttpUrl.parse(requestUrl + "status").newBuilder();
            urlbuilder.addQueryParameter("session", mytoken);
            String myurl = urlbuilder.build().toString();
//            System.out.println(myurl + " " + mystatus);

            Request request1 = new Request.Builder()
                    .url(myurl)
                    .build();

            response = null;
            try {
                response = okHttpClient.newCall(request1).execute();
                String statusresult = response.body().string();
                if (statusresult.equals("TransactionDone")) {
                    mystatus = 2;
//                    System.out.println("Success: \n" + statusresult);
                    break;
                }
//                System.out.println("Success: \n" + statusresult);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.body().close();
                }
            }
        }
        if (mystatus == 2) {
            HttpUrl.Builder urlbuilder = HttpUrl.parse(requestUrl + "result").newBuilder();
            urlbuilder.addQueryParameter("session", mytoken);
            String myurl = urlbuilder.build().toString();
//            System.out.println(myurl + "  " + mystatus);

            Request request2 = new Request.Builder()
                    .url(myurl)
                    .build();

            response = null;
            try {
                response = okHttpClient.newCall(request2).execute();
                mystatus = 0;
                String myresult = response.body().string();
                System.out.println("SuccessALL: \n" + myresult);
                BufferedWriter out = null;
                try {
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myfile, true)));
                    out.write(requestData.toString());
                    out.newLine();
                    out.write(myresult);
                    out.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.body().close();
                }
            }
        }
    }

    public static void main(String[] args) {
        String myUser[] = {"Garvey", "Spencer", "Liz", "Felinae", "Userone", "Usertwo", "Userthree", "Userfour", "Userfive", "Usersix"};
        String myPassword[] = {"GarveyPassword", "GarveyPassword", "GarveyPassword", "GarveyPassword", "666", "666", "666", "666", "666", "666"};
        String myType[] = {"register", "delete"};
        int len = myUser.length;
        for (id = 0; id < len; id++) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
//                    Thread.sleep(10000);
                  Myregister(myUser[id], myPassword[id], myType[0]);  //register myType[0] or delete myType[1]
                    return "register or delete over";
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
        }
    }
}
