import okhttp3.*;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

// query
public class QueryTransaction extends Thread {
    private String UserName;
    private String Password;
    private String TransactionType;
    private int tid;
    private String myfile = "log.txt";
    private static String requestUrl = "http://bk.felinae98.cn:8001/";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public QueryTransaction(int id, String myUser, String myPassword, String myType) {
        this.tid = id;
        this.UserName = myUser;
        this.Password = myPassword;
        this.TransactionType = myType;
    }

    public void run() {
        OkHttpClient okHttpClient = new OkHttpClient();
        int mystatus = 0;
        JSONObject requestData = new JSONObject()
//                .put("transactionType", "account")
                .put("transactionType", "general")
                .put("account", new JSONObject()
                        .put("username", this.UserName)
                        .put("password", this.Password))
                .put("transactions", new JSONObject()
                        .put("type", this.TransactionType));
//                          .put("type", "register"));

        System.out.println(requestData.toString());

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
            System.out.println(this.tid + " Success: \n" + mytoken);
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
            System.out.println(this.tid + " " + myurl + " " + mystatus);

            Request request1 = new Request.Builder()
                    .url(myurl)
                    .build();

            response = null;
            try {
                response = okHttpClient.newCall(request1).execute();
                String statusresult = response.body().string();
                if (statusresult.equals("TransactionDone")) {
                    mystatus = 2;
                    System.out.println(this.tid + " " + statusresult);
                    break;
                }
                System.out.println(this.tid + " " + statusresult);
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
            System.out.println(this.tid + " " + myurl + "  " + mystatus);

            Request request2 = new Request.Builder()
                    .url(myurl)
                    .build();

            response = null;
            try {
                response = okHttpClient.newCall(request2).execute();
                mystatus = 0;
                String myresult = response.body().string();
                System.out.println(this.tid + " SuccessALL: \n" + myresult);
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
}
