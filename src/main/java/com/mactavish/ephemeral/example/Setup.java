package com.mactavish.ephemeral.example;

public class Setup {
    public static void main(String[] args) throws Exception {
        new Thread(()-> {
            try {
                Hello.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(1000);
        new Client().start();
    }
}
