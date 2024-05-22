package com.lrl.liudrivecore.service.stream;

public class TcpStreamCenter {
    public static void main(String[] args) throws InterruptedException {
        TcpServerEnhanced tcp = new TcpServerEnhanced();
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcp.run();
            }
        }).start();

        System.out.println("Phase 1");

        Thread.sleep(5000);
        System.out.println("Phase 2");
        tcp.attachOutputStream(System.out);

        Thread.sleep(5000);
        System.out.println("Phase 3");
        tcp.detachOutputStream();

        Thread.sleep(5000);
        System.out.println("Phase 4");

    }
}
