package com.ly;

public class InterruptTest {

    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            try{
                System.out.println("fuck    ");
                System.out.println("world   ");
            }catch (Exception e){
                System.out.println("hello");
            }finally {
                System.out.println("finally");
            }
        });
        thread.start();
        thread.interrupt();
    }
}
